package com.money_plan.api.domain.budget.entity;

import com.money_plan.api.domain.budget.dto.CategoryBudgetDto;
import com.money_plan.api.domain.category.entity.Category;
import com.money_plan.api.global.common.exception.CustomException;
import com.money_plan.api.global.common.exception.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
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
}
