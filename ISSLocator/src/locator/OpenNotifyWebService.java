package locator;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.stream.Collectors;

public class OpenNotifyWebService implements ISSWebService {
    String apiGetRequest(double lat, double lon) throws Exception {
       URL url = new URL(String.format("http://api.open-notify.org/iss-pass.json?lat=%f&lon=%f&n=1", lat, lon));
       return new Scanner(url.openStream()).tokens().collect(Collectors.joining(""));
    }

    String parseJSONPayload(String json) {
        JSONObject parsed = new JSONObject(json);
        String message = parsed.getString("message");
        if (message.equalsIgnoreCase("success")) {
            JSONObject response = (JSONObject)
              parsed.getJSONArray("response").get(0);
            return Long.toString(response.getLong("risetime"));
        }

        throw new RuntimeException(parsed.getString("reason"));
    }

    public long fetchISSFlyOverData(double lat, double lon) {
        try {
            String parsedData = parseJSONPayload(apiGetRequest(lat, lon));
            return Long.parseLong(parsedData);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
