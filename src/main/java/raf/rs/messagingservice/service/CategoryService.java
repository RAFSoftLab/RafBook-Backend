package raf.rs.messagingservice.service;

import raf.rs.messagingservice.dto.BulkImportCategoriesDTO;
import raf.rs.messagingservice.dto.NewCategoryDTO;

import java.util.List;

public interface CategoryService {

    List<String> getAllCategoryNames(String studiesName, String studyProgramName);
    void addCategory(NewCategoryDTO newCategoryDTO, String token);
    void addCategories(BulkImportCategoriesDTO bulkImportCategoriesDTO, String token);

}
