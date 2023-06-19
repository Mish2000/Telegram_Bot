package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;

public class WeatherDataActivity implements ActivityManager {
    //This method is called when the user types the city name.
    //The method gets the user input and calls the other method to make an API request.
    @Override
    public void handleUserResponse(Update update, TelegramLongPollingBot bot) {
        String city = update.getMessage().getText();
        sendWeather(city, update.getMessage().getChatId(), bot);
    }
    //This method triggers the bot to send a message to the user, after the user clicks on the activity button.
    @Override
    public void onActivityCommand(long chatId, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Please enter the city name for the weather data:");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    //This method sends an API request, then it reformats the response for the user by converting the default temperature
    //in the API to Celsius and round off the unnecessary decimal digits. Then the bot sends the constructed message
    //to the user.
    private void sendWeather(String city, long chatId, TelegramLongPollingBot bot) {
        new Thread(() -> {
            try {
                String url = String.format(Constants.WEATHER_API_ELEMENTS[1], URLEncoder.encode(city, StandardCharsets.UTF_8),
                        Constants.WEATHER_API_ELEMENTS[0]);
                String response = Utils.sendApiRequest(url);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response);
                JsonNode mainNode = root.path("main");
                JsonNode weatherArray = root.path("weather");
                JsonNode firstWeatherNode = weatherArray.get(0);
                //Converting the temperature to Celsius for user comfort.
                double tempInKelvin = mainNode.path("temp").asDouble();
                double tempInCelsius = tempInKelvin - 273.15;

                String description = "Could not get weather data.";
                if (firstWeatherNode != null) {
                    description = firstWeatherNode.path("description").asText();
                }
                //.2 in the temperature placeholder allows printing the number with only 2 numbers after the float point.
                String messageText = String.format("The current temperature is %.2f°C with %s", tempInCelsius, description);

                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(messageText);

                bot.execute(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}



