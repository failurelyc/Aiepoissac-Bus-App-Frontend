package com.aiepoissac.busapp.core.data.busservices;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

final class DataConnection {

    static ArrayList<JSONObject> getData(String description, int count) {
        try {
            URL url = null;
            if (description.equals("BUS STOPS")) {
                url = new URL("https://datamall2.mytransport.sg/ltaodataservice/BusStops?$skip=" + count);
            } else if (description.equals("BUS ROUTES")) {
                url = new URL("https://datamall2.mytransport.sg/ltaodataservice/BusRoutes?$skip=" + count);
            } else if (description.equals("BUS SERVICES")) {
                url = new URL("https://datamall2.mytransport.sg/ltaodataservice/BusServices?$skip=" + count);
            }
            JSONObject jsonObject = getJsonObject(url);
            @SuppressWarnings("unchecked")
            ArrayList<JSONObject> jsonArray = (ArrayList<JSONObject>) jsonObject.get("value");
            return jsonArray;
        } catch (IOException | ParseException | NullPointerException e) {
            return new ArrayList<>();
        }
    }

    private static JSONObject getJsonObject(URL url) throws IOException, ParseException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("AccountKey", "***REMOVED***");
        connection.setRequestProperty("accept", "application/json");
        connection.connect();
        StringBuilder result = new StringBuilder();
        Scanner scanner = new Scanner(connection.getInputStream());
        while (scanner.hasNext()) {
            result.append(scanner.nextLine());
        }
        scanner.close();
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(result.toString());
    }

}
