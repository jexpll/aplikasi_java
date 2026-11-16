package com.example.project_pbo;

import java.util.ArrayList;
import java.util.List;

/**
 * Kelas Pengguna merepresentasikan user biasa aplikasi musik
 * Mengikuti prinsip Dependency Inversion - bergantung pada abstraksi (MusicApiService)
 * bukan implementasi konkret (DeezerApiService)
 */
public class Pengguna extends Akun {
    private String nama;
    private String email;
    private final List<Lagu> laguTersimpan;
    private final List<Playlist> daftarPlaylist;
    private final MusicApiService apiService;

    /**
     * Constructor dengan dependency injection untuk MusicApiService
     * @param apiService implementasi MusicApiService (default: DeezerApiService)
     */
    public Pengguna(String idAkun, String username, String password, String nama, String email, MusicApiService apiService) {
        super(idAkun, username, password);
        this.nama = nama;
        this.email = email;
        this.laguTersimpan = new ArrayList<>();
        this.daftarPlaylist = new ArrayList<>();
        this.apiService = apiService != null ? apiService : new DeezerApiService();
    }

    /**
     * Constructor convenience dengan default DeezerApiService
     */
    public Pengguna(String idAkun, String username, String password, String nama, String email) {
        this(idAkun, username, password, nama, email, null);
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /** Return a copy so callers cannot modify the user's collections directly. */
    public List<Lagu> getLaguTersimpan() {
        return new ArrayList<>(laguTersimpan);
    }

    public List<Playlist> getDaftarPlaylist() {
        return new ArrayList<>(daftarPlaylist);
    }

    public void putarLagu(Lagu lagu) {
        if (lagu != null) {
            System.out.println(nama + " memutar: " + lagu.getJudul());
            lagu.putar();
        }
    }

    public void simpanLagu(Lagu lagu) {
        if (lagu != null && !laguTersimpan.contains(lagu)) {
            laguTersimpan.add(lagu);
            System.out.println("Lagu '" + lagu.getJudul() + "' disimpan ke favorit " + nama);
        }
    }

    public Playlist buatPlaylist(String namaPlaylist) {
        if (namaPlaylist == null || namaPlaylist.trim().isEmpty()) return null;
        String idPlaylist = "PL-" + (daftarPlaylist.size() + 1);
        Playlist playlist = new Playlist(idPlaylist, namaPlaylist.trim());
        daftarPlaylist.add(playlist);
        return playlist;
    }

    public List<Lagu> cariLagu(String judul) {
        if (judul == null || judul.trim().isEmpty()) return new ArrayList<>();
        // Panggil abstraksi service agar mudah diuji dan diganti.
        List<Lagu> hasil = apiService.cariLaguOnline(judul.trim());

        // Fallback: cari di lagu tersimpan jika offline/kosong
        if (hasil.isEmpty()) {
            for (Lagu l : laguTersimpan) {
                if (l.getJudul().toLowerCase().contains(judul.toLowerCase())) {
                    hasil.add(l);
                }
            }
        }
        return hasil;
    }

    @Override
    public boolean login() {
        System.out.println("Pengguna " + nama + " (" + username + ") berhasil login.");
        return true;
    }

    @Override
    public void logout() {
        System.out.println("Pengguna " + nama + " logout.");
    }
}
