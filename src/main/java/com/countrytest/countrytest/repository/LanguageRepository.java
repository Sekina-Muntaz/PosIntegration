package com.countrytest.countrytest.repository;

import com.countrytest.countrytest.entity.CountryInfo;
import com.countrytest.countrytest.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language,Long> {

    List<Language> findByCountry_id(Long id);
}
