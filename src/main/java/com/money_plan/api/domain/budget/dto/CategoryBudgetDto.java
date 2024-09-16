package com.money_plan.api.domain.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "카테고리별 예산 정보 DTO")
public class CategoryBudgetDto {
    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;

    @Schema(description = "카테고리 예산 금액", example = "300000")
    private Long amount;
}
