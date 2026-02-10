package com.example.backend.beneficio.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record BeneficioRequest(
        @NotBlank
        @Schema(example = "Benefício teste")
        String nome,

        @Schema(example = "Criado via API")
        String descricao,

        @NotNull
        @DecimalMin(value = "0.00", inclusive = true)
        @Schema(example = "100.00")
        BigDecimal valor,

        @Schema(example = "true")
        Boolean ativo
) {}