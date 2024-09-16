package com.money_plan.api.domain.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@Schema(description = "월별 예산 설정 요청 DTO")
public class MonthlyBudgetDto {
    @Schema(description = "연도", example = "2024")
    @NotNull(message = "연도는 필수 입력 값입니다.")
    private int year;

    @Schema(description = "월", example = "9")
    @Min(value = 1, message = "월은 1 이상이어야 합니다.")
    @Max(value = 12, message = "월은 12 이하여야 합니다.")
    private int month;

    @Schema(description = "총 예산 금액", example = "1000000")
    @NotNull(message = "총 예산 금액은 필수 입력 값입니다.")
    private Long totalBudget;

    @Schema(description = "카테고리별 예산 목록")
    private List<CategoryBudgetDto> categoryBudgets;
}
