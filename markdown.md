# Dokumentasi OOP Project Aplikasi Musik

## 1. Cara Memahami Project

Project ini adalah aplikasi Android Java untuk registrasi, login, pencarian lagu melalui Deezer API, penyimpanan lagu, playlist, dan pemutaran preview audio.

Urutan membaca yang disarankan:

1. `settings.gradle.kts` dan `app/build.gradle.kts` untuk mengetahui konfigurasi.
2. Class model: `Akun`, `Pengguna`, `Artis`, `Lagu`, `Album`, dan `Playlist`.
3. Interface: `SongCollection` dan `MusicApiService`.
4. Service: `DeezerApiService`.
5. Controller: `MusikController`.
6. Utility: `AudioPlayerManager` dan `ImageLoader`.
7. Adapter dan halaman Android.
8. `MainAppTest.java` sebagai contoh penggunaan.

Jangan mulai dari `homePage.java` karena file tersebut paling panjang.

## 2. Struktur File Utama

```text
app/src/main/java/com/example/project_pbo
├── Model: Akun, Pengguna, Artis, Lagu, Album, Playlist
├── Interface: SongCollection, MusicApiService
├── Service: DeezerApiService
├── Controller: MusikController
├── Utility: AudioPlayerManager, ImageLoader
├── Adapter: SongAdapter, PlaylistAdapter
└── UI: MainActivity, loginPage, registerPage, homePage
```

## 3. Konfigurasi Project

`D:\tugas_pbo\settings.gradle.kts` mendaftarkan module:

```kotlin
rootProject.name = "project_pbo"
include(":app")
```

`D:\tugas_pbo\app\build.gradle.kts` menunjukkan aplikasi Android dan penggunaan Java 11:

```kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
```

Konfigurasi ini bukan konsep OOP langsung, tetapi menjelaskan lingkungan kode berjalan.

---

# 4. `Akun.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\Akun.java`

```java
public abstract class Akun {
    protected String idAkun;
    protected String username;
    protected String password;

    public abstract boolean login();
    public abstract void logout();
}
```

`Akun` adalah abstract class. Ia menjadi dasar untuk `Pengguna` dan `Artis`, sehingga tidak dibuat sebagai object langsung.

Constructor mengisi data akun. Getter dan setter membatasi akses terhadap field. Ini adalah enkapsulasi.

Method `login()` dan `logout()` adalah kontrak untuk semua subclass. Detailnya dibuat oleh subclass. Ini adalah abstraction dan polymorphism.

---

# 5. `Pengguna.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\Pengguna.java`

```java
public class Pengguna extends Akun {
    private String nama;
    private String email;
    private final List<Lagu> laguTersimpan;
    private final List<Playlist> daftarPlaylist;
    private final MusicApiService apiService;
}
```

`Pengguna extends Akun` berarti pengguna mewarisi identitas akun, username, password, getter, setter, serta kontrak login/logout.

Constructor memanggil:

```java
super(idAkun, username, password);
```

Kemudian list lagu dan playlist dibuat kosong. `MusicApiService` diberikan melalui constructor. Ini disebut dependency injection karena dependency diberikan dari luar class.

Getter list mengembalikan salinan:

```java
public List<Lagu> getLaguTersimpan() {
    return new ArrayList<>(laguTersimpan);
}
```

Tujuannya menjaga enkapsulasi agar list internal tidak diubah langsung.

Method penting:

- `simpanLagu(Lagu lagu)` menyimpan lagu yang belum ada.
- `putarLagu(Lagu lagu)` meminta lagu diputar.
- `buatPlaylist(String nama)` membuat dan menyimpan object `Playlist`.
- `cariLagu(String judul)` memakai `MusicApiService` lalu melakukan fallback ke lagu tersimpan.

`Pengguna` meng-override `login()` dan `logout()` dengan perilaku khusus pengguna.

---

# 6. `Artis.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\Artis.java`

```java
public class Artis extends Akun {
    private String namaArtis;
    private String genre;
    private final List<Lagu> daftarLagu;
    private final List<Album> daftarAlbum;
}
```

`Artis` adalah subclass `Akun`. Ia menambahkan nama artis, genre, lagu, dan album.

Saat `tambahLagu(lagu)` dipanggil, lagu dimasukkan ke daftar artis dan referensi balik ke artis diatur. Ini adalah bidirectional association.

Saat `buatAlbum(album)` dipanggil, album disimpan di daftar artis dan album diberi referensi artis.

`Artis` juga meng-override `login()` dan `logout()`. Method yang sama menghasilkan perilaku berbeda dibanding `Pengguna`, sehingga menjadi contoh polymorphism.

---

# 7. `Lagu.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\Lagu.java`

Data lagu meliputi ID, judul, durasi, genre, URL preview, URL cover, dan object `Artis`.

```java
private final String idLagu;
private String judul;
private Artis artis;
```

`idLagu` final berarti identitas lagu tidak diubah setelah constructor selesai. Field private merupakan enkapsulasi.

Class memiliki beberapa constructor dengan parameter berbeda. Ini disebut constructor overloading. Constructor lengkap menggunakan `this(...)` untuk memakai constructor dasar.

Method `getNamaArtis()` mengambil nama dari object `Artis`, atau memakai nama dari API jika object belum dihubungkan.

Method behavior-nya adalah:

```java
putar();
jeda();
hentikan();
```

`equals()` dan `hashCode()` memakai `idLagu`, sehingga lagu dengan ID sama dianggap object yang sama. `toString()` menghasilkan bentuk teks lagu.



---

# 16. `SongAdapter.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\SongAdapter.java`

```java
public class SongAdapter extends ArrayAdapter<Lagu>
```

Adapter menjembatani `List<Lagu>` dengan `ListView`. Method `getView` mengambil object `Lagu`, lalu memasukkan judul, artis, durasi, dan cover ke layout Android.

Adapter memiliki callback:

```java
public interface OnMoreClickListener {
    void onMoreClick(Lagu song);
}
```

Saat tombol more ditekan, adapter memberi tahu class pemanggil. Adapter tidak perlu mengetahui apakah lagu akan disimpan atau dimasukkan ke playlist. Ini mengurangi coupling.

# 17. `PlaylistAdapter.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\PlaylistAdapter.java`

```java
public class PlaylistAdapter extends ArrayAdapter<Playlist>
```

Adapter ini mengubah object `Playlist` menjadi item list. Ia menampilkan nama playlist dan jumlah lagu. Inheritance dari `ArrayAdapter` serta fungsi adapter merupakan konsep OOP dan Adapter Pattern.

# 18. `NonScrollListView.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\NonScrollListView.java`

```java
public class NonScrollListView extends ListView
```

Class ini mewarisi `ListView`, lalu meng-override `onMeasure` untuk mengatur tinggi list. Pemanggilan `super.onMeasure(...)` menjalankan implementasi parent setelah ukuran baru ditentukan.

---

# 19. Halaman Android

## `MainActivity.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\MainActivity.java`

```java
public class MainActivity extends AppCompatActivity
```

`MainActivity` adalah halaman awal. `onCreate()` memasang layout. Setelah delay tiga detik, `Intent` memindahkan aplikasi ke `registerPage`.

## `registerPage.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\registerPage.java`

Saat tombol register diklik, halaman membuat object `Pengguna` dan memberikan `DeezerApiService` melalui constructor. Ini adalah object creation dan dependency injection.

Catatan: object tersebut belum disimpan ke database maupun `MusikController`; saat ini halaman membuat object dan menampilkan pesan sukses.

## `loginPage.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\loginPage.java`

```java
Akun akun = new Pengguna(...);
if (akun.login()) {
    // pindah ke homePage
}
```

Tipe referensi adalah `Akun`, tetapi object nyatanya `Pengguna`. Saat `login()` dipanggil, implementasi `Pengguna` dijalankan. Ini runtime polymorphism.

## `homePage.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\homePage.java`

```java
public class homePage extends AppCompatActivity
        implements AudioPlayerManager.OnPlaybackListener
```

`homePage` mewarisi kemampuan Android dan menjadi listener playback. Class ini mengelola home feed, pencarian, playlist, drawer, serta mini player.

Data ditampilkan melalui:

```text
List<Lagu>       → SongAdapter
List<Playlist>   → PlaylistAdapter
```

Callback `onPlay`, `onPause`, `onStop`, dan `onError` mengubah icon mini player atau menampilkan Toast. Saat playlist dibuka, UI memanggil `playlist.putarPlaylist()` dan `playlist.acakLagu()`.


---

# 8. `SongCollection.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\SongCollection.java`

```java
public interface SongCollection {
    String getId();
    String getName();
    List<Lagu> getDaftarLagu();
    void tambahLagu(Lagu lagu);
    void hapusLagu(Lagu lagu);
    int getJumlahLagu();
    boolean isEmpty();
}
```

Interface ini adalah kontrak untuk object yang mengelola kumpulan lagu. `Album` dan `Playlist` mengimplementasikannya. Detail penyimpanan lagu diserahkan kepada masing-masing class.

Konsep yang digunakan adalah abstraction dan polymorphism. Keduanya dapat digunakan melalui tipe `SongCollection` walaupun implementasinya berbeda.

# 9. `Album.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\Album.java`

```java
public class Album implements SongCollection
```

Album memiliki ID, nama, tanggal rilis, artis, dan `List<Lagu>`. Method `tambahLagu`, `hapusLagu`, `getJumlahLagu`, dan `isEmpty` memenuhi kontrak interface.

Getter daftar lagu mengembalikan salinan list. Ini menjaga enkapsulasi. Album memiliki hubungan aggregation terhadap lagu karena lagu masih dapat hidup di luar album.

# 10. `Playlist.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\Playlist.java`

Playlist juga mengimplementasikan `SongCollection`. Selain operasi umum, playlist memiliki behavior khusus:

```java
public void putarPlaylist() {
    for (Lagu lagu : daftarLagu) lagu.putar();
}

public void acakLagu() {
    Collections.shuffle(daftarLagu);
}
```

Playlist mengelola banyak object `Lagu`. Hubungan ini juga aggregation karena lagu tidak bergantung pada umur playlist.

Struktur model:

```text
Akun (abstract)
├── Pengguna
└── Artis ── memiliki Lagu dan Album

SongCollection (interface)
├── Album
└── Playlist
```

---

# 11. `MusicApiService.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\MusicApiService.java`

```java
public interface MusicApiService {
    List<Lagu> cariLaguOnline(String query);
}
```

Interface ini mendefinisikan kontrak pencarian lagu. `Pengguna` dan `MusikController` bergantung pada interface ini, bukan langsung pada Deezer.

```text
Pengguna / MusikController
            ↓
    MusicApiService
            ↑
    DeezerApiService
```

Ini adalah Dependency Inversion dan membuat service mudah diganti atau dibuat versi mock untuk testing.

# 12. `DeezerApiService.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\DeezerApiService.java`

```java
public class DeezerApiService implements MusicApiService
```

Class ini menjadi adapter untuk Deezer API. Method `cariLaguOnline` melakukan:

1. Validasi query.
2. URL encoding.
3. Request HTTP GET.
4. Membaca JSON.
5. Mengambil array `data`.
6. Mengubah setiap track menjadi object `Lagu`.
7. Mengembalikan list lagu.

Contoh mapping:

```java
hasil.add(new Lagu(
    String.valueOf(track.optLong("id")),
    track.optString("title", "Tanpa judul"),
    durasi,
    "Pop/Rock",
    track.optString("preview", ""),
    namaArtis,
    coverUrl
));
```

Data dari API diubah menjadi object domain `Lagu`, sehingga UI tidak perlu mengetahui format JSON. Jika terjadi error, method mengembalikan hasil kosong dan koneksi ditutup pada `finally`.

---

# 13. `MusikController.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\MusikController.java`

Controller mengatur logika bisnis dan koordinasi model:

```java
private Pengguna penggunaAktif;
private Artis artisAktif;
private final List<Pengguna> listPengguna = new ArrayList<>();
private final List<Artis> listArtis = new ArrayList<>();
private final MusicApiService apiService;
```

Constructor menerima service melalui dependency injection. `initializeDefaultData()` membuat data awal pengguna dan artis.

`registerPengguna()` memeriksa username duplikat, membuat object `Pengguna`, menyimpannya, lalu menjadikannya pengguna aktif.

`loginPengguna()` mencari username dan password. Setelah cocok, controller memanggil:

```java
return p.login();
```

Pemanggilan ini menggunakan implementasi `login()` milik `Pengguna`.

Method seperti `cariLagu`, `simpanLagu`, `putarLagu`, dan `buatPlaylist` mendelegasikan pekerjaan kepada `penggunaAktif`. Ini menjaga pembagian tanggung jawab.

---

# 14. `AudioPlayerManager.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\AudioPlayerManager.java`

## Singleton

```java
private static AudioPlayerManager instance;
private AudioPlayerManager() {}

public static synchronized AudioPlayerManager getInstance() {
    if (instance == null) instance = new AudioPlayerManager();
    return instance;
}
```

Constructor private mencegah object dibuat dari luar. Semua halaman menggunakan satu manager audio. Ini adalah Singleton Pattern.

## Listener

```java
public interface OnPlaybackListener {
    void onPlay(Lagu song);
    void onPause();
    void onStop();
    void onError(String msg);
}
```

UI dapat menjadi listener. Manager memberi tahu UI saat status audio berubah tanpa bergantung langsung pada detail UI. Ini menyerupai Observer Pattern.

`playSong` mengatur `MediaPlayer`, menggunakan preview URL, dan memanggil `prepareAsync`. Saat siap, audio diputar dan `listener.onPlay(song)` dipanggil. `togglePlayPause` menangani pause dan resume.

# 15. `ImageLoader.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\ImageLoader.java`

`ImageLoader` mengunduh cover lagu secara asynchronous. Ia memakai `ConcurrentHashMap` sebagai cache dan `ExecutorService` sebagai background worker.

Jika gambar ada di cache, gambar langsung digunakan. Jika belum, gambar diunduh lalu dikirim ke UI thread dengan `imageView.post(...)`. Konsep yang tampak adalah enkapsulasi, cache, dan pemrosesan asynchronous.



---

# 20. `MainAppTest.java`

File: `D:\tugas_pbo\app\src\main\java\com\example\project_pbo\MainAppTest.java`

File ini mendemonstrasikan konsep OOP:

```java
Akun akunUser = new Pengguna(...);
Akun akunArtis = new Artis(...);
akunUser.login();
akunArtis.login();
```

Satu tipe referensi `Akun` digunakan untuk dua object berbeda. Ini polymorphism. Pembuatan album, penambahan lagu ke artis, dan pembuatan playlist menguji association serta aggregation. Assertion seperti `akunUser instanceof Akun` memeriksa kondisi object.

# 21. Alur Program

## Aplikasi dimulai

1. Android menjalankan `MainActivity`.
2. Layout awal ditampilkan.
3. Setelah delay, aplikasi berpindah ke `registerPage`.

## Registrasi dan login

1. Form registrasi membuat object `Pengguna`.
2. Halaman login membuat object `Pengguna` melalui referensi `Akun`.
3. `login()` dipanggil secara polymorphic.
4. Aplikasi berpindah ke `homePage`.

## Pencarian lagu

```text
UI → Pengguna.cariLagu()
   → MusicApiService
   → DeezerApiService
   → JSON Deezer
   → object Lagu
   → SongAdapter
   → ListView
```

Response JSON diubah menjadi object `Lagu`, lalu ditampilkan adapter.

## Menyimpan lagu dan playlist

1. Tombol more pada `SongAdapter` menjalankan callback.
2. UI memanggil `pengguna.simpanLagu(lagu)`.
3. `Pengguna.buatPlaylist()` membuat object `Playlist`.
4. Lagu ditambahkan melalui `playlist.tambahLagu(lagu)`.

## Memutar lagu

1. UI memperoleh `AudioPlayerManager.getInstance()`.
2. `playSong(lagu)` mengatur `MediaPlayer`.
3. Preview URL digunakan sebagai sumber audio.
4. Manager memanggil callback `homePage.onPlay()`.
5. UI mengganti ikon play menjadi pause.

# 22. Ringkasan Konsep OOP

| Konsep | Penerapan |
|---|---|
| Class dan object | `Lagu`, `Pengguna`, `Playlist`, `Album` |
| Encapsulation | Field private, getter/setter, copy pada list |
| Inheritance | `Pengguna extends Akun`, `Artis extends Akun` |
| Abstraction | `abstract class Akun`, interface service dan collection |
| Polymorphism | `Akun akun = new Pengguna(...)`, implementasi interface |
| Overriding | `login`, `logout`, `getJumlahLagu`, `onMeasure` |
| Overloading | Constructor `Lagu` dan `Pengguna` |
| Association | `Lagu` memiliki referensi `Artis` |
| Aggregation | `Playlist` dan `Album` memiliki `List<Lagu>` |
| Dependency Injection | `MusicApiService` diberikan lewat constructor |
| Dependency Inversion | Bergantung pada interface, bukan langsung Deezer |
| Singleton | `AudioPlayerManager.getInstance()` |
| Observer/Listener | `OnPlaybackListener` |
| Adapter Pattern | `SongAdapter`, `PlaylistAdapter` |
| Controller/MVC sederhana | `MusikController` |

# 23. Kesimpulan

Project ini memodelkan aplikasi musik sebagai object-object yang saling bekerja sama. `Akun` menjadi abstraksi dasar; `Pengguna` dan `Artis` menjadi turunannya; `Lagu`, `Album`, dan `Playlist` menjadi model domain; service memisahkan API dari model; controller mengatur logika bisnis; dan UI menggunakan adapter untuk menampilkan object.

Cara berpikir ketika membaca project:

```text
Data dunia nyata
    ↓
Class model
    ↓
Object dan hubungan antarobject
    ↓
Service dan controller
    ↓
UI Android
```

Dengan urutan tersebut, kode panjang dapat dipahami sebagai kumpulan tanggung jawab kecil yang saling berhubungan.
