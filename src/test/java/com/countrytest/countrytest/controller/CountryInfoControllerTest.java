package com.countrytest.countrytest.controller;

import com.countrytest.countrytest.dto.CountryDetailsDTO;
import com.countrytest.countrytest.entity.CountryInfo;
import com.countrytest.countrytest.entity.Language;
import com.countrytest.countrytest.repository.CountryInfoRepository;
import com.countrytest.countrytest.repository.LanguageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CountryInfoController.class)
class CountryInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryInfoRepository countryInfoRepository;

    @MockBean
    private LanguageRepository languageRepository;

    @Test
    void getAllCountries_shouldReturnList() throws Exception {
        CountryInfo country = new CountryInfo();
        country.setId(1L);
        country.setName("Kenya");

        when(countryInfoRepository.findAllWithLanguages()).thenReturn(List.of(country));

        mockMvc.perform(get("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Kenya"));
    }

    @Test
    void getCountryById_whenNotFound_shouldReturn404() throws Exception {
        when(countryInfoRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/countries/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCountry_whenCountryDoesNotExist_shouldReturnBadRequest() throws Exception {
        when(countryInfoRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/v1/countries/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated name\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Country with id 99 not found"));
    }

    @Test
    void deleteCountry_whenFound_shouldReturnSuccessMessage() throws Exception {
        CountryInfo country = new CountryInfo();
        country.setId(2L);
        country.setName("Uganda");

        when(countryInfoRepository.findById(2L)).thenReturn(Optional.of(country));
        when(languageRepository.findByCountry_id(2L)).thenReturn(List.of(new Language()));
        doNothing().when(languageRepository).deleteAll(any());

        mockMvc.perform(delete("/api/v1/countries/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Country deleted successfully with id: 2"));
    }
}
