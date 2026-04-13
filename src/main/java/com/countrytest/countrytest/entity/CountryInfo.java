package com.countrytest.countrytest.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "CountryInfo")
@Getter
@Setter
    public class CountryInfo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String isoCode;

       private String phoneCode;
        private String capitalCity;
        private String continentCode;
        private String currencyIsoCode;
        private String countryFlag;

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Language> languages;


}

