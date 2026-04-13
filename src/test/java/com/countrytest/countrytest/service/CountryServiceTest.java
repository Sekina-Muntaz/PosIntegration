package com.countrytest.countrytest.service;

import com.countrytest.countrytest.dto.FullCountryInfoResponse;
import com.countrytest.countrytest.dto.IsoCodeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class CountryServiceTest {

    @Test
    void capitalizeFirstLetter_shouldReturnCapitalizedName() {
        CountryService countryService = new CountryService();

        String result = countryService.capitalizeFirstLetter("nigeria");

        assertThat(result).isEqualTo("Nigeria");
    }

    @Test
    void getIsoCode_shouldParseSoapResponseFromService() {
        CountryService countryService = spy(new CountryService());

        String soapResponse = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:m=\"http://www.oorsprong.org/websamples.countryinfo\">"
                + "<soapenv:Body>"
                + "<m:CountryISOCodeResponse>"
                + "<m:CountryISOCodeResult>KE</m:CountryISOCodeResult>"
                + "</m:CountryISOCodeResponse>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        doReturn(ResponseEntity.ok(soapResponse)).when(countryService).sendRequest("Kenya");

        IsoCodeResponse response = countryService.getIsoCode("Kenya");

        assertThat(response).isNotNull();
        assertThat(response.getIsoCode()).isEqualTo("KE");
    }

    @Test
    void getFullCountryInf_shouldParseFullCountryInfoResponse() {
        CountryService countryService = spy(new CountryService());

        String soapResponse = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:m=\"http://www.oorsprong.org/websamples.countryinfo\">"
                + "<soapenv:Body>"
                + "<m:FullCountryInfoResponse>"
                + "<m:sISOCode>KE</m:sISOCode>"
                + "<m:sName>Kenya</m:sName>"
                + "<m:sCapitalCity>Nairobi</m:sCapitalCity>"
                + "<m:sPhoneCode>254</m:sPhoneCode>"
                + "<m:sContinentCode>AF</m:sContinentCode>"
                + "<m:sCurrencyISOCode>KES</m:sCurrencyISOCode>"
                + "<m:sCountryFlag>https://example.com/flag.png</m:sCountryFlag>"
                + "<m:Languages>"
                + "<m:tLanguage>"
                + "<m:sISOCode>en</m:sISOCode>"
                + "<m:sName>English</m:sName>"
                + "</m:tLanguage>"
                + "</m:Languages>"
                + "</m:FullCountryInfoResponse>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        doReturn(ResponseEntity.ok(soapResponse)).when(countryService).sendFullCountryRequest("KE");

        FullCountryInfoResponse response = countryService.getFullCountryInf("KE");

        assertThat(response).isNotNull();
        assertThat(response.getIsoCode()).isEqualTo("KE");
        assertThat(response.getName()).isEqualTo("Kenya");
        assertThat(response.getCapitalCity()).isEqualTo("Nairobi");
        assertThat(response.getLanguages()).hasSize(1);
        assertThat(response.getLanguages().get(0).getIsoCode()).isEqualTo("en");
        assertThat(response.getLanguages().get(0).getName()).isEqualTo("English");
    }
}
