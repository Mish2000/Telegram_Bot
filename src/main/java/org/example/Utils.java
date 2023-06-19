package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
public class Utils {

    //This method is used when I do not need to put an additional headers to the url.
    public static String sendApiRequest(String url) {
        return sendApiRequest(url, null);
    }
    // This method returns an API request string, that is built from a specific url plus headers (if needed).
    public static String sendApiRequest(String url, Map<String, String> headers) {
        String apiResponse = "";
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            //This command updates the server that the program wants to get data.
            connection.setRequestMethod("GET");
            // Add request headers
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            // Reading the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                content.append(inputLine);
            }
            reader.close();
            connection.disconnect();
            //Converting stringBuilder into a single string which represents the response.
            apiResponse = content.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResponse;
    }
}
