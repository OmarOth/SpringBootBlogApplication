package com.omar.blog.services.impl;

import com.omar.blog.domain.entity.Category;
import com.omar.blog.repositories.CategoryRepository;
import com.omar.blog.services.CategoryService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;

  @Override
  public List<Category> listCategories() {

    return categoryRepository.findAllWithPostCount();
  }

  @Override
  @Transactional
  public Category createCategory(Category category) {
    String categoryName = category.getName();
    if (categoryRepository.existsByNameIgnoreCase(categoryName)) {
      throw new IllegalArgumentException("Category already exists with name: " + categoryName);
    }

    return categoryRepository.save(category);
  }

  @Override
  public void deleteCategory(UUID id) {
    Optional<Category> category = categoryRepository.findById(id);

    if (!category.isPresent()) {
      return;
    }

    if (!category.get().getPosts().isEmpty()) {
      throw new IllegalStateException("Category has posts associated with it");
    }

    categoryRepository.deleteById(id);
  }
}
