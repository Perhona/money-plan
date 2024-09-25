package com.money_plan.api.domain.budget.entity;

import com.money_plan.api.domain.budget.dto.CategoryBudgetDto;
import com.money_plan.api.domain.category.entity.Category;
import com.money_plan.api.global.common.exception.CustomException;
import com.money_plan.api.global.common.exception.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Embeddable
public class CategoryBudgets {
    @OneToMany(mappedBy = "monthlyBudget", cascade = CascadeType.ALL, orphanRemoval = true)
    // FetchType을 설정하지 않는 경우, 기본적으로 Lazy Loading
    private final List<CategoryBudget> categoryBudgetList;

    public CategoryBudgets() {
        this.categoryBudgetList = new ArrayList<>();
    }

    public CategoryBudgets(List<CategoryBudget> categoryBudgetList) {
        this.categoryBudgetList = categoryBudgetList;
    }

    public List<CategoryBudget> getCategoryBudgetList() {
        return Collections.unmodifiableList(categoryBudgetList);
    }

    // 월별 예산, 카테고리별 예산 DTO 리스트, 카테고리 Map으로 카테고리별 예산 생성
    public static CategoryBudgets from(MonthlyBudget monthlyBudget, List<CategoryBudgetDto> categoryBudgetDtoList, Map<Long, Category> categoryMap) {
        List<CategoryBudget> categoryBudgetList = categoryBudgetDtoList.stream()
                .map(categoryBudgetDto -> {
                    Category category = categoryMap.get(categoryBudgetDto.getCategoryId());

                    if (category == null) {
                        throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
                    }

                    return CategoryBudget.builder()
                            .monthlyBudget(monthlyBudget)
                            .category(category)
                            .amount(categoryBudgetDto.getAmount())
                            .build();
                }).toList();
        return new CategoryBudgets(categoryBudgetList);
    }

    // 신규 카테고리의 예산 추가
    public void addCategoryBudget(Category category, Long amount, MonthlyBudget monthlyBudget) {
        if (categoryExists(category.getId())) { // 기존 리스트에서 카테고리 중복 여부 검사
            throw new CustomException(ErrorCode.CATEGORY_DUPLICATED);
        }
        CategoryBudget newCategoryBudget = CategoryBudget.builder()
                .monthlyBudget(monthlyBudget)
                .category(category)
                .amount(amount)
                .build();
        categoryBudgetList.add(newCategoryBudget);
    }

    // 기존 카테고리별 예산 리스트에 카테고리 ID 존재 여부 확인
    private boolean categoryExists(Long categoryId) {
        return categoryBudgetList.stream().anyMatch(budget -> budget.getCategory().getId().equals(categoryId));
    }

    // 기존 카테고리별 예산 수정
    public void updateCategoryBudget(Long categoryBudgetId, Long newAmount, MonthlyBudget monthlyBudget) {
        CategoryBudget categoryBudget = findCategoryBudgetById(categoryBudgetId, monthlyBudget.getId());
        categoryBudget.updateAmount(newAmount);
    }

    // 기존 카테고리 예산 존재 여부 확인
    private CategoryBudget findCategoryBudgetById(Long categoryBudgetId, Long monthlyBudgetId) {
        return categoryBudgetList.stream()
                .filter(budget -> budget.getId().equals(categoryBudgetId) && budget.getMonthlyBudget().getId().equals(monthlyBudgetId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_BUDGET_NOT_FOUND));
    }
}
