package org.example;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
public class BotAdminGUI extends JFrame {
    //This object displays the activities array on the GUI.
    private final JList<String> activitiesList;
    //This object stores the activities of all users of the bot, and allows the GUI to display it for admin.
    private final ActivityHistory activityHistory;
    //This object gets the statistics of the users and displays it next to the labels in the right corner of the GUI.
    private final UserStatistics userStatistics;
    //This object stores the selected activities by the admin that the bot will offer to the user.
    private final List<String> selectedActivities = new ArrayList<>();

    //This is the constructor of admins GUI to control the bot.
    public BotAdminGUI(UserStatistics userStatistics, ActivityHistory activityHistory) {
        this.userStatistics = userStatistics;
        this.activityHistory = activityHistory;
        this.setTitle(Constants.FRAME_TITLE);
        this.setSize(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setIconImage(Constants.ICON.getImage());
        // List of activities
        //How to choose the activities? (Instructions for admin):
        //Hold the Ctrl button and click the left mouse button on up to three activities that you want the bot to offer
        //to the user, if you try to check more than three, the next select option will be blocked.
        this.activitiesList = new JList<>(Constants.TELEGRAM_BOT_ACTIVITIES);
        this.activitiesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.activitiesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                //Allowing the admin to add to the bot activities from the activity list.
                this.selectedActivities.clear();
                for (int i : this.activitiesList.getSelectedIndices()) {
                    this.selectedActivities.add(Constants.TELEGRAM_BOT_ACTIVITIES[i]);
                }
            }
        });
        // The admin is not allowed to choose more than 3 activities.
        this.activitiesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && this.activitiesList.getSelectedIndices().length > 3) {
                JOptionPane.showMessageDialog(null, Constants.ADMIN_WARNING);
                //Let the admin choose activities again.
                this.activitiesList.clearSelection();
            }
        });
        //Adding the object that stores all the activities of the bot.
        this.add(new JScrollPane(this.activitiesList), BorderLayout.CENTER);
        // User Statistics labels in the right corner of the GUI.
        JLabel totalRequests = new JLabel();
        JLabel totalUniqueUsers = new JLabel();
        JLabel mostActiveUser = new JLabel();
        JLabel mostPopularActivity = new JLabel();
        //Panel that stores all the statistics JLabel.
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(4, 1));
        statsPanel.add(totalRequests);
        statsPanel.add(totalUniqueUsers);
        statsPanel.add(mostActiveUser);
        statsPanel.add(mostPopularActivity);
        this.add(statsPanel, BorderLayout.EAST);
        // Creating a table that stores information for the last 10 activities that requested by users.
        //I use an object for each cell in the table to be to store both strings and LocalDateTime objects.
        Object[][] data = new Object[10][3];
        JTable historyTable = new JTable(data, Constants.TABLE_COLUMN_NAMES);
        TableColumnModel columnModel = historyTable.getColumnModel();
        TableColumn column1 = columnModel.getColumn(0);
        column1.setPreferredWidth(0);
        TableColumn column2 =columnModel.getColumn(1);
        column2.setPreferredWidth(0);
        TableColumn column3 = columnModel.getColumn(2);
        column3.setPreferredWidth(30);
        this.add(new JScrollPane(historyTable), BorderLayout.WEST);
        // Creating components for displaying the graph.
        JPanel graphPanel = new JPanel();
        graphPanel.setLayout(new BorderLayout());
        JLabel graphLabel = new JLabel();
        graphPanel.add(graphLabel, BorderLayout.CENTER);
        graphPanel.setPreferredSize(new Dimension(Constants.GRAPH_WIDTH, Constants.GRAPH_HEIGHT));
        this.add(graphPanel, BorderLayout.SOUTH);
        // update the labels, table and chart every 1 second
        new Timer(1000, e -> SwingUtilities.invokeLater(() -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            totalRequests.setText("Total Requests: " + userStatistics.getTotalRequests());
            totalUniqueUsers.setText("Total Unique Users: " + userStatistics.getTotalUniqueUsers());
            mostActiveUser.setText("Most Active User: " + userStatistics.getMostActiveUser());
            mostPopularActivity.setText("Most Popular Activity: " + userStatistics.getMostPopularActivity());
            //List that stores all ActivityEvent objects (parameters of every activity).
            List<ActivityEvent> history = activityHistory.getHistory();
            for (int i = 0; i < history.size(); i++) {
                //Filling the table with info of every requested activity.
                ActivityEvent entry = history.get(i);
                historyTable.setValueAt(entry.getUsername(), i, 0);
                historyTable.setValueAt(entry.getActivity(), i, 1);
                LocalDateTime timestamp = entry.getTimestamp();
                String formattedTimestamp = timestamp.format(formatter);
                historyTable.setValueAt(formattedTimestamp, i, 2);
            }
            //This loop ensures that the old activities data is deleted.
            for (int i = history.size(); i < 10; i++) {
                historyTable.setValueAt(null, i, 0);
                historyTable.setValueAt(null, i, 1);
                historyTable.setValueAt(null, i, 2);
            }
            String graphUrl = getGraphUrl();
            try {
                URL url = new URL(graphUrl);
                BufferedImage image = ImageIO.read(url);
                //Setting the graph to fit to the graphPanel.
                Image scaledImg = image.getScaledInstance(graphPanel.getWidth(), graphPanel.getHeight(), Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(scaledImg);
                //Add the image of the graph to his label.
                graphLabel.setIcon(icon);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        })).start();
        this.setVisible(true);
    }
    //This method returns an updated string that represents an API link of the graph that changes related to the user
    //requests. This method allows refreshing the graph after every new API request, and sort by corresponding order
    //the API requests on the graph, by the time when they were requested.
    public String getGraphUrl() {
        Map<LocalDateTime, Integer> requestCounts = this.userStatistics.getRequestCounts();
        //I preferred to show only the time of the requests on the graph to save space.
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        //Convert the Map to ArrayList to sort the requests on the graph by their time.
        List<Map.Entry<LocalDateTime, Integer>> sortedRequests = requestCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();
        //Converting the sortedRequests list keys to a single String to use it later in the graph link.
        String labels = sortedRequests.stream()
                .map(entry -> "'" + entry.getKey().format(timeFormatter) + "'")
                .collect(Collectors.joining(","));
        //Converting the sortedRequests list values to a single String to use it later in the graph link.
        String data = sortedRequests.stream()
                .map(entry -> entry.getValue().toString())
                .collect(Collectors.joining(","));
        //Replace '[%s]' placeholders by the current time labels and the current data which represent the number of requests
        //for every dot.
        String graphConfig = String.format(Constants.GRAPH_SETTING, labels, data);
        //Converting graphConfig String into url format.
        String finalConfig = URLEncoder.encode(graphConfig, StandardCharsets.UTF_8);

        return Constants.GRAPH_API_LINK + finalConfig;
    }
    //This method returns the list copy of only the activities that admin selects for the bot. (The bot class uses this method).
    public List<String> getAvailableActivities() {
        return new ArrayList<>(this.selectedActivities);
    }

}
