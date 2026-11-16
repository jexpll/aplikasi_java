package com.example.project_pbo;

import java.util.List;

/**
 * Interface untuk Music API Service - memungkinkan dependency injection dan testing
 * Mengikuti prinsip Dependency Inversion (D dalam SOLID)
 */
public interface MusicApiService {
    List<Lagu> cariLaguOnline(String query);
}