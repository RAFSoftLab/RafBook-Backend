package raf.rs.messagingservice.service.implementation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.dto.BulkImportCategoriesDTO;
import raf.rs.messagingservice.dto.CategoryDTO;
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
import raf.rs.userservice.model.MyUser;
import raf.rs.userservice.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private CategoryMapper categoryMapper;
    private CategoryRepository categoryRepository;
    private UserService userService;
    private StudiesRepository studiesRepository;
    private StudyProgramRepository studyProgramRepository;

    @Override
    public List<String> getAllCategoryNames(String studiesName, String studyProgramName) {

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
            throw new StudiesNotFoundException("Study program with name " + studyProgramName +" not foumd");
        }


        return studyProgramFound.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    @Override
    public void addCategory(NewCategoryDTO newCategoryDTO, String token) {
        String username = userService.getUserByToken(token).getUsername();
        Set<String> roles = userService.getUserRoles(username);

        if (!roles.contains("ADMIN")) {
            throw new ForbiddenActionException("You are not authorized for this action!");
        }

        Studies studies = studiesRepository.findByNameIgnoreCase(newCategoryDTO.getStudies())
                .orElseThrow(() -> new StudiesNotFoundException("There is no studies with name " + newCategoryDTO.getStudies()));

        String categoryName = newCategoryDTO.getName();
        for(StudyProgram studyProgram : studies.getStudyPrograms()) {
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

    @Override
    public void addCategories(BulkImportCategoriesDTO bulkImportCategoriesDTO, String token) {

        String username = userService.getUserByToken(token).getUsername();
        Set<String> roles = userService.getUserRoles(username);

        if (!roles.contains("ADMIN")) {
            throw new ForbiddenActionException("You are not authorized for this action!");
        }

        for (NewCategoryDTO newCategoryDTO : bulkImportCategoriesDTO.getCategories()) {
            Studies studies = studiesRepository.findByNameIgnoreCase(newCategoryDTO.getStudies())
                    .orElseThrow(() -> new StudiesNotFoundException("There is no studies with name " + newCategoryDTO.getStudies()));

            String categoryName = newCategoryDTO.getName();
            for(StudyProgram studyProgram : studies.getStudyPrograms()) {
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

    }

}
