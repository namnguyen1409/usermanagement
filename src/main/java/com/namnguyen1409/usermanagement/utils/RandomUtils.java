package com.namnguyen1409.usermanagement.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RandomUtils {
    List<String> ajd = List.of("happy", "sad", "angry", "excited", "bored", "confused", "curious", "disappointed", "frustrated", "grateful");
    List<String> noun = List.of("cat", "dog", "bird", "fish", "tiger", "lion", "elephant", "giraffe", "zebra", "monkey");

    public String generateRandomUsername() {
        var random = new Random();
        int number = 10000 + random.nextInt(90000);
        return String.format("%s_%s%d", getRandomElement(ajd), getRandomElement(noun), number);
    }

    public String generateRandomFirstName() {
        return getRandomElement(ajd);
    }
    public String generateRandomLastName() {
        return getRandomElement(noun);
    }

    public LocalDate generateRandomBirthday() {
        var random = new Random();
        int year = 1906 + random.nextInt(100);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        return LocalDate.of(year, month, day);
    }

    public String generateRandomEmail(String username) {
        return String.format("%s@%s.com", username, getRandomElement(noun));
    }

    public Boolean generateRandomBoolean() {
        return Math.random() < 0.5;
    }

    public String generateRandomPhone() {
        var random = new Random();
        int number = 100000000 + random.nextInt(900000000);
        return String.format("0%s", number);
    }

    public String generateRandomAddress() {
        var random = new Random();
        int number = 100 + random.nextInt(900);
        return String.format("%s %s St.", number, getRandomElement(noun));
    }


    private String getRandomElement(List<String> list) {
        int randomIndex = (int) (Math.random() * list.size());
        return list.get(randomIndex);
    }


}
