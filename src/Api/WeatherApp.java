package Api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WeatherApp {

    public static String getResponse () {
        try {
            String dataUrl = "https://samples.openweathermap.org/data/2.5/forecast/hourly?q=London,us&appid=b6907d289e10d714a6e88b30761fae22";
            URL url = new URL(dataUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } else {
                System.out.println("Error: HTTP request failed with response code " + responseCode);
            }

            return null;
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static JSONObject getData(String date, String key) throws ParseException {
        String jsonData = getResponse();
        JSONObject dataObject;
        JSONObject finalDataObject = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date targetDate = sdf.parse(date);

        try {
            if (jsonData != null) {
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray listArray = jsonObject.getJSONArray("list");

                for (int i = 0; i < listArray.length(); i++) {
                    dataObject = listArray.getJSONObject(i);
                    String dtTxt = dataObject.getString("dt_txt");
                    Date currentDate = sdf.parse(dtTxt.substring(0, 10));

                    if (currentDate.compareTo(targetDate) == 0) {
                        finalDataObject = dataObject.getJSONObject(key);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return finalDataObject;
    }


    private static double getWeather(String date) throws ParseException {
        JSONObject result = getData(date, "main");

        return result != null ? result.getBigDecimal("temp").doubleValue() : 0.0;
    }

    private static double getWindSpeed(String date) throws ParseException {
        JSONObject result = getData(date, "wind");

        return result != null ? result.getDouble("speed") : 0.0;
    }

    private static double getPressure(String date) throws ParseException {
        JSONObject result = getData(date, "main");

        return result != null ? result.getBigDecimal("pressure").doubleValue() : 0;
    }

    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Get weather\n2. Get Wind Speed\n3. Get Pressure\n0. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter the date (YYYY-MM-DD): ");
                String date = scanner.nextLine();
                double temperature = getWeather(date);
                System.out.println("Temperature on "+ date +" is "+ temperature);
            } else if (choice == 2) {
                System.out.print("Enter the date (YYYY-MM-DD): ");
                String date = scanner.nextLine();
                double windSpeed = getWindSpeed(date);
                System.out.println("Wind Speed on "+date +" is "+ windSpeed);
            } else if (choice == 3) {
                System.out.print("Enter the date (YYYY-MM-DD): ");
                String date = scanner.nextLine();
                double pressure = getPressure(date);
                System.out.println("Pressure on "+date +" is "+  pressure);
            } else if (choice == 0) {
                System.out.println("Exiting...");
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}

