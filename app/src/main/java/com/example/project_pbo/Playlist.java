package com.example.project_pbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Kelas Playlist merepresentasikan playlist pengguna
 * Implementasi SongCollection untuk polymorphism
 */
public class Playlist implements SongCollection {
    private final String idPlaylist;
    private String namaPlaylist;
    private final List<Lagu> daftarLagu;

    public Playlist(String idPlaylist, String namaPlaylist) {
        this.idPlaylist = idPlaylist;
        this.namaPlaylist = namaPlaylist;
        this.daftarLagu = new ArrayList<>();
    }

    @Override
    public String getId() {
        return idPlaylist;
    }

    @Override
    public String getName() {
        return namaPlaylist;
    }

    public void setNamaPlaylist(String namaPlaylist) {
        this.namaPlaylist = namaPlaylist;
    }

    @Override
    public List<Lagu> getDaftarLagu() {
        // Return unmodifiable view untuk encapsulation
        return new ArrayList<>(daftarLagu);
    }

    @Override
    public void tambahLagu(Lagu lagu) {
        if (lagu != null && !daftarLagu.contains(lagu)) {
            daftarLagu.add(lagu);
        }
    }

    @Override
    public void hapusLagu(Lagu lagu) {
        daftarLagu.remove(lagu);
    }

    @Override
    public int getJumlahLagu() {
        return daftarLagu.size();
    }

    @Override
    public boolean isEmpty() {
        return daftarLagu.isEmpty();
    }

    // Legacy methods untuk backward compatibility
    public String getIdPlaylist() {
        return idPlaylist;
    }

    public String getNamaPlaylist() {
        return namaPlaylist;
    }

    public void putarPlaylist() {
        System.out.println("Memutar playlist: " + namaPlaylist + " (" + daftarLagu.size() + " lagu)");
        for (Lagu lagu : daftarLagu) {
            lagu.putar();
        }
    }

    public void acakLagu() {
        Collections.shuffle(daftarLagu);
        System.out.println("Playlist " + namaPlaylist + " diacak.");
    }
}
