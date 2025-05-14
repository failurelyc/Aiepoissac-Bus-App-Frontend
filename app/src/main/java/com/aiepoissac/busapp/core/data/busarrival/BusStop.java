package com.aiepoissac.busapp.core.data.busarrival;

import com.aiepoissac.busapp.core.data.busservices.BusStopInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public final class BusStop {

    private final int busStopCode;

    private final ArrayList<BusService> busServices;

    public static BusStop getBusArrival(final String busStopCode) {
        try {
            URL url = new URL("https://datamall2.mytransport.sg/ltaodataservice/v3/BusArrival?BusStopCode=" + busStopCode);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("AccountKey","***REMOVED***");
            connection.setRequestProperty("accept","application/json");
            connection.connect();
            if (connection.getResponseCode() != 200) {
                throw new RuntimeException(connection.getResponseMessage());
            } else {
                StringBuilder result = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNext()) {
                    result.append(scanner.nextLine());
                }
                scanner.close();
                JSONParser parser = new JSONParser();
                return new BusStop((JSONObject) parser.parse(result.toString()));
            }
        } catch (IOException | ParseException e) {
            return null;
        }
    }

    private BusStop(final JSONObject data) {
        this.busStopCode = Integer.parseInt((String) data.get("BusStopCode"));
        this.busServices = new ArrayList<>();
        for (Object busService : (JSONArray) data.get("Services")) {
            this.busServices.add(new BusService((JSONObject) busService));
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Estimated Bus Arrival Timings");
        s.append("\n");
        s.append(BusStopInfo.getBusStopInfoString(this.busStopCode, 0, false));
        for (BusService busService : this.busServices) {
            s.append(busService);
            s.append("\n");
        }

        return s.toString();
    }

}
