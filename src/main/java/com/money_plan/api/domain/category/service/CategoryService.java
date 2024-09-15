package com.money_plan.api.domain.category.service;

import com.money_plan.api.domain.category.dto.CategoryRequestDto;
import com.money_plan.api.domain.category.dto.CategoryResponseDto;
import com.money_plan.api.domain.category.entity.Category;
import com.money_plan.api.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    private CategoryResponseDto makeCategoryResponseDto(Category category) {
        return CategoryResponseDto.builder().id(category.getId()).name(category.getName()).build();
    }

    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        Category category = Category
                .builder()
                .name(categoryRequestDto.getName())
                .build();
        categoryRepository.save(category);
        return CategoryResponseDto.builder().id(category.getId()).name(category.getName()).build();
    }

    public List<CategoryResponseDto> getCategoryList() {
        List<Category> categoryList = categoryRepository.findAll();
        return categoryList.stream().map(this::makeCategoryResponseDto).toList();
    }
}
