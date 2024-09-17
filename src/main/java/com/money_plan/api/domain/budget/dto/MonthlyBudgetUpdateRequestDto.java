package com.money_plan.api.domain.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@Schema(description = "월별 예산 수정 요청 DTO")
public class MonthlyBudgetUpdateRequestDto {

    @Schema(description = "수정할 월별 예산 금액", example = "1000000")
    @NotNull(message = "총 예산 금액은 필수 입력 값입니다.")
    @Min(value = 0, message = "총 예산 금액은 0원 이상으로 입력해주세요.")
    private Long totalBudget; // 수정할 월별 총 예산

    @Schema(description = "총 예산 카테고리별 수정 요청 DTO 목록")
    @Valid
    private List<CategoryBudgetDto> categoryBudgetDtoList; // 수정할 카테고리별 예산 목록

}
