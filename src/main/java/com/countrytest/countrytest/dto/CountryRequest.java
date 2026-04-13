package com.countrytest.countrytest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CountryRequest {
    @NotBlank(message = "Country name is required")
    public String name;
}
