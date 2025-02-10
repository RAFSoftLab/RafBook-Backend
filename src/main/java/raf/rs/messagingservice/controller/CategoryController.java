package raf.rs.messagingservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raf.rs.messagingservice.service.CategoryService;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/categories")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private CategoryService categoryService;

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllCategoryNames() {
        return new ResponseEntity<>(categoryService.getAllCategoryNames(), HttpStatus.OK);
    }

}
