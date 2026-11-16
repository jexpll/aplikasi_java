package com.example.project_pbo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private static final Map<String, Bitmap> cache = new ConcurrentHashMap<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void load(String urlString, ImageView imageView) {
        if (urlString == null || urlString.isEmpty()) return;

        imageView.setTag(urlString);

        Bitmap cached = cache.get(urlString);
        if (cached != null) {
            imageView.setImageBitmap(cached);
            return;
        }

        executor.execute(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.connect();
                InputStream in = conn.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(in);
                in.close();

                if (bmp != null) {
                    cache.put(urlString, bmp);
                    imageView.post(() -> {
                        Object tag = imageView.getTag();
                        if (tag != null && tag.equals(urlString)) {
                            imageView.setImageBitmap(bmp);
                        }
                    });
                }
            } catch (Exception ignored) {
            }
        });
    }
}
