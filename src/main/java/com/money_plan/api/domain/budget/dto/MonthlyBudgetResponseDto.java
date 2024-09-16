package com.money_plan.api.domain.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@Schema(description = "월별 예산 설정 응답 DTO")
public class MonthlyBudgetResponseDto {
    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "연도", example = "2024")
    private int year;

    @Schema(description = "월", example = "9")
    private int month;

    @Schema(description = "총 예산 금액", example = "1000000")
    private Long totalBudget;

    @Schema(description = "카테고리별 예산 목록")
    private List<CategoryBudgetDto> categoryBudgetDtoList;
}
