package com.countrytest.countrytest.controller;

import com.countrytest.countrytest.dto.*;
import com.countrytest.countrytest.entity.CountryInfo;
import com.countrytest.countrytest.entity.Language;
import com.countrytest.countrytest.repository.CountryInfoRepository;
import com.countrytest.countrytest.repository.LanguageRepository;
import com.countrytest.countrytest.service.CountryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CountryController {
    private static final Logger logger = LoggerFactory.getLogger(CountryController.class);
    @Autowired
    private CountryService countryService;


    @Autowired
    private CountryInfoRepository countryInfoRepository;

    @Autowired
    private LanguageRepository languageRepository;
    @PostMapping("/isoCode")

    public ResponseEntity<?> getCountryIsoCode(@Valid @RequestBody CountryRequest request) {



        if ((request.getName().trim().isEmpty())) {
            // Return a detailed error response
            return ResponseEntity.badRequest().body("Country name is required");
        }
        String countryName = request.getName().trim();

        countryName = countryService.capitalizeFirstLetter(countryName);
        logger.info("Received country name: {}", countryName);

        IsoCodeResponse isoCodeResponse;
        try {
            isoCodeResponse = countryService.getIsoCode(countryName);
        } catch (Exception e) {
            // Handle potential errors from the SOAP service
            logger.error("Error fetching ISO code for the country: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error fetching ISO code for the country: " + e.getMessage());
        }


        return ResponseEntity.ok(isoCodeResponse);
    }


    @PostMapping("/country/info")
    public ResponseEntity<?> getCountryInfo(@Valid @RequestBody CountryInfoRequest request) {
        if ((request.getIsoCode().trim().isEmpty())) {

            return ResponseEntity.badRequest().body("Country isoCode is required");
        }

        String isoCode = request.getIsoCode().trim();

        FullCountryInfoResponse fullCountryInfoResponse;

        try {

            fullCountryInfoResponse = countryService.getFullCountryInf(isoCode.trim());
            if(fullCountryInfoResponse!=null){
                if(fullCountryInfoResponse.getName().equalsIgnoreCase("Country not found in the database")){
                    return ResponseEntity.badRequest().body(fullCountryInfoResponse.getName());
                }
            }

            boolean existsByIsoCode =countryInfoRepository.existsByIsoCode(isoCode.trim());
            if(existsByIsoCode){
                logger.debug("Country with isoCode {} exists",isoCode);
                return ResponseEntity.ok(fullCountryInfoResponse);

            }
            logger.debug("Adding Country with isoCode {} to db",isoCode);
            // Save into countryinfo table
            CountryInfo countryInfo = new CountryInfo();
            countryInfo.setName(fullCountryInfoResponse.getName());
            countryInfo.setIsoCode(fullCountryInfoResponse.getIsoCode());
            countryInfo.setCountryFlag(fullCountryInfoResponse.getCountryFlag());
            countryInfo.setCapitalCity(fullCountryInfoResponse.getCapitalCity());
            countryInfo.setPhoneCode(fullCountryInfoResponse.getPhoneCode());
            countryInfo.setContinentCode(fullCountryInfoResponse.getContinentCode());
            countryInfo.setCurrencyIsoCode(fullCountryInfoResponse.getCurrencyIsoCode());
            countryInfo = countryInfoRepository.save(countryInfo);

            // Save into languages table
            List<Language> languages = new ArrayList<>();
            for (LanguageDTO languageName : fullCountryInfoResponse.getLanguages()) {
                Language language = new Language();
                language.setName(languageName.getName());
                language.setIsoCode(languageName.getIsoCode());
                language.setCountry(countryInfo); // Link to countryInfo
                languages.add(language);
            }

            languageRepository.saveAll(languages);


        } catch (Exception e) {
            // Handle potential errors from retrieving full country info
            return ResponseEntity.status(500).body("Error fetching full country info: " + e.getMessage());
        }

        return ResponseEntity.ok(fullCountryInfoResponse);
    }




}
