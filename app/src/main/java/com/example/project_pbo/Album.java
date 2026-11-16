package com.example.project_pbo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Kelas Album merepresentasikan album musik
 * Implementasi SongCollection untuk polymorphism
 */
public class Album implements SongCollection {
    private final String idAlbum;
    private String namaAlbum;
    private Date tanggalRilis;
    private Artis artis;
    private final List<Lagu> daftarLagu;

    public Album(String idAlbum, String namaAlbum, Date tanggalRilis) {
        this.idAlbum = idAlbum;
        this.namaAlbum = namaAlbum;
        this.tanggalRilis = tanggalRilis;
        this.daftarLagu = new ArrayList<>();
    }

    @Override
    public String getId() {
        return idAlbum;
    }

    @Override
    public String getName() {
        return namaAlbum;
    }

    public void setNamaAlbum(String namaAlbum) {
        this.namaAlbum = namaAlbum;
    }

    public Date getTanggalRilis() {
        return tanggalRilis;
    }

    public void setTanggalRilis(Date tanggalRilis) {
        this.tanggalRilis = tanggalRilis;
    }

    public Artis getArtis() {
        return artis;
    }

    /** Maintains the Album -> Artis association from the class diagram. */
    public void setArtis(Artis artis) {
        this.artis = artis;
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
    public String getIdAlbum() {
        return idAlbum;
    }

    public String getNamaAlbum() {
        return namaAlbum;
    }
}
