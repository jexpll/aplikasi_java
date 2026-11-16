package com.example.project_pbo;

import java.util.List;

public interface SongCollection {
    String getId();
    String getName();
    List<Lagu> getDaftarLagu();
    void tambahLagu(Lagu lagu);
    void hapusLagu(Lagu lagu);
    int getJumlahLagu();
    boolean isEmpty();
}