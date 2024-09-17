package com.money_plan.api.domain.budget.controller;

import com.money_plan.api.domain.budget.dto.MonthlyBudgetRequestDto;
import com.money_plan.api.domain.budget.dto.MonthlyBudgetResponseDto;
import com.money_plan.api.domain.budget.dto.MonthlyBudgetUpdateRequestDto;
import com.money_plan.api.domain.budget.service.BudgetService;
import com.money_plan.api.global.common.exception.CustomException;
import com.money_plan.api.global.common.response.CommonResponse;
import com.money_plan.api.global.security.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Tag(name = "예산 관리 API", description = "예산 설정, 추천 등의 API 입니다.")
public class BudgetController {
    private final BudgetService budgetService;

    /**
     * 월별 예산을 설정합니다.
     *
     * @param requestDto  월별 예산 요청 DTO
     * @param userDetails 컨텍스트에 저장된 사용자 정보
     * @return MonthlyBudgetResponseDto
     * @throws CustomException USER_NOT_FOUND 사용자를 찾을 수 없는 경우
     * @throws CustomException CATEGORY_NOT_FOUND 카테고리를 찾을 수 없는 경우
     * @throws CustomException CATEGORY_DUPLICATED 중복된 카테고리 예산 값이 있는 경우
     */
    @Operation(summary = "새로운 월별 예산 설정", description = "새로운 예산을 설정합니다.")
    @PostMapping
    public ResponseEntity<CommonResponse<MonthlyBudgetResponseDto>> createMonthlyBudget(
            @Valid @RequestBody MonthlyBudgetRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MonthlyBudgetResponseDto createdBudget = budgetService.createMonthlyBudget(userDetails, requestDto);
        return new ResponseEntity<>(CommonResponse.ok(createdBudget), HttpStatus.CREATED);
    }

    /**
     * 월별 예산을 수정합니다.
     *
     * @param requestDto  월별 예산 수정 요청 DTO
     * @param userDetails 컨텍스트에 저장된 사용자 정보
     * @return List<MonthlyBudgetResponseDto>
     * @throws CustomException USER_NOT_FOUND 사용자를 찾을 수 없는 경우
     */
    @Operation(summary = "월별 예산, 카테고리별 예산 수정", description = "월별 예산 및 카테고리별 예산을 수정합니다. 새로운 카테고리를 추가하는 경우, 신규 카테고리별 예산이 추가됩니다.")
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<MonthlyBudgetResponseDto>> recommendMonthlyBudget(
            @PathVariable Long id,
            @Valid @RequestBody MonthlyBudgetUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MonthlyBudgetResponseDto updatedBudget = budgetService.updateMonthlyBudget(id, requestDto, userDetails);
        return new ResponseEntity<>(CommonResponse.ok(updatedBudget), HttpStatus.OK);
    }
}
