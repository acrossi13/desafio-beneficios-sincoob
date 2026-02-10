package com.example.backend.beneficio.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record BeneficioResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Benefício A") String nome,
        @Schema(example = "Descrição A") String descricao,
        @Schema(example = "1000.00") BigDecimal valor,
        @Schema(example = "true") boolean ativo,
        @Schema(example = "0") long version
) {}