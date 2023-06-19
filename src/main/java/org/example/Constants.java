package org.example;

import javax.swing.*;

public class Constants {
    //Visual configuration of the admin graph. The first placeHolder represents the time of the request, and the second one
    // represents the number of the requests.
    public static final String GRAPH_SETTING = "{type: 'line',data: {labels: [%s],datasets: [{label: 'Requests',data: [%s]," +
            "fill: false,borderColor: 'rgb(144, 238, 144)'}]}}";
    //A link to access the API of the graph site.
    public static final String GRAPH_API_LINK = "https://quickchart.io/chart?c=";
    //Array that stores the activities that the bot can handle.
    public static final String[] TELEGRAM_BOT_ACTIVITIES = {"Weather Data", "Numbers Info", "Jokes", "Random Cat Facts", "COVID-19 Data"};
    //Title of the admin GUI.
    public static final String FRAME_TITLE = "Bot Admin Panel";
    //GUI sizes.
    public static final int FRAME_WIDTH = 800;
    public static final int FRAME_HEIGHT = 500;
    //Warning message for admin.
    public static final String ADMIN_WARNING = "You cannot select more than 3 activities.";
    //Column names for the activities table.
    public static final String[] TABLE_COLUMN_NAMES = {"Username", "Activity", "Timestamp"};
    //Sizes of the graph panel.
    public static final int GRAPH_WIDTH = 800;
    public static final int GRAPH_HEIGHT = 200;
    //API keys and links for all the available activities.
    public static final String CAT_API_LINK = "https://catfact.ninja/fact";
    public static final String[] COVID19_API_ELEMENTS = {"X-Api-Key", "qGwFeuVWFm+riN2T4mgzcg==gWdUGS7zk3WYHmTM", "https://api.api-ninjas.com/v1/covid19?country="};
    public static final String JOKES_API_LINK = "https://official-joke-api.appspot.com/jokes/random";
    public static final String NUMBERS_API_LINK = "http://numbersapi.com/";
    public static final String[] WEATHER_API_ELEMENTS = {"bd97f3399413a777d1278b7e27e6726c", "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s"};
    //Telegram Bot info.
    public static final String MICHAEL_BOT_USERNAME = "mishkishmish_bot";
    public static final String MICHAEL_BOT_TOKEN = "6038502056:AAHpxP6Y_ypbpKBwyvVOK5jqENdpEPVH2OA";
    //Frame icon.
    public static final ImageIcon ICON = new ImageIcon("src/icon.png");

}
