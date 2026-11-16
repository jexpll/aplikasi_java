package com.example.project_pbo;

import java.util.Date;
import java.util.List;

public class MainAppTest {
    public static void main(String[] args) {
        System.out.println("=== TEST APLIKASI MUSIK OOP ===");

        // 1. Controller setup (MVC Controller)
        MusikController controller = new MusikController();

        // 2. Inheritance & Polimorfisme: Akun adalah abstract class, Pengguna & Artis meng-override login() & logout()
        Akun akunUser = new Pengguna("U02", "john_doe", "123", "John Doe", "john@mail.com");
        Akun akunArtis = new Artis("A02", "tulus_m", "456", "Tulus", "Pop");

        System.out.println("\n--- TEST POLIMORFISME login() & logout() ---");
        akunUser.login();   // Output Pengguna
        akunArtis.login();  // Output Artis
        akunUser.logout();
        akunArtis.logout();

        // 3. Test Enkapsulasi: Semua property private/protected diakses via getter/setter
        Pengguna user = controller.getPenggunaAktif();
        System.out.println("\n--- TEST ENKAPSULASI ---");
        System.out.println("User aktif: " + user.getNama() + " (" + user.getEmail() + ")");

        // 4. Test Artis: Tambah Lagu & Buat Album
        System.out.println("\n--- TEST ARTIS & ALBUM ---");
        Artis artis = controller.getArtisAktif();
        Lagu lagu1 = new Lagu("L01", "Yellow", "4:29", "Alternative");
        Lagu lagu2 = new Lagu("L02", "Fix You", "4:55", "Alternative");
        artis.tambahLagu(lagu1);
        artis.tambahLagu(lagu2);

        Album album = new Album("AL01", "Parachutes", new Date());
        album.tambahLagu(lagu1);
        album.tambahLagu(lagu2);
        artis.buatAlbum(album);

        System.out.println("Lagu karya " + artis.getNamaArtis() + ":");
        for (Lagu l : artis.lihatDaftarLagu()) {
            System.out.println("- " + l.getInformasiLagu());
        }

        // 5. Test Lagu: putar(), jeda(), hentikan()
        System.out.println("\n--- TEST KONTROL LAGU ---");
        lagu1.putar();
        lagu1.jeda();
        lagu1.hentikan();

        // 6. Test Playlist
        System.out.println("\n--- TEST PLAYLIST ---");
        Playlist pl = user.buatPlaylist("My Favorites");
        pl.tambahLagu(lagu1);
        pl.tambahLagu(lagu2);
        pl.acakLagu();
        pl.putarPlaylist();

        // 7. Test API Gratis (Deezer): Cari Lagu Online
        System.out.println("\n--- TEST INTEGRASI API DEEZER (ONLINE SEARCH) ---");
        System.out.println("Mencari lagu 'Coldplay' via Deezer API...");
        List<Lagu> hasilOnline = user.cariLagu("Coldplay");
        System.out.println("Ditemukan: " + hasilOnline.size() + " lagu.");
        for (int i = 0; i < Math.min(3, hasilOnline.size()); i++) {
            Lagu l = hasilOnline.get(i);
            System.out.println((i + 1) + ". " + l.getInformasiLagu() + " | Preview: " + l.getPreviewUrl());
            user.simpanLagu(l);
        }

        // Self-check assertions
        assert akunUser instanceof Akun : "akunUser must be Akun";
        assert !artis.lihatDaftarLagu().isEmpty() : "Artis songs should not be empty";
        assert !user.getDaftarPlaylist().isEmpty() : "Playlist should exist";
        System.out.println("\nSEMUA TEST OOP & API BERHASIL!");
    }
}
