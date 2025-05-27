package com.nager.api.client.service;

import com.nager.api.client.constants.Constants;
import com.nager.api.client.model.Holiday;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Stream;

@Service
public class HolidayServiceImpl implements HolidayService {
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * This function will invoke the Nager API Get endpoint to retrieve Array of holidays for a country
     */
    private Holiday[] getHolidaysOfCountry(int year, String countryCode) throws RestClientException {
        String url = String.format(Constants.NAGER_API_V_3_PUBLIC_HOLIDAYS, year, countryCode);
        return restTemplate.getForObject(url, Holiday[].class);
    }

    /**
     * This function will get holidays for current and previous year, then it will append those into a array and then
     * get the index of array which is greater than current date. Nager API sends the holidays in ascending date
     * so we can combine the arrays directly. Then it will retrieve the last 3 celebrated holidays
     */
    @Override
    public Map<LocalDate, String> getLastThreeHolidays(String countryCode) {
        Holiday[] holidays = getHolidaysOfCountry(Year.now().getValue(), countryCode);
        Holiday[] lastYearHolidays = getHolidaysOfCountry(Year.now().getValue() - 1, countryCode);

        Holiday[] combinedHolidays =
        Stream.concat(Arrays.stream(lastYearHolidays), Arrays.stream(holidays)).toArray(Holiday[]::new);

        Map<LocalDate, String> retHolidayMap = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        for(int i = 0 ; i <= combinedHolidays.length ; i++) {
            if(today.isBefore(combinedHolidays[i].getDate())) {
               for(int j = i - 3 ; j < i  ; j++) {
                   if(j >= 0)
                    retHolidayMap.put(combinedHolidays[j].getDate(), combinedHolidays[j].getLocalName());
               }
               break;
            }
        }
        return retHolidayMap;
    }

    /**
     * This function will call getPublicHolidaysNotOnWeekendsForOneCountry function for all the comma seperated
     * country codes supplied
     */
    @Override
    public Map<String, List<Holiday>> getPublicHolidaysNotOnWeekendsForCountries(int year, String[] countryCodes){
        Map<String, List<Holiday>> allCountriesHolidays = new HashMap<>();
        for(String countryCode : countryCodes){
            allCountriesHolidays.put(countryCode, getPublicHolidaysNotOnWeekendsForOneCountry(year, countryCode));
        }
        return allCountriesHolidays;
    }

    /**
     * This function will get all the holidays of one country and then filter by Public type and non-weekend and
     * then sort the list in descending order
     */
    private List<Holiday> getPublicHolidaysNotOnWeekendsForOneCountry(int year, String countryCode){
        Holiday[] holidays = getHolidaysOfCountry(year, countryCode);
        if(holidays == null)
            return null;

        return Arrays.stream(holidays).parallel()
                .filter(holiday -> holiday.getTypes().contains(Constants.PUBLIC))
                .filter(holiday -> !(holiday.getDate().getDayOfWeek() == DayOfWeek.SATURDAY ||
                        holiday.getDate().getDayOfWeek() == DayOfWeek.SUNDAY ))
                .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
                .toList();
    }

    /**
     * This function will get Arrays of holidays of each country then use a TreeMap for duplicate elimination and
     * natural sorting on LocalDate
     */
    @Override
    public TreeMap<LocalDate, String> getDeduplicatedListForCountries(Integer year, String countryCode1,
                                                                      String countryCode2) {
        TreeMap<LocalDate, String> sortedHolidays = new TreeMap<>();
        Holiday[] holidaysOfCountry1 = getHolidaysOfCountry(year, countryCode1);
        Holiday[] holidaysOfCountry2 = getHolidaysOfCountry(year, countryCode2);
        for (Holiday holiday : holidaysOfCountry1) {
            sortedHolidays.put(holiday.getDate(), holiday.getLocalName());
        }
        for (Holiday holiday : holidaysOfCountry2) {
            sortedHolidays.put(holiday.getDate(), holiday.getLocalName());
        }
        return sortedHolidays;
    }
}