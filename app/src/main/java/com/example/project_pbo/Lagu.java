package com.example.project_pbo;

/**
 * Kelas Lagu merepresentasikan satu track musik
 * Encapsulation: field private, akses via getter/setter
 * Association: memiliki referensi ke Artis object (bukan hanya String nama)
 */
public class Lagu {
    private final String idLagu;
    private String judul;
    private String durasi;
    private String genre;
    private String previewUrl;
    private String coverUrl;
    private String namaArtisApi;
    private Artis artis;  // Reference ke object Artis (Association)

    public Lagu(String idLagu, String judul, String durasi, String genre) {
        this.idLagu = idLagu;
        this.judul = judul;
        this.durasi = durasi;
        this.genre = genre;
    }

    public Lagu(String idLagu, String judul, String durasi, String genre, String previewUrl, String namaArtis, String coverUrl) {
        this(idLagu, judul, durasi, genre);
        this.previewUrl = previewUrl;
        this.coverUrl = coverUrl;
        this.namaArtisApi = namaArtis;
    }

    public Lagu(String idLagu, String judul, String durasi, String genre, String previewUrl, String namaArtis) {
        this(idLagu, judul, durasi, genre, previewUrl, namaArtis, null);
    }

    public String getIdLagu() {
        return idLagu;
    }

    // idLagu is final - no setter (immutable identifier)

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDurasi() {
        return durasi;
    }

    public void setDurasi(String durasi) {
        this.durasi = durasi;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    /**
     * Get nama artis - prioritaskan object Artis, fallback ke namaArtis lama
     * @return nama artis atau "Unknown Artist"
     */
    public String getNamaArtis() {
        if (artis != null) {
            return artis.getNamaArtis();
        }
        return namaArtisApi == null || namaArtisApi.isEmpty() ? "Unknown Artist" : namaArtisApi;
    }

    /**
     * Set Artis object - establishes bidirectional association
     * @param artis object Artis (tidak null)
     */
    public void setArtis(Artis artis) {
        this.artis = artis;
        if (artis != null && !artis.lihatDaftarLagu().contains(this)) {
            artis.tambahLagu(this);  // Maintain bidirectional relationship
        }
    }

    /**
     * Get Artis object
     * @return Artis atau null jika belum di-set
     */
    public Artis getArtis() {
        return artis;
    }

    public void putar() {
        System.out.println("Memutar lagu: " + judul + (artis != null ? " - " + artis.getNamaArtis() : ""));
    }

    public void jeda() {
        System.out.println("Menjeda lagu: " + judul);
    }

    public void hentikan() {
        System.out.println("Menghentikan lagu: " + judul);
    }

    public String getInformasiLagu() {
        return "ID: " + idLagu + " | Judul: " + judul + " | Durasi: " + durasi + " | Genre: " + genre
                + (artis != null ? " | Artis: " + artis.getNamaArtis() : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lagu lagu = (Lagu) o;
        return idLagu != null && idLagu.equals(lagu.idLagu);
    }

    @Override
    public int hashCode() {
        return idLagu != null ? idLagu.hashCode() : 0;
    }

    @Override
    public String toString() {
        return judul + (artis != null ? " - " + artis.getNamaArtis() : "") + " (" + durasi + ")";
    }
}

