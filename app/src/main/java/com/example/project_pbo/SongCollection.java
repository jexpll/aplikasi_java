package com.example.project_pbo;

import java.util.List;

/**
 * Interface umum untuk koleksi lagu (Playlist, Album)
 * Mengikuti prinsip Interface Segregation dan Liskov Substitution
 * Memungkinkan polymorphism untuk operasi umum pada koleksi lagu
 */
public interface SongCollection {
    String getId();
    String getName();
    List<Lagu> getDaftarLagu();
    void tambahLagu(Lagu lagu);
    void hapusLagu(Lagu lagu);
    int getJumlahLagu();
    boolean isEmpty();
}