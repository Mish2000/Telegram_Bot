package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CatFactActivity implements ActivityManager {
    //This method sends a cat fact to the telegram user, by getting an API response from the target link.
    public void sendCatFact(long chatId, TelegramLongPollingBot bot) {
        new Thread(() -> {
            try {
                String response = Utils.sendApiRequest(Constants.CAT_API_LINK);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response);
                String catFact = jsonNode.get("fact").asText();
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(catFact);
                bot.execute(message);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    //In this case, this method should not perform anything, because the random fact is sent to the user without getting
    //any additional input from the user.
    @Override
    public void handleUserResponse(Update update, TelegramLongPollingBot bot) {

    }
    //This method is responsible for sending a message to the user that indicates his API request choice, and then it calls
    //the sendCatFact method to send the actual fact.
    @Override
    public void onActivityCommand(long chatId, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Here's a random cat fact for you:");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        sendCatFact(chatId, bot);
    }
}

