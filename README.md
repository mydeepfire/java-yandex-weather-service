# Сервис по получению метеорологических данных (Яндекс Погода API)

Консольное Java-приложение для получения метеорологических данных через API Яндекс Погоды (`v2/forecast`).

## Функциональность

1. Выполняет `GET`-запрос к `https://api.weather.yandex.ru/v2/forecast` с параметрами координат (`lat`, `lon`), количеством суток прогноза (`limit`) и заголовком авторизации `X-Yandex-Weather-Key`.
2. Выводит в консоль полный JSON-ответ от сервиса без изменений.
3. Извлекает и выводит текущую температуру (`fact.temp`).
4. Вычисляет и выводит среднюю дневную температуру (`forecasts[*].parts.day.temp_avg`) за указанное количество суток (`limit`).

## Требования

- **Java 17** или выше
- **Maven 3.6+**
- Действующий API-ключ [Яндекс Погоды]

## Настройка и запуск

В целях безопасности API-ключ не хранится в исходном коде и передаётся через переменную окружения `YANDEX_WEATHER_KEY`.

### Запуск через IntelliJ IDEA

1. Откройте **Run -> Edit Configurations...**
2. Выберите класс `ru.promoz.WeatherService`.
3. В поле **Environment variables** добавьте:
   ```text
   YANDEX_WEATHER_KEY=ваш_api_ключ
   ```
4. Нажмите **Apply** и запустите приложение (**Run**).

### Запуск через терминал (Maven)

**Linux / macOS:**
```bash
export YANDEX_WEATHER_KEY="ваш_api_ключ"
mvn compile exec:java -Dexec.mainClass="ru.promoz.WeatherService"
```

**Windows (PowerShell):**
```powershell
\$env:YANDEX_WEATHER_KEY="ваш_api_ключ"
mvn compile exec:java -Dexec.mainClass="ru.promoz.WeatherService"
```
