package raf.rs.messagingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.rs.messagingservice.dto.BulkImportCategoriesDTO;
import raf.rs.messagingservice.dto.NewCategoryDTO;
import raf.rs.messagingservice.service.CategoryService;
import raf.rs.userservice.dto.ResponseMessageDTO;

import java.util.List;

@Tag(name = "Category Controller", description = "Endpoints for managing categories")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/categories")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private CategoryService categoryService;

    @Operation(summary = "Get all category names", description = "Returns a list of all category names available in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved category names",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String[].class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content)
    })
    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllCategoryNamesByStudiesAndStudyProgram(@RequestParam(name = "studies", required = true) String studies,
                                                                                    @RequestParam(name = "studyPrograms", required = true) String studyPrograms) {
        return new ResponseEntity<>(categoryService.getAllCategoryNames(studies, studyPrograms), HttpStatus.OK);
    }


    @Operation(summary = "Add a new category", description = "Adds a new category to the system. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category successfully added",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<ResponseMessageDTO> addCategory(@RequestHeader("Authorization") String token, @RequestBody NewCategoryDTO dto) {
        categoryService.addCategory(dto, token.substring(7));
        return new ResponseEntity<>(new ResponseMessageDTO("Category successfully added"), HttpStatus.OK);
    }

    @Operation(summary = "Bulk import categories", description = "Imports multiple categories at once. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories successfully imported",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    @PostMapping("/bulk-import")
    public ResponseEntity<ResponseMessageDTO> addCategories(@RequestHeader("Authorization") String token, @RequestBody BulkImportCategoriesDTO dto) {
        categoryService.addCategories(dto, token.substring(7));
        return new ResponseEntity<>(new ResponseMessageDTO("Categories successufully imported"), HttpStatus.OK);
    }

}
