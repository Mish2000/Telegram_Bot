package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.*;

public class MichaelBot extends TelegramLongPollingBot {
    //This Map stores the keys, which is the activity names related to a specific activity class object.
    private final Map<String, ActivityManager> activityManagers;
    //This Map stores the ID (key) of each user next to his last activity (value).
    private final Map<Long, ActivityManager> lastActivityByUser;
    //This field stores the data about all user required statistics. It helps to display to the admin the specific
    //required statistics.
    private final UserStatistics userStatistics;
    //This field is used to store the data of every activity event, which contains the username, the activity name and
    //the accurate timeStamp for the event.
    private final ActivityHistory activityHistory;
    //This field is responsible for making the bot being controlled by admin GUI.
    private final BotAdminGUI botAdminGUI;

    //MichaelBot constructor.
    public MichaelBot(BotAdminGUI botAdminGUI, UserStatistics userStatistics, ActivityHistory activityHistory) {
        this.userStatistics = userStatistics;
        this.activityHistory = activityHistory;
        this.botAdminGUI = botAdminGUI;
        this.lastActivityByUser = new HashMap<>();
        this.activityManagers = new HashMap<>();
        this.activityManagers.put(Constants.TELEGRAM_BOT_ACTIVITIES[0], new WeatherDataActivity());
        this.activityManagers.put(Constants.TELEGRAM_BOT_ACTIVITIES[1], new NumbersInfoActivity());
        this.activityManagers.put(Constants.TELEGRAM_BOT_ACTIVITIES[2], new JokesActivity());
        this.activityManagers.put(Constants.TELEGRAM_BOT_ACTIVITIES[3], new CatFactActivity());
        this.activityManagers.put(Constants.TELEGRAM_BOT_ACTIVITIES[4], new CovidDataActivity());
    }

    //Getter for bot username.
    @Override
    public String getBotUsername() {
        return Constants.MICHAEL_BOT_USERNAME;
    }

    //Getter for bot token.
    @Override
    public String getBotToken() {
        return Constants.MICHAEL_BOT_TOKEN;
    }

    //This method is responsible for the communication between telegram bot and the user.
    @Override
    public void onUpdateReceived(Update update) {
        long chatId;
        User user;
        String username;
        String activity;
        //If some of the activity buttons has been clicked.
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            //For example, activity = Numbers Info;
            activity = callbackData.split("_")[1];
            chatId = update.getCallbackQuery().getMessage().getChatId();
            user = update.getCallbackQuery().getFrom();
            if (user.getUserName() != null) {
                username = user.getUserName();
            } else {
                username = user.getFirstName();
            }
            //Adding to the history and the user statistics a new activity event to update the information in the GUI.
            this.userStatistics.addActivity(user, activity);
            this.activityHistory.addActivity(new ActivityEvent(username, activity, LocalDateTime.now()));
            //This object stores the current activity that is requested by user.
            ActivityManager activityManager = this.activityManagers.get(activity);
            //If the activity is detected, the bot starting the conversation.
            if (activityManager != null) {
                activityManager.onActivityCommand(chatId, this);
                //This map allows storing the last activity per user.
                this.lastActivityByUser.put(user.getId(), activityManager);
            }
            //If the user sends something to the bot.
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String receivedMessage = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            //This command will start the functionality of the bot.
            if (receivedMessage.startsWith("/start")) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Select an activity:");
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<String> activities = getAvailableActivities();
                //Creating the activity buttons for the user.
                for (String act : activities) {
                    InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                    inlineKeyboardButton.setText(act);
                    inlineKeyboardButton.setCallbackData("activity_" + act);
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowInline.add(inlineKeyboardButton);
                    rowsInline.add(rowInline);
                }
                markup.setKeyboard(rowsInline);
                message.setReplyMarkup(markup);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                //If a user types something that is not "/start", it means that it is some necessary input
                //that some activities require. In that case, handleUserResponse method will be called again.
                ActivityManager activityManager = this.lastActivityByUser.get(update.getMessage().getFrom().getId());
                if (activityManager != null) {
                    activityManager.handleUserResponse(update, this);
                }
            }
        }
    }

    //The bot uses this method to get the activities that been chosen by the admin.
    //Every time the admin will decide to change the available activities, the user will have to type "/start" again
    //to see the new available activities.
    public List<String> getAvailableActivities() {
        return this.botAdminGUI.getAvailableActivities();
    }

}
