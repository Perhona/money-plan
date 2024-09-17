package com.money_plan.api.domain.budget.repository;

import com.money_plan.api.domain.budget.entity.MonthlyBudget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, Long> {
    Optional<MonthlyBudget> findByIdAndUserId(Long id, Long userId);
}
