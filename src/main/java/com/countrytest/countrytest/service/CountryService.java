package com.countrytest.countrytest.service;

import com.countrytest.countrytest.dto.FullCountryInfoResponse;
import com.countrytest.countrytest.dto.IsoCodeResponse;
import com.countrytest.countrytest.dto.LanguageDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.*;


@Service
@Slf4j
public class CountryService {
    private static final Logger logger = LoggerFactory.getLogger(CountryService.class);
    @Value("${country.endpoint}")
    private String serviceEndPoint;
       public String callSoapServiceForIsoCode(String countryName) {
        try {
            ResponseEntity<String> soaResponse = sendRequest(countryName);
            String isoCode=parseXMLResponse(soaResponse.getBody());
            return isoCode;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
       }



    public ResponseEntity<String> sendRequest(String countryName) {

        ResponseEntity<String> soaResponse;
        try {





            String request = String.format("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://www.oorsprong.org/websamples.countryinfo\">\n" +
                            "   <soapenv:Header/>\n" +
                            "   <soapenv:Body>\n" +
                            "      <web:CountryISOCode>\n" +
                            "         <web:sCountryName>%s</web:sCountryName>\n" +
                            "      </web:CountryISOCode>\n" +
                            "   </soapenv:Body>\n" +
                            "</soapenv:Envelope>",countryName);




            HttpHeaders headers = new HttpHeaders();
            headers.add("SOAPAction", "");
            headers.setContentType(MediaType.TEXT_XML);

            HttpEntity<String> entity = new HttpEntity<String>(request, headers);
            logger.info("Sending getIsoCode   request {}", request);
            RestTemplate restTemplate = new RestTemplate();
            soaResponse = restTemplate.exchange(serviceEndPoint, HttpMethod.POST, entity, String.class);


            logger.debug("SOA RESPONSE : status code {} ExchangeRateBody {}", soaResponse.getStatusCode(), soaResponse.getBody());
        } catch (Exception e) {
            soaResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            logger.error("Error occurred while calling {} Ex {} ", "sendRequestToSoa", e.getMessage());
        }
        return soaResponse;
    }

    public ResponseEntity<String> sendFullCountryRequest(String isoCode) {

        ResponseEntity<String> soaResponse;
        try {





            String request = String.format("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://www.oorsprong.org/websamples.countryinfo\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <web:FullCountryInfo>\n" +
                    "         <web:sCountryISOCode>%s</web:sCountryISOCode>\n" +
                    "      </web:FullCountryInfo>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>",isoCode);




            HttpHeaders headers = new HttpHeaders();
            headers.add("SOAPAction", "");
            headers.setContentType(MediaType.TEXT_XML);

            HttpEntity<String> entity = new HttpEntity<String>(request, headers);
            logger.debug("Sending getFullCountry info   request {}", request);
            RestTemplate restTemplate = new RestTemplate();
            soaResponse = restTemplate.exchange(serviceEndPoint, HttpMethod.POST, entity, String.class);


            logger.debug("SOA RESPONSE : status code {} ExchangeRateBody {}", soaResponse.getStatusCode(), soaResponse.getBody());
        } catch (Exception e) {
            soaResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            logger.error("Error occurred while calling {} Ex {} ", "sendRequestToSoa", e.getMessage());
        }
        return soaResponse;
    }






    private String parseXMLResponse(String responseBody) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(responseBody));
            org.w3c.dom.Document doc = db.parse(is);

            String isoCode = "";

            NodeList nodeList = doc.getElementsByTagName("m:CountryISOCodeResult");
            if (nodeList.getLength() > 0) {
                Node node = nodeList.item(0);
                isoCode = node.getTextContent().trim();

            }
            return  isoCode;
        } catch (Exception e) {
            logger.error("Error occurred while calling {} Ex {}", "parseXMLResponse", e);
            return "Error occurred while calling parseXMLResponse ";
        }
    }



    public String capitalizeFirstLetter(String countryName) {
        return countryName.substring(0, 1).toUpperCase() + countryName.substring(1).toLowerCase();
    }

    private static String getElementValue(Document document, String elementName) {
        NodeList nodeList = document.getElementsByTagName(elementName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    private String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null; // or throw an exception if a value is mandatory
    }

    public IsoCodeResponse getIsoCode(String countryName){
           IsoCodeResponse isoCodeResponse=new IsoCodeResponse();
           try {
               ResponseEntity<String> responseEntity=sendRequest(countryName);
               String soapResponseBody = responseEntity.getBody();
               if(soapResponseBody!=null){
                   DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder builder = factory.newDocumentBuilder();
                   Document document = builder.parse(new InputSource(new StringReader(soapResponseBody)));
                   logger.info(soapResponseBody);

                   String sIsoCode = getElementValue(document, "m:CountryISOCodeResult");
                   isoCodeResponse.setIsoCode(sIsoCode);

               }

           } catch (Exception e) {
               throw new RuntimeException(e);
           }

        return isoCodeResponse;
    }
    public FullCountryInfoResponse getFullCountryInf (String isoCode){
           FullCountryInfoResponse countryInfoResponse=new FullCountryInfoResponse();
           try {
               ResponseEntity<String> soaResponse = sendFullCountryRequest(isoCode);
               String soapResponseBody = soaResponse.getBody();
               if(soapResponseBody!=null){
                   DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder builder = factory.newDocumentBuilder();
                   Document document = builder.parse(new InputSource(new StringReader(soapResponseBody)));
                   logger.info(soapResponseBody);

                   String sIsoCode = getElementValue(document, "m:sISOCode");
                   String sName = getElementValue(document, "m:sName");
                   String sCapitalCity = getElementValue(document, "m:sCapitalCity");
                   String sPhoneCode = getElementValue(document, "m:sPhoneCode");
                   String sContinentCode = getElementValue(document, "m:sContinentCode");
                   String sCurrencyISOCode = getElementValue(document, "m:sCurrencyISOCode");
                   String sCountryFlag = getElementValue(document, "m:sCountryFlag");
                   String slanguages = getElementValue(document, "m:Languages");

                   List<LanguageDTO> languages = new ArrayList<>();
                   NodeList languagesNode = document.getElementsByTagName("m:Languages");
                   if (languagesNode != null && languagesNode.getLength() > 0) {
                       Element languagesElement = (Element) languagesNode.item(0); // Get the <m:Languages> element
                       NodeList languageNodes = languagesElement.getElementsByTagName("m:tLanguage");

                       for (int i = 0; i < languageNodes.getLength(); i++) {
                           Element languageElement = (Element) languageNodes.item(i);
                           String languageIso = getElementValue(languageElement, "m:sISOCode");
                           String languageName = getElementValue(languageElement, "m:sName");
                           LanguageDTO languageDto = new LanguageDTO();
                           languageDto.setIsoCode(languageIso);
                           languageDto.setName(languageName);
                           languages.add(languageDto);
                       }
                   }
//




                   logger.info("Languages"+ slanguages);
                   countryInfoResponse.setIsoCode(sIsoCode);
                   countryInfoResponse.setName(sName);
                   countryInfoResponse.setCapitalCity(sCapitalCity);
                   countryInfoResponse.setPhoneCode(sPhoneCode);
                   countryInfoResponse.setContinentCode(sContinentCode);
                   countryInfoResponse.setCurrencyIsoCode(sCurrencyISOCode);
                   countryInfoResponse.setCountryFlag(sCountryFlag);
                   countryInfoResponse.setLanguages( languages);
//                   countryInfoResponse.setLanguages(sCountryFlag);
               }


           } catch (Exception e) {
               throw new RuntimeException(e);
           }

        return countryInfoResponse;
    }
}
