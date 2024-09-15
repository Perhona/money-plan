package com.money_plan.api.domain.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@Schema(description = "카테고리 DTO")
public class CategoryRequestDto {
    @NotEmpty(message = "카테고리명을 입력해주세요.")
    @Schema(description = "카테고리 이름", example = "식비")
    @Length(min = 2, max = 20, message = "카테고리 이름은 2 ~ 20자 사이여야 합니다.")
    private String name;
}
