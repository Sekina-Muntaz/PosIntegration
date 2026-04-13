package com.countrytest.countrytest.dto;


import lombok.Data;

import java.util.List;
@Data
public class CountryDetailsDTO {


    private String name;
    private String isoCode;
    private String phoneCode;
    private String capitalCity;
    private String continentCode;
    private String currencyIsoCode;
    private String countryFlag;

    private List<LanguageDTO> languages;

    // getters and setters



}