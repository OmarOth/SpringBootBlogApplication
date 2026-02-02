package com.omar.blog.controllers;

import com.omar.blog.domain.dto.CategoryDto;
import com.omar.blog.domain.dto.CreateCategoryRequest;
import com.omar.blog.domain.entity.Category;
import com.omar.blog.mappers.CategoryMapper;
import com.omar.blog.services.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;

  @GetMapping
  public ResponseEntity<List<CategoryDto>> listCategories() {
    List<Category> categories = categoryService.listCategories();
    List<CategoryDto> categoryDtos =
        categories.stream().map(category -> categoryMapper.toDto(category)).toList();
    return ResponseEntity.ok(categoryDtos);
  }

  @PostMapping
  public ResponseEntity<CategoryDto> createCategory(
      @Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
    Category categoryToCreate = categoryMapper.toEntity(createCategoryRequest);
    Category savedCategory = categoryService.createCategory(categoryToCreate);
    return new ResponseEntity<>(categoryMapper.toDto(savedCategory), HttpStatus.CREATED);
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deteCategory(@PathVariable UUID id) {
    categoryService.deleteCategory(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
