package raf.rs.messagingservice.mapper;

import org.springframework.stereotype.Component;
import raf.rs.messagingservice.dto.CategoryDTO;
import raf.rs.messagingservice.dto.NewCategoryDTO;
import raf.rs.messagingservice.model.Category;

@Component
public class CategoryMapper {

    public Category toEntity(NewCategoryDTO newCategoryDTO) {
        Category category = new Category();
        category.setName(newCategoryDTO.getName());
        category.setDescription(newCategoryDTO.getDescription());

        return category;
    }

    public CategoryDTO toDto(Category category) {

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());

        return categoryDTO;
    }

}
