package com.countrytest.countrytest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CountryInfoRequest {
    @NotBlank(message = "Country isoCode is required")
    public String isoCode;
}
