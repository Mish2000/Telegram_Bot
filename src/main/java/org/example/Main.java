package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    //This method initializes all the components of the program and forks it with telegram bot.
    public static void main(String[] args) {
        try {
            UserStatistics userStatistics = new UserStatistics();
            ActivityHistory activityHistory = new ActivityHistory();
            BotAdminGUI botAdminGUI = new BotAdminGUI(userStatistics, activityHistory);
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MichaelBot(botAdminGUI, userStatistics, activityHistory));
        } catch (TelegramApiException e) {
            throw  new RuntimeException(e);
        }
    }

}

