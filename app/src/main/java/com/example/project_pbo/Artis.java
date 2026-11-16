package com.example.project_pbo;

import java.util.ArrayList;
import java.util.List;

/**
 * Kelas Artis merepresentasikan penyanyi/musisi
 * Mengelola daftar lagu dan album (Composition/Aggregation)
 */
public class Artis extends Akun {
    private String namaArtis;
    private String genre;
    private final List<Lagu> daftarLagu;
    private final List<Album> daftarAlbum;

    public Artis(String idAkun, String username, String password, String namaArtis, String genre) {
        super(idAkun, username, password);
        this.namaArtis = namaArtis;
        this.genre = genre;
        this.daftarLagu = new ArrayList<>();
        this.daftarAlbum = new ArrayList<>();
    }

    public String getNamaArtis() {
        return namaArtis;
    }

    public void setNamaArtis(String namaArtis) {
        this.namaArtis = namaArtis;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public List<Album> getDaftarAlbum() {
        // Return copy untuk encapsulation
        return new ArrayList<>(daftarAlbum);
    }

    /**
     * Tambah lagu ke daftar artis - establishes bidirectional association
     * @param lagu lagu yang ditambahkan
     */
    public void tambahLagu(Lagu lagu) {
        if (lagu != null && !daftarLagu.contains(lagu)) {
            daftarLagu.add(lagu);
            // Set reference balik ke artis ini
            if (lagu.getArtis() != this) {
                lagu.setArtis(this);
            }
        }
    }

    /** Associates an album with this artist, without allowing duplicates. */
    public void buatAlbum(Album album) {
        if (album == null || daftarAlbum.contains(album)) return;
        daftarAlbum.add(album);
        album.setArtis(this);
    }

    /**
     * Get daftar lagu - return copy untuk encapsulation
     * @return copy of daftar lagu
     */
    public List<Lagu> lihatDaftarLagu() {
        return new ArrayList<>(daftarLagu);
    }

    @Override
    public boolean login() {
        System.out.println("Artis " + namaArtis + " (" + username + ") berhasil login.");
        return true;
    }

    @Override
    public void logout() {
        System.out.println("Artis " + namaArtis + " logout.");
    }
}
