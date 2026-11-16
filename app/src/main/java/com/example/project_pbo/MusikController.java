package com.example.project_pbo;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller untuk mengelola logika bisnis aplikasi musik
 * Mengikuti prinsip Single Responsibility - hanya handle coordination
 * Dependency Injection: menerima MusicApiService dari luar
 */
public class MusikController {
    private Pengguna penggunaAktif;
    private Artis artisAktif;
    private final List<Pengguna> listPengguna = new ArrayList<>();
    private final List<Artis> listArtis = new ArrayList<>();
    private final MusicApiService apiService;

    /**
     * Constructor dengan dependency injection
     * @param apiService implementasi MusicApiService (default: DeezerApiService)
     */
    public MusikController(MusicApiService apiService) {
        this.apiService = apiService != null ? apiService : new DeezerApiService();
        initializeDefaultData();
    }

    /**
     * Constructor convenience dengan default DeezerApiService
     */
    public MusikController() {
        this(null);
    }

    private void initializeDefaultData() {
        // Ponytail: Dummy seed data untuk testing lokal
        Pengguna defaultUser = new Pengguna("U01", "user1", "pass123", "Budi Santoso", "budi@mail.com", apiService);
        Artis defaultArtis = new Artis("A01", "coldplay_off", "pass456", "Coldplay", "Alternative Rock");

        listPengguna.add(defaultUser);
        listArtis.add(defaultArtis);
        this.penggunaAktif = defaultUser;
        this.artisAktif = defaultArtis;
    }

    // Registrasi & Login
    public boolean registerPengguna(String username, String email, String password, String nama) {
        for (Pengguna p : listPengguna) {
            if (p.getUsername().equalsIgnoreCase(username)) return false;
        }
        Pengguna p = new Pengguna("U0" + (listPengguna.size() + 1), username, password, nama, email, apiService);
        listPengguna.add(p);
        this.penggunaAktif = p;
        return true;
    }

    public boolean loginPengguna(String username, String password) {
        for (Pengguna p : listPengguna) {
            if (p.getUsername().equals(username) && p.getPassword().equals(password)) {
                this.penggunaAktif = p;
                return p.login(); // Polimorfisme
            }
        }
        return false;
    }

    public Pengguna getPenggunaAktif() {
        return penggunaAktif;
    }

    public Artis getArtisAktif() {
        return artisAktif;
    }

    public List<Pengguna> getListPengguna() {
        return new ArrayList<>(listPengguna); // Encapsulation
    }

    public List<Artis> getListArtis() {
        return new ArrayList<>(listArtis); // Encapsulation
    }

    // Fitur Pengguna via Controller
    public List<Lagu> cariLagu(String judul) {
        if (penggunaAktif == null) return new ArrayList<>();
        return penggunaAktif.cariLagu(judul);
    }

    public void simpanLagu(Lagu lagu) {
        if (penggunaAktif != null) {
            penggunaAktif.simpanLagu(lagu);
        }
    }

    public void putarLagu(Lagu lagu) {
        if (penggunaAktif != null) {
            penggunaAktif.putarLagu(lagu);
        }
    }

    public Playlist buatPlaylist(String nama) {
        if (penggunaAktif == null) return null;
        return penggunaAktif.buatPlaylist(nama);
    }
}
