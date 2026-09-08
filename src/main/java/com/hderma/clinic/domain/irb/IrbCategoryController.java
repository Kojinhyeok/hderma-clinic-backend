package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/irb-categories")
@RequiredArgsConstructor
public class IrbCategoryController {

    private final IrbCategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<IrbDto.CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<IrbDto.CategoryResponse> createCategory(@RequestBody IrbDto.CategoryResponse req) {
        return ResponseEntity.ok(categoryService.saveCategory(req.getCategoryCode(), req.getCategory(), null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IrbDto.CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody IrbDto.CategoryResponse req) {
        return ResponseEntity.ok(categoryService.saveCategory(req.getCategoryCode(), req.getCategory(), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}