package com.clinic.dto;

import jakarta.validation.constraints.NotNull;

public class VisitRequest {

    @NotNull(message = "Token ID is required")
    private Long tokenId;

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }
}