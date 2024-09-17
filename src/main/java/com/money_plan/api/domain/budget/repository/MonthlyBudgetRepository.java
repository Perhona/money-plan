package com.money_plan.api.domain.budget.repository;

import com.money_plan.api.domain.budget.entity.MonthlyBudget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, Long> {
}
