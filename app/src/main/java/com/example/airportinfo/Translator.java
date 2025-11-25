package com.example.airportinfo;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Translator {
    private static final Map<String, String> ruToEnStatus = new HashMap<>();
    private static final Map<String, String> ruToEnAirlines = new HashMap<>();
    private static final Map<String, String> ruToEnCities = new HashMap<>();

    static {
        ruToEnStatus.put("отменен", "Canceled");
        ruToEnStatus.put("вылетел", "Departed");
        ruToEnStatus.put("задержан", "Delayed");
        ruToEnStatus.put("ожидается", "Еxpected");
        ruToEnStatus.put("по расписанию", "On schedule");
        ruToEnStatus.put("посадка", "Boarding");
        ruToEnStatus.put("сменить язык", "Change language");

        ruToEnAirlines.put("Аэрофлот", "Aeroflot");
        ruToEnAirlines.put("Победа", "Pobeda");
        ruToEnAirlines.put("Уральские авиалинии", "Ural Airlines");

        ruToEnCities.put("Москва", "Moscow");
        ruToEnCities.put("Санкт-Петербург", "Saint-Petersburg");
        ruToEnCities.put("Казань", "Kazan");
        ruToEnCities.put("Киев", "Kiev");
        ruToEnCities.put("Сочи", "Sochi");
    }

    public static String translateStatus(String ru) {
        if (ru == null) return "";
        return ruToEnStatus.getOrDefault(ru.toLowerCase(Locale.ROOT), ru);
    }


    public static String translateAirline(String ru) {
        return ruToEnAirlines.getOrDefault(ru, ru);
    }

    public static String translateCity(String ru) {
        return ruToEnCities.getOrDefault(ru, ru);
    }
}
