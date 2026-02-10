package com.example.backend.beneficio.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record TransferRequest(
        @Schema(example = "1") Long fromId,
        @Schema(example = "2") Long toId,
        @Schema(example = "10.00") BigDecimal valor
) {}