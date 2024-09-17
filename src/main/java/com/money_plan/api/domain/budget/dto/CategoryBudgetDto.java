package com.money_plan.api.domain.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "카테고리별 예산 정보 DTO")
public class CategoryBudgetDto {
    @Schema(description = "카테고리 예산 ID", example = "1")
    private Long id;

    @Schema(description = "카테고리 ID", example = "1")
    @NotNull(message = "카테고리 ID는 필수 입력 값입니다.")
    private Long categoryId;

    @Schema(description = "카테고리 예산 금액", example = "300000")
    @Min(value = 0, message = "카테고리 예산 금액은 0원 이상으로 입력해야 합니다.")
    @NotNull(message = "카테고리 예산 금액은 필수 입력 값입니다.")
    private Long amount;
}
