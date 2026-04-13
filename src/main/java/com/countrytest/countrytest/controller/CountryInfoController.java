package com.countrytest.countrytest.controller;

import com.countrytest.countrytest.dto.CountryDetailsDTO;
import com.countrytest.countrytest.entity.CountryInfo;
import com.countrytest.countrytest.entity.Language;
import com.countrytest.countrytest.repository.CountryInfoRepository;
import com.countrytest.countrytest.repository.LanguageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryInfoController {
    private static final Logger logger = LoggerFactory.getLogger(CountryInfoController.class);
    @Autowired
    private CountryInfoRepository countryInfoRepository;
    @Autowired
    private LanguageRepository languageRepository;

    @GetMapping
    public List<CountryInfo> getAllCountriesWithLanguages() {
        return countryInfoRepository.findAllWithLanguages();
    }


    @GetMapping("/{id}")
    public ResponseEntity<CountryInfo> getCountryById(@PathVariable Long id) {
        logger.info("Fetching country with id: {}", id);
        Optional<CountryInfo> countryInfo = countryInfoRepository.findById(id);
        if (countryInfo.isPresent()) {
            logger.info("Country found: {}", countryInfo.get().getName());
            return ResponseEntity.ok(countryInfo.get());
        } else {
            logger.warn("Country with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCountry(@PathVariable Long id,
                                           @RequestBody CountryDetailsDTO dto) {

        logger.info("Updating country with id: {}", id);

        Optional<CountryInfo> optional = countryInfoRepository.findById(id);

        if (optional.isEmpty()) {
            logger.warn("Country with id {} not found", id);
            return ResponseEntity.badRequest()
                    .body(String.format("Country with id %s not found", id));
        }

        CountryInfo country = optional.get();


        if (dto.getName() != null && !dto.getName().isBlank()) {
            country.setName(dto.getName());
        }

        if (dto.getIsoCode() != null && !dto.getIsoCode().isBlank()) {
            country.setIsoCode(dto.getIsoCode());
        }

        if (dto.getCountryFlag() != null && !dto.getCountryFlag().isBlank()) {
            country.setCountryFlag(dto.getCountryFlag());
        }

        if (dto.getCapitalCity() != null && !dto.getCapitalCity().isBlank()) {
            country.setCapitalCity(dto.getCapitalCity());
        }

        if (dto.getPhoneCode() != null && !dto.getPhoneCode().isBlank()) {
            country.setPhoneCode(dto.getPhoneCode());
        }

        if (dto.getContinentCode() != null && !dto.getContinentCode().isBlank()) {
            country.setContinentCode(dto.getContinentCode());
        }

        if (dto.getCurrencyIsoCode() != null && !dto.getCurrencyIsoCode().isBlank()) {
            country.setCurrencyIsoCode(dto.getCurrencyIsoCode());
        }


        CountryInfo updatedCountry = countryInfoRepository.save(country);

        logger.info("Country updated successfully: {}", updatedCountry.getName());

        return ResponseEntity.ok(updatedCountry);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCountry(@PathVariable Long id) {
        logger.info("Deleting country with id: {}", id);
        String body="";
        Optional<CountryInfo> countryInfo = countryInfoRepository.findById(id);
        if (countryInfo.isPresent()) {
            logger.info("Country found: {}", countryInfo.get().getName());
            List<Language> languagesToDelete = languageRepository.findByCountry_id(id);
            languageRepository.deleteAll(languagesToDelete);
            countryInfoRepository.deleteById(id);
            logger.info("Country deleted successfully with id: {}", id);
            body=String.format("Country deleted successfully with id: %s", id);

        }else{
            logger.info("country with id: {} not found", id);
            body=String.format("Country with id: %s not found", id);
        }





        return ResponseEntity.ok(body);
    }


}
