package com.nager.api.client.service;

import com.nager.api.client.model.Holiday;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public interface HolidayService {
    Map<LocalDate, String> getLastThreeHolidays(String countryCode);

    Map<String, List<Holiday>> getPublicHolidaysNotOnWeekendsForCountries(int year, String[] countryCodes);

    TreeMap<LocalDate, String> getDeduplicatedListForCountries(Integer year, String countryCode1,
                                                               String countryCode2);
}
