package com.countrytest.countrytest.repository;

import com.countrytest.countrytest.entity.CountryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryInfoRepository extends JpaRepository<CountryInfo,Long> {

    boolean existsByIsoCode(String isoCode);

    @Query("SELECT c FROM CountryInfo c JOIN FETCH c.languages WHERE c.id = :id")
    CountryInfo findByIdWithLanguages(Long id);

    @Query("SELECT DISTINCT c FROM CountryInfo c JOIN FETCH c.languages")
    List<CountryInfo> findAllWithLanguages();
}
