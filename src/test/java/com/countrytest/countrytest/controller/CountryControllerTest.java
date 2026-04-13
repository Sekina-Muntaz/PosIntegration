package com.countrytest.countrytest.controller;

import com.countrytest.countrytest.dto.CountryInfoRequest;
import com.countrytest.countrytest.dto.CountryRequest;
import com.countrytest.countrytest.dto.FullCountryInfoResponse;
import com.countrytest.countrytest.dto.IsoCodeResponse;
import com.countrytest.countrytest.dto.LanguageDTO;
import com.countrytest.countrytest.repository.CountryInfoRepository;
import com.countrytest.countrytest.repository.LanguageRepository;
import com.countrytest.countrytest.service.CountryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CountryController.class)
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryService countryService;

    @MockBean
    private CountryInfoRepository countryInfoRepository;

    @MockBean
    private LanguageRepository languageRepository;

    @Test
    void getCountryIsoCode_shouldReturnIsoCode() throws Exception {
        IsoCodeResponse response = new IsoCodeResponse();
        response.setIsoCode("US");

        when(countryService.capitalizeFirstLetter("usa")).thenReturn("Usa");
        when(countryService.getIsoCode("Usa")).thenReturn(response);

        mockMvc.perform(post("/api/v1/isoCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"usa\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isoCode").value("US"));
    }

    @Test
    void getCountryIsoCode_whenNameIsEmpty_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/isoCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"   \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCountryInfo_whenIsoCodeIsMissing_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/country/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isoCode\": \"   \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCountryInfo_shouldSaveCountryWhenNotExists() throws Exception {
        FullCountryInfoResponse fullResponse = new FullCountryInfoResponse();
        fullResponse.setIsoCode("KE");
        fullResponse.setName("Kenya");
        fullResponse.setCapitalCity("Nairobi");
        fullResponse.setCountryFlag("https://example.com/flag.png");
        fullResponse.setPhoneCode("254");
        fullResponse.setContinentCode("AF");
        fullResponse.setCurrencyIsoCode("KES");
        LanguageDTO language = new LanguageDTO();
        language.setIsoCode("en");
        language.setName("English");
        fullResponse.setLanguages(List.of(language));

        when(countryService.getFullCountryInf("KE")).thenReturn(fullResponse);
        when(countryInfoRepository.existsByIsoCode("KE")).thenReturn(false);
        when(countryInfoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/v1/country/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isoCode\": \"KE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kenya"))
                .andExpect(jsonPath("$.isoCode").value("KE"));
    }
}
