package raf.rs.messagingservice.service.implementation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.dto.BulkImportCategoriesDTO;
import raf.rs.messagingservice.dto.NewCategoryDTO;
import raf.rs.messagingservice.mapper.CategoryMapper;
import raf.rs.messagingservice.model.Category;
import raf.rs.messagingservice.repository.CategoryRepository;
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

    @Override
    public List<String> getAllCategoryNames() {
        return categoryRepository.findAll()
                .stream()
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

        categoryRepository.save(categoryMapper.toEntity(newCategoryDTO));
    }

    @Override
    public void addCategories(BulkImportCategoriesDTO bulkImportCategoriesDTO, String token) {

        String username = userService.getUserByToken(token).getUsername();
        Set<String> roles = userService.getUserRoles(username);

        if (!roles.contains("ADMIN")) {
            throw new ForbiddenActionException("You are not authorized for this action!");
        }

        List<Category> categories = bulkImportCategoriesDTO.getCategories().stream()
                .map(categoryMapper::toEntity)
                .collect(Collectors.toList());

        categoryRepository.saveAll(categories);
    }

}
