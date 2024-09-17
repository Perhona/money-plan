package com.money_plan.api.domain.budget.service;

import com.money_plan.api.domain.budget.dto.*;
import com.money_plan.api.domain.budget.entity.CategoryBudget;
import com.money_plan.api.domain.budget.entity.MonthlyBudget;
import com.money_plan.api.domain.budget.repository.CategoryBudgetRepository;
import com.money_plan.api.domain.budget.repository.MonthlyBudgetRepository;
import com.money_plan.api.domain.category.entity.Category;
import com.money_plan.api.domain.category.repository.CategoryRepository;
import com.money_plan.api.domain.user.entity.User;
import com.money_plan.api.domain.user.repository.UserRepository;
import com.money_plan.api.global.common.exception.CustomException;
import com.money_plan.api.global.common.exception.ErrorCode;
import com.money_plan.api.global.security.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetService {
    private final MonthlyBudgetRepository monthlyBudgetRepository;
    private final CategoryBudgetRepository categoryBudgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private static MonthlyBudget makeMonthlyBudget(MonthlyBudgetRequestDto requestDto, User user) {
        return MonthlyBudget.builder()
                .user(user)
                .year(requestDto.getYear())
                .month(requestDto.getMonth())
                .totalBudget(requestDto.getTotalBudget())
                .build();
    }

    private static List<CategoryBudgetDto> makeCategoryBudgetDtoList(MonthlyBudget monthlyBudget, List<CategoryBudget> categoryBudgetList) {
        return categoryBudgetList.stream()
                .map(categoryBudget -> CategoryBudgetDto.builder()
                        .id(categoryBudget.getId())
                        .categoryId(categoryBudget.getCategory().getId())
                        .amount(categoryBudget.getAmount())
                        .build())
                .collect(Collectors.toList());
    }

    private static MonthlyBudgetResponseDto makeMonthlyBudgetResponseDto(MonthlyBudget monthlyBudget, List<CategoryBudget> categoryBudgetList, Long userId) {
        return MonthlyBudgetResponseDto.builder()
                .id(monthlyBudget.getId())
                .userId(userId)
                .year(monthlyBudget.getYear())
                .month(monthlyBudget.getMonth())
                .totalBudget(monthlyBudget.getTotalBudget())
                .categoryBudgetDtoList(makeCategoryBudgetDtoList(monthlyBudget, categoryBudgetList))
                .build();
    }

    @Transactional
    public MonthlyBudgetResponseDto createMonthlyBudget(CustomUserDetails userDetails, MonthlyBudgetRequestDto requestDto) {
        // 사용자 정보 조회
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 월별 예산 저장
        MonthlyBudget monthlyBudget = makeMonthlyBudget(requestDto, user);
        monthlyBudgetRepository.save(monthlyBudget);

        // 카테고리 ID 중복 검사
        List<Long> categoryIds = requestDto.getCategoryBudgetDtoList().stream().map(CategoryBudgetDto::getCategoryId).toList();
        Set<Long> uniqueCategoryIds = new HashSet<>(categoryIds);
        if (categoryIds.size() != uniqueCategoryIds.size()) {
            log.info(ErrorCode.CATEGORY_DUPLICATED.getMessage());
            throw new CustomException(ErrorCode.CATEGORY_DUPLICATED);
        }

        // 카테고리 조회
        Map<Long, Category> categoryMap = categoryRepository.findAllById(categoryIds).stream().collect(Collectors.toMap(Category::getId, category -> category));

        List<CategoryBudget> categoryBudgetList = requestDto.getCategoryBudgetDtoList().stream()
                .map(categoryBudgetDto -> {
                    Category category = categoryMap.get(categoryBudgetDto.getCategoryId());
                    // 카테고리 유효성 검사
                    if (category == null) {
                        log.info(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
                        throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
                    }

                    return CategoryBudget.builder()
                            .monthlyBudget(monthlyBudget)
                            .category(category)
                            .amount(categoryBudgetDto.getAmount())
                            .build();
                }).toList();

        // 카테고리별 예산 저장
        categoryBudgetRepository.saveAll(categoryBudgetList);
        return makeMonthlyBudgetResponseDto(monthlyBudget, categoryBudgetList, user.getId());
    }

    @Transactional
    public MonthlyBudgetResponseDto updateMonthlyBudget(Long monthlyBudgetId, MonthlyBudgetUpdateRequestDto requestDto, CustomUserDetails userDetails) {
        // 1. 기존의 MonthlyBudget 조회
        MonthlyBudget monthlyBudget = monthlyBudgetRepository.findByIdAndUserId(monthlyBudgetId, userDetails.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.MONTHLY_BUDGET_NOT_FOUND));
        // 2. 월별 총 예산 수정(Amount 변동이 있는 경우)
        if (!requestDto.getTotalBudget().equals(monthlyBudget.getTotalBudget())) {
            monthlyBudget.updateTotalBudget(requestDto.getTotalBudget());
        }

        // 3. 카테고리별 예산 추가 or 수정
        List<CategoryBudget> updatedCategoryBudgets = new ArrayList<>();
        if (!requestDto.getCategoryBudgetDtoList().isEmpty()) {
            for (CategoryBudgetDto categoryBudgetDto : requestDto.getCategoryBudgetDtoList()) {
                if (categoryBudgetDto.getId() == null) {
                    // 3.1 새로운 카테고리 예산 추가
                    Category category = categoryRepository.findById(categoryBudgetDto.getCategoryId()).orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
                    CategoryBudget categoryBudget = CategoryBudget.builder()
                            .monthlyBudget(monthlyBudget)
                            .category(category)
                            .amount(categoryBudgetDto.getAmount())
                            .build();
                    categoryBudgetRepository.save(categoryBudget);
                    updatedCategoryBudgets.add(categoryBudget);
                } else {
                    // 3.2 기존 카테고리 예산 수정
                    CategoryBudget existingCategoryBudget = categoryBudgetRepository.findByIdAndMonthlyBudgetId(categoryBudgetDto.getId(), monthlyBudget.getId()).orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_BUDGET_NOT_FOUND));
                    existingCategoryBudget.updateAmount(categoryBudgetDto.getAmount());
                    updatedCategoryBudgets.add(existingCategoryBudget);
                }
            }
        }

        // 4. 응답 DTO 반환
        return makeMonthlyBudgetResponseDto(monthlyBudget, updatedCategoryBudgets, userDetails.getUserId());
    }
}
