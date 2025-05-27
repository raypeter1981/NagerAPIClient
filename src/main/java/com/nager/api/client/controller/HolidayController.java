package com.nager.api.client.controller;

import com.nager.api.client.model.Holiday;
import com.nager.api.client.service.HolidayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @GetMapping("/lastThreeholidays/{countryCode}")
    public Map<LocalDate, String> getLastThreeHolidays(@PathVariable String countryCode) {
        return holidayService.getLastThreeHolidays(countryCode);
    }

    @GetMapping("/holidaysNotOnWeekend/{year}/{countryCodes}")
    public Map<String, List<Holiday>> getHolidaysNotOnWeekend(@PathVariable Integer year,
                                                              @PathVariable String countryCodes) {
        return holidayService.getPublicHolidaysNotOnWeekendsForCountries(year, countryCodes.split(","));
    }

    @GetMapping("/deduplicatedList/{year}/{countryCode1}/{countryCode2}")
    public TreeMap<LocalDate, String> getDeduplicatedList(@PathVariable Integer year, @PathVariable String countryCode1,
                                                          @PathVariable String countryCode2) {
        return holidayService.getDeduplicatedListForCountries(year, countryCode1, countryCode2);
    }
}