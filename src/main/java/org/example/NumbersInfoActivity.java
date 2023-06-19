package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class NumbersInfoActivity implements ActivityManager {
    //This method checks user input, in case of valid input it calls the sendNumberFact method to send an API request,
    //in other case it will ask the user again to enter a number.
    @Override
    public void handleUserResponse(Update update, TelegramLongPollingBot bot) {
        String receivedMessage = update.getMessage().getText();
        try {
            int number = Integer.parseInt(receivedMessage);
            sendNumberFact(number, update.getMessage().getChatId(), bot);
        } catch (NumberFormatException e) {
            sendInvalidInputMessage(update.getMessage().getChatId(), bot);
        }
    }
    //This method sends an API request, and gets a response. Then the bot sends the response to the user.
    private void sendNumberFact(int number, long chatId, TelegramLongPollingBot bot) {
        new Thread(() -> {
            try {
                String response = Utils.sendApiRequest(Constants.NUMBERS_API_LINK + number);

                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(response);
                bot.execute(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    //This method is called in case that the user enters input that is not a number.
    //It makes the bot to send a warning to user.
    private void sendInvalidInputMessage(long chatId, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Invalid input. Please enter a valid number.");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    //This method makes the bot to send a message to the user that asks him for an input.
    @Override
    public void onActivityCommand(long chatId, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Please enter a number to get a fact:");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

