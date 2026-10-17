package com.lankaspot.server.dto.advertiser.discount;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** POST /advertiser/validate-discount-code */
public record ValidateDiscountCodeRequest(
        @NotBlank @Size(max = 100) String code
) {
    // Whitespace only. The case is never touched because codes are case-sensitive.
    public ValidateDiscountCodeRequest {
        code = code == null ? null : code.trim();
    }
}