package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class JokesActivity implements ActivityManager {
    //This method sends an API request, constructs a response and then triggers the bot to send it to the user.
    public void sendJoke(long chatId, TelegramLongPollingBot bot) {
        new Thread(() -> {
            try {
                String response = Utils.sendApiRequest(Constants.JOKES_API_LINK);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response);
                String jokeSetup = jsonNode.get("setup").asText();
                String jokePunchline = jsonNode.get("punchline").asText();
                String joke = jokeSetup + "\n\n" + jokePunchline;
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(joke);
                bot.execute(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    //In this case, this method should not perform anything, because the random joke is sent to the user without getting
    //any additional input from the user.
    @Override
    public void handleUserResponse(Update update, TelegramLongPollingBot bot) {

    }
    //This method triggers the bot to send an intro message to the user, after he clicks the Jokes activity button.
    //Then it calls the sendJoke method to build an API response to the user.
    @Override
    public void onActivityCommand(long chatId, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Here is a joke for you:");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        sendJoke(chatId, bot);
    }
}


