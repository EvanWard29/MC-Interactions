package uk.co.evanward.twitchinteractions.twitch.server;

import org.json.JSONObject;
import spark.Spark;
import uk.co.evanward.twitchinteractions.helpers.TwitchHelper;

import static spark.Spark.*;

public class SparkServer
{
    public static void start() {
        init();

        // The page the access token is sent to
        get("/twitch/auth/redirect", (request, response) -> {
            // Return JavaScript to extract and submit the access token
            response.body("<html> <body> <p id=\"response\"></p> </body> <script> let fragment = window.location.hash; fetch(\"http://localhost:4567/twitch/auth/token\", { method: \"POST\", headers: { \"Accept\": \"application/json\", \"Content-Type\": \"application/json\" }, body: JSON.stringify(getHashParams(fragment)) }).then(response => response.json()).then(response => { if (response.success) { document.getElementById(\"response\").innerHTML = \"Authentication Successful! You may now close this tab and return to minecraft.\"; } else { document.getElementById(\"response\").innerHTML = response.error; } }); function getHashParams() { var hashParams = {}; var e, a = /\\+/g, r = /([^&;=]+)=?([^&;]*)/g, d = function (s) { return decodeURIComponent(s.replace(a, \" \")); }, q = window.location.hash.substring(1); while (e = r.exec(q)) hashParams[d(e[1])] = d(e[2]); return hashParams; } </script></html>");
            response.status(200);

            return response.body();
        });

        // Post request made by JavaScript containing returned URI fragment data
        post("/twitch/auth/token", (request, response) -> {
            JSONObject body = new JSONObject(request.body());

            if (body.has("error")) {
                response.body(new JSONObject().put("success", false).put("error", "Error obtaining access token! Be sure to click 'Authorize' in order for Twitch Interactions to work!").toString());
                response.status(400);
            } else {
                // Save access token to config
                TwitchHelper.setAccessToken(body.getString("access_token"));

                response.body(new JSONObject().put("success", true).toString());
                response.status(200);
            }

            new Thread(() -> {
                try {
                    Thread.sleep(10000);
                    SparkServer.stop();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            response.type("application/json");
            return response.body();
        });
    }

    public static void stop() {
        Spark.stop();
    }
}
