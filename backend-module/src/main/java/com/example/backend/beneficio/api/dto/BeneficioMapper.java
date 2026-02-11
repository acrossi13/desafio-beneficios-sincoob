package com.example.backend.beneficio.api.dto;

import com.example.backend.beneficio.api.domain.BeneficioEntity;

public final class BeneficioMapper {
    private BeneficioMapper() {}

    public static BeneficioResponse toResponse(BeneficioEntity e) {
        return new BeneficioResponse(
                e.getId(),
                e.getNome(),
                e.getDescricao(),
                e.getValor(),
                e.getAtivo(),
                e.getVersion()
        );
    }
}