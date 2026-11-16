package com.example.project_pbo;

import java.util.List;

public interface MusicApiService {
    List<Lagu> cariLaguOnline(String query);
}