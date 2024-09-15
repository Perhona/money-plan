package com.money_plan.api.domain.category.controller;

import com.money_plan.api.domain.category.dto.CategoryRequestDto;
import com.money_plan.api.domain.category.dto.CategoryResponseDto;
import com.money_plan.api.domain.category.service.CategoryService;
import com.money_plan.api.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "카테고리 API", description = "카테고리 관련 API")
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * 카테고리 추가
     *
     * @param categoryRequestDto 카테고리 추가 요청 DTO
     * @return CategoryResponseDto
     */
    @Operation(summary = "새로운 카테고리 추가", description = "새로운 카테고리를 추가합니다.")
    @ApiResponse(
            responseCode = "200"
            , description = "카테고리 추가 성공"
            , content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponseDto.class))
    )
    @PostMapping
    public ResponseEntity<CommonResponse<CategoryResponseDto>> createCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        return ResponseEntity.ok(CommonResponse.ok("카테고리를 생성했습니다.", categoryService.createCategory(categoryRequestDto)));
    }

    /**
     * 카테고리 목록 조회
     *
     * @return List<CategoryResponseDto>
     */
    @Operation(summary = "카테고리 목록", description = "카테고리를 목록을 반환합니다.")
    @ApiResponse(
            responseCode = "200"
            , description = "카테고리 목록 조회 성공"
            , content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CategoryResponseDto.class)))
    )
    @GetMapping
    public ResponseEntity<CommonResponse<List<CategoryResponseDto>>> getCategoryList(){
        return ResponseEntity.ok(CommonResponse.ok(categoryService.getCategoryList()));
    }
}
