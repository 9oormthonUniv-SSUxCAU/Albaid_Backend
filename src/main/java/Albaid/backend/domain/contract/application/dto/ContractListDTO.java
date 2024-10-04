package Albaid.backend.domain.contract.application.dto;

import Albaid.backend.domain.contract.entity.Contract;

public record ContractListDTO(
        Integer id,
        String url,
        String title
) {

    public static ContractListDTO of(Contract contract) {
        return new ContractListDTO(
                contract.getId(),
                contract.getUrl(),
                contract.getTitle()
        );
    }
}

