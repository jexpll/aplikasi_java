package com.example.project_pbo;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Adapter API Deezer. Parsing JSON dilakukan berdasarkan field, bukan urutan teks. */
public class DeezerApiService implements MusicApiService {
    private static final String API_URL = "https://api.deezer.com/search?q=%s&limit=10";
    private static final int TIMEOUT_MS = 5000;

    @Override
    public List<Lagu> cariLaguOnline(String query) {
        List<Lagu> hasil = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return hasil;
        HttpURLConnection connection = null;
        try {
            String encoded = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8.name());
            connection = (HttpURLConnection) new URL(String.format(API_URL, encoded)).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) return hasil;

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
            }
            JSONArray data = new JSONObject(response.toString()).optJSONArray("data");
            if (data == null) return hasil;
            for (int i = 0; i < data.length(); i++) {
                JSONObject track = data.optJSONObject(i);
                if (track == null) continue;
                JSONObject artist = track.optJSONObject("artist");
                JSONObject album = track.optJSONObject("album");
                int seconds = track.optInt("duration", 0);
                hasil.add(new Lagu(String.valueOf(track.optLong("id")),
                        track.optString("title", "Tanpa judul"),
                        (seconds / 60) + ":" + String.format("%02d", seconds % 60),
                        "Pop/Rock", track.optString("preview", ""),
                        artist == null ? "Unknown Artist" : artist.optString("name", "Unknown Artist"),
                        album == null ? "" : album.optString("cover_medium", "")));
            }
        } catch (Exception e) {
            System.err.println("API error: " + e.getMessage());
        } finally {
            if (connection != null) connection.disconnect();
        }
        return hasil;
    }
}