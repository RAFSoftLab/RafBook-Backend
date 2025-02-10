package raf.rs.messagingservice.service.implementation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.model.Category;
import raf.rs.messagingservice.repository.CategoryRepository;
import raf.rs.messagingservice.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;

    @Override
    public List<String> getAllCategoryNames() {
        return categoryRepository.findAll()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }
}
