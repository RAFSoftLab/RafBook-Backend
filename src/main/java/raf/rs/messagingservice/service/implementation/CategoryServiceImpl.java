package raf.rs.messagingservice.service.implementation;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.dto.BulkImportCategoriesDTO;
import raf.rs.messagingservice.dto.NewCategoryDTO;
import raf.rs.messagingservice.exception.AlreadyExistsException;
import raf.rs.messagingservice.exception.StudiesNotFoundException;
import raf.rs.messagingservice.mapper.CategoryMapper;
import raf.rs.messagingservice.model.Category;
import raf.rs.messagingservice.model.Studies;
import raf.rs.messagingservice.model.StudyProgram;
import raf.rs.messagingservice.repository.CategoryRepository;
import raf.rs.messagingservice.repository.StudiesRepository;
import raf.rs.messagingservice.repository.StudyProgramRepository;
import raf.rs.messagingservice.service.CategoryService;
import raf.rs.userservice.exception.ForbiddenActionException;
import raf.rs.userservice.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private CategoryMapper categoryMapper;
    private CategoryRepository categoryRepository;
    private UserService userService;
    private StudiesRepository studiesRepository;
    private StudyProgramRepository studyProgramRepository;

    private final Counter categoryAddCounter;
    private final Timer categoryAddTimer;
    private final Gauge activeCategoriesGauge;

    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper, CategoryRepository categoryRepository,
                               UserService userService, StudiesRepository studiesRepository,
                               StudyProgramRepository studyProgramRepository,
                               io.micrometer.core.instrument.MeterRegistry meterRegistry) {
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.studiesRepository = studiesRepository;
        this.studyProgramRepository = studyProgramRepository;

        this.categoryAddCounter = meterRegistry.counter("category.add.counter");
        this.categoryAddTimer = meterRegistry.timer("category.add.timer");
        this.activeCategoriesGauge = Gauge.builder("category.active.count", categoryRepository::count)
                .description("Total number of active categories")
                .register(meterRegistry);
    }

    @Override
    public List<String> getAllCategoryNames(String studiesName, String studyProgramName) {
        log.info("Entering getAllCategoryNames with studiesName: {}, studyProgramName: {}", studiesName, studyProgramName);

        categoryAddCounter.increment();

        Studies studies = studiesRepository.findByNameIgnoreCase(studiesName)
                .orElseThrow(() -> new StudiesNotFoundException("There is not studies with name " + studiesName));

        boolean exists = false;
        StudyProgram studyProgramFound = null;

        for (StudyProgram studyProgram : studies.getStudyPrograms()) {
            if (studyProgram.getName().equalsIgnoreCase(studyProgramName)) {
                exists = true;
                studyProgramFound = studyProgram;
            }
        }

        if (!exists) {
            throw new StudiesNotFoundException("Study program with name " + studyProgramName + " not found");
        }

        List<String> categoryNames = studyProgramFound.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        log.info("Exiting getAllCategoryNames with result: {}", categoryNames);
        return categoryNames;
    }

    @Override
    public void addCategory(NewCategoryDTO newCategoryDTO, String token) {
        log.info("Entering addCategory with newCategoryDTO: {}, token: {}", newCategoryDTO, token);

        categoryAddTimer.record(() -> {
            String username = userService.getUserByToken(token).getUsername();
            Set<String> roles = userService.getUserRoles(username);

            if (!roles.contains("ADMIN")) {
                throw new ForbiddenActionException("You are not authorized for this action!");
            }

            Studies studies = studiesRepository.findByNameIgnoreCase(newCategoryDTO.getStudies())
                    .orElseThrow(() -> new StudiesNotFoundException("There is no studies with name " + newCategoryDTO.getStudies()));

            String categoryName = newCategoryDTO.getName();
            for (StudyProgram studyProgram : studies.getStudyPrograms()) {
                if (newCategoryDTO.getStudyProgram().equalsIgnoreCase(studyProgram.getName())) {
                    for (Category category : studyProgram.getCategories()) {
                        if (category.getName().equalsIgnoreCase(categoryName)) {
                            throw new AlreadyExistsException("Category with name " + categoryName + " already exists");
                        }
                    }

                    Category category = categoryMapper.toEntity(newCategoryDTO);
                    Category savedCategory = categoryRepository.save(category);
                    studyProgram.getCategories().add(savedCategory);
                    studyProgramRepository.save(studyProgram);
                }
            }
        });

        log.info("Exiting addCategory");
    }

    @Override
    public void addCategories(BulkImportCategoriesDTO bulkImportCategoriesDTO, String token) {
        log.info("Entering addCategories with bulkImportCategoriesDTO: {}, token: {}", bulkImportCategoriesDTO, token);

        categoryAddTimer.record(() -> {
            String username = userService.getUserByToken(token).getUsername();
            Set<String> roles = userService.getUserRoles(username);

            if (!roles.contains("ADMIN")) {
                throw new ForbiddenActionException("You are not authorized for this action!");
            }

            for (NewCategoryDTO newCategoryDTO : bulkImportCategoriesDTO.getCategories()) {
                Studies studies = studiesRepository.findByNameIgnoreCase(newCategoryDTO.getStudies())
                        .orElseThrow(() -> new StudiesNotFoundException("There is no studies with name " + newCategoryDTO.getStudies()));

                String categoryName = newCategoryDTO.getName();
                for (StudyProgram studyProgram : studies.getStudyPrograms()) {
                    if (newCategoryDTO.getStudyProgram().equalsIgnoreCase(studyProgram.getName())) {
                        for (Category category : studyProgram.getCategories()) {
                            if (category.getName().equalsIgnoreCase(categoryName)) {
                                throw new AlreadyExistsException("Category with name " + categoryName + " already exists");
                            }
                        }

                        Category category = categoryMapper.toEntity(newCategoryDTO);
                        Category savedCategory = categoryRepository.save(category);
                        studyProgram.getCategories().add(savedCategory);
                        studyProgramRepository.save(studyProgram);
                    }
                }
            }
        });

        log.info("Exiting addCategories");
    }
}