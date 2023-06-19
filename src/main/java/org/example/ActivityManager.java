package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ActivityManager {
    //Those methods are implemented in every activity type class.
    //This method gets an update object to have the ability to read a user message, and to respond only in case that the
    //user will send an appropriate input.
    void handleUserResponse(Update update, TelegramLongPollingBot bot);
    //This method gets chatId that represent the current chat, and gets a telegram bot that will send a
    //response message after the user clicks some activity button.
    void onActivityCommand(long chatId, TelegramLongPollingBot bot);

}
