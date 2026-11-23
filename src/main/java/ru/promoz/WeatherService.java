package ru.promoz;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class WeatherService {
    private static final String BASE_URL = "https://api.weather.yandex.ru/v2/forecast";
    private static final String API_KEY_HEADER = "X-Yandex-Weather-Key";

    public static void main(String[] args) {
        String apiKey = System.getenv("YANDEX_WEATHER_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Ошибка: не задана переменная окружения YANDEX_WEATHER_KEY.");
            return;
        }

        double lat = 55.9073; // Широта (Королев)
        double lon = 37.8645; // Долгота (Королев)
        int limit = 5;      // Количество суток прогноза

        try {
            String rawJson = fetchWeatherJson(apiKey, lat, lon, limit);

            // 1. Вывод всего JSON-ответа «как есть»
            System.out.println("=== Полный ответ сервиса (JSON) ===");
            System.out.println(rawJson);
            System.out.println("===================================\n");

            // 2. Извлечение текущей температуры fact.temp
            JSONObject root = new JSONObject(rawJson);
            double currentTemp = root.getJSONObject("fact").getDouble("temp");
            System.out.printf("Текущая температура (fact.temp): %.1f °C%n", currentTemp);

            // 3. Вычисление средней дневной температуры по прогнозу за limit суток
            JSONArray forecasts = root.getJSONArray("forecasts");
            int daysCount = Math.min(limit, forecasts.length());

            if (daysCount == 0) {
                System.out.println("Прогноз по суткам отсутствует в ответе.");
                return;
            }

            double averageTemp = calculateAverageDayTemp(forecasts, daysCount);
            System.out.printf(
                    "Средняя дневная температура по прогнозу за %d сут.: %.2f °C%n",
                    daysCount,
                    averageTemp
            );

        } catch (IOException e) {
            System.err.println("Ошибка сети или API: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Запрос был прерван: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка при обработке данных: " + e.getMessage());
        }
    }

    private static String fetchWeatherJson(String apiKey, double lat, double lon, int limit)
            throws IOException, InterruptedException {
        String requestUrl = BASE_URL + "?lat=" + lat + "&lon=" + lon + "&limit=" + limit;

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header(API_KEY_HEADER, apiKey)
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("API вернуло код " + response.statusCode() + ": " + response.body());
        }

        return response.body();
    }

    private static double calculateAverageDayTemp(JSONArray forecasts, int daysCount) {
        double sumTempAvg = 0;
        for (int i = 0; i < daysCount; i++) {
            double tempAvg = forecasts
                    .getJSONObject(i)
                    .getJSONObject("parts")
                    .getJSONObject("day")
                    .getDouble("temp_avg");
            sumTempAvg += tempAvg;
        }
        return sumTempAvg / daysCount;
    }
}
