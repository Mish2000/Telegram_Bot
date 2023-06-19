package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class CovidDataActivity implements ActivityManager {
    //This method is responsible for checking the input of the user, and then it calls sendCovidData method to
    //get an API response.
    @Override
    public void handleUserResponse(Update update, TelegramLongPollingBot bot) {
        long chatId = update.getMessage().getChatId();
        String receivedMessage = update.getMessage().getText();
        String[] splitMessage = receivedMessage.split(" ");
        if (splitMessage.length == 2) {
            String country = splitMessage[0];
            String date = reformatDate(splitMessage[1]);
            sendCovidData(chatId, bot, country, date);
        }else {
            onActivityCommand(chatId,bot);
        }
    }
    //This method allows the user to enter the date in a more comfortable way, by reformatting user input to the requested
    // input of the API provider.
    private String reformatDate(String date) {
        String[] splitDate = date.split("/");
        return splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
    }
    //This method is responsible for making the API request for the user, and then it triggers the bot to respond to
    //the user with the requested data.
    private void sendCovidData(long chatId, TelegramLongPollingBot bot, String country, String date) {
        new Thread(() -> {
            try {
                Map<String, String> headers = new HashMap<>();
                headers.put(Constants.COVID19_API_ELEMENTS[0], Constants.COVID19_API_ELEMENTS[1]);
                String response = Utils.sendApiRequest(
                        Constants.COVID19_API_ELEMENTS[2] + country, headers);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response);
                JsonNode countryData = rootNode.get(0);
                JsonNode casesData = countryData.get("cases");
                String totalCases = "No Data";
                String newCases = "No Data";
                if (casesData != null && casesData.has(date)) {
                    JsonNode dateCasesData = casesData.get(date);
                    if (dateCasesData.has("total")) {
                        totalCases = dateCasesData.get("total").asText();
                    }
                    if (dateCasesData.has("new")) {
                        newCases = dateCasesData.get("new").asText();
                    }
                }
                String covidData = "COVID-19 data for " + country + " on " + date + ":\n" +
                        "Total Cases: " + totalCases +
                        "\nNew Cases: " + newCases;
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(covidData);
                bot.execute(message);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    //This method called when the user chooses this activity, and triggers the bot to send an intro message to the user
    //to continue the process by the other methods.
    @Override
    public void onActivityCommand(long chatId, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Please enter a country name and a date (DD/MM/YYYY) separated by space:\n"+
                "Please make sure to enter a date before 09/03/2023.");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}



