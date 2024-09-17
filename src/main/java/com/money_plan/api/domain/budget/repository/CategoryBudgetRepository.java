package com.money_plan.api.domain.budget.repository;

import com.money_plan.api.domain.budget.entity.CategoryBudget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryBudgetRepository extends JpaRepository<CategoryBudget, Long> {
    Optional<CategoryBudget> findByIdAndMonthlyBudgetId(Long id, Long monthlyBudgetId);
}
