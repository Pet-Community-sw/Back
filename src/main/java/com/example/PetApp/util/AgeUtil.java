package com.example.PetApp.util;

import java.time.LocalDate;
import java.time.Period;

public class AgeUtil {

    public static int CalculateAge(LocalDate dogBirthDate) {
        return Period.between(dogBirthDate, LocalDate.now()).getYears();
    }
}
