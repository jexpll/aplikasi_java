package com.example.project_pbo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class homePage extends AppCompatActivity implements AudioPlayerManager.OnPlaybackListener {
    private static final int TAB_HOME = 0;
    private static final int TAB_SEARCH = 1;
    private static final int TAB_LIBRARY = 2;
    private static final int ACTIVE_COLOR = 0xFFFFFFFF;
    private static final int INACTIVE_COLOR = 0xFFB3B3B3;
    private static final String DEFAULT_FEED_QUERY = "indonesia top hits";

    private DrawerLayout drawerLayout;
    private ScrollView pageHome;
    private View pageSearch, pageLibrary;

    private ImageView ivTabHome, ivTabSearch, ivTabLibrary;
    private TextView tvTabHome, tvTabSearch, tvTabLibrary;

    private NonScrollListView lvHomeSongs;
    private ListView lvSearchResults, lvPlaylists;

    private EditText etSearchInput;
    private ImageView btnClearSearch;
    private TextView tvSearchResultStatus;

    private RelativeLayout layoutMiniPlayer;
    private ImageView ivMiniCover, btnMiniPlayPause, btnMiniHeart;
    private TextView tvMiniTitle, tvMiniArtist;

    private TextView tvQuickPlaylistName;
    private TextView tvDrawerUsername, tvDrawerEmail;

    private Pengguna penggunaAktif;
    private final List<Lagu> homeSongs = new ArrayList<>();
    private final List<Lagu> searchSongs = new ArrayList<>();
    private final List<Playlist> userPlaylists = new ArrayList<>();

    private SongAdapter homeAdapter;
    private SongAdapter searchAdapter;
    private PlaylistAdapter playlistAdapter;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final AudioPlayerManager playerManager = AudioPlayerManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Safe insets padding applied directly to mainContainer and sideMenuDrawer
        View mainContainer = findViewById(R.id.mainContainer);
        View sideDrawer = findViewById(R.id.sideMenuDrawer);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
            androidx.core.graphics.Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
            );

            mainContainer.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            sideDrawer.setPadding(bars.left + 20, bars.top + 16, bars.right + 20, bars.bottom + 20);

            return insets;
        });

        String username = getIntent().getStringExtra("USERNAME");
        String email = getIntent().getStringExtra("EMAIL");
        if (username == null) username = "User";
        if (email == null) email = username + "@spotify.com";

        // Dependency Injection: gunakan DeezerApiService untuk Pengguna
        penggunaAktif = new Pengguna("U-" + System.currentTimeMillis(), username, "secret", username, email, new DeezerApiService());
        penggunaAktif.buatPlaylist("Playlist Saya #1");

        initViews();
        setupNavigationTabs();
        setupSearchLogic();
        setupPlaylistsLogic();
        setupSideMenu();

        playerManager.setListener(this);

        loadHomeFeed(DEFAULT_FEED_QUERY);
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        pageHome = findViewById(R.id.pageHome);
        pageSearch = findViewById(R.id.pageSearch);
        pageLibrary = findViewById(R.id.pageLibrary);

        ivTabHome = findViewById(R.id.ivTabHome);
        ivTabSearch = findViewById(R.id.ivTabSearch);
        ivTabLibrary = findViewById(R.id.ivTabLibrary);
        tvTabHome = findViewById(R.id.tvTabHome);
        tvTabSearch = findViewById(R.id.tvTabSearch);
        tvTabLibrary = findViewById(R.id.tvTabLibrary);

        lvHomeSongs = findViewById(R.id.lvHomeSongs);
        lvSearchResults = findViewById(R.id.lvSearchResults);
        lvPlaylists = findViewById(R.id.lvPlaylists);

        etSearchInput = findViewById(R.id.etSearchInput);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        tvSearchResultStatus = findViewById(R.id.tvSearchResultStatus);

        layoutMiniPlayer = findViewById(R.id.layoutMiniPlayer);
        ivMiniCover = findViewById(R.id.ivMiniCover);
        btnMiniPlayPause = findViewById(R.id.btnMiniPlayPause);
        btnMiniHeart = findViewById(R.id.btnMiniHeart);
        tvMiniTitle = findViewById(R.id.tvMiniTitle);
        tvMiniArtist = findViewById(R.id.tvMiniArtist);

        tvQuickPlaylistName = findViewById(R.id.tvQuickPlaylistName);
        tvDrawerUsername = findViewById(R.id.tvDrawerUsername);
        tvDrawerEmail = findViewById(R.id.tvDrawerEmail);

        tvDrawerUsername.setText(penggunaAktif.getNama());
        tvDrawerEmail.setText(penggunaAktif.getEmail());

        homeAdapter = new SongAdapter(this, homeSongs, this::showSongOptionsDialog);
        lvHomeSongs.setAdapter(homeAdapter);

        searchAdapter = new SongAdapter(this, searchSongs, this::showSongOptionsDialog);
        lvSearchResults.setAdapter(searchAdapter);

        userPlaylists.clear();
        userPlaylists.addAll(penggunaAktif.getDaftarPlaylist());
        playlistAdapter = new PlaylistAdapter(this, userPlaylists);
        lvPlaylists.setAdapter(playlistAdapter);

        if (!userPlaylists.isEmpty()) {
            tvQuickPlaylistName.setText(userPlaylists.get(0).getNamaPlaylist());
        }

        findViewById(R.id.ivProfileAvatar).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        btnMiniPlayPause.setOnClickListener(v -> playerManager.togglePlayPause());
        btnMiniHeart.setOnClickListener(v -> {
            Lagu cur = playerManager.getCurrentSong();
            if (cur != null) {
                penggunaAktif.simpanLagu(cur);
                Toast.makeText(this, "Disimpan ke Lagu yang Disukai", Toast.LENGTH_SHORT).show();
            }
        });

        lvHomeSongs.setOnItemClickListener((p, v, pos, id) -> putarLaguSekarang(homeSongs.get(pos)));
        lvSearchResults.setOnItemClickListener((p, v, pos, id) -> putarLaguSekarang(searchSongs.get(pos)));
    }

    private void setupNavigationTabs() {
        findViewById(R.id.tabHome).setOnClickListener(v -> switchTab(TAB_HOME));
        findViewById(R.id.tabSearch).setOnClickListener(v -> switchTab(TAB_SEARCH));
        findViewById(R.id.tabLibrary).setOnClickListener(v -> switchTab(TAB_LIBRARY));
    }

    private void switchTab(int tab) {
        pageHome.setVisibility(tab == TAB_HOME ? View.VISIBLE : View.GONE);
        pageSearch.setVisibility(tab == TAB_SEARCH ? View.VISIBLE : View.GONE);
        pageLibrary.setVisibility(tab == TAB_LIBRARY ? View.VISIBLE : View.GONE);
        setTabColor(ivTabHome, tvTabHome, tab == TAB_HOME);
        setTabColor(ivTabSearch, tvTabSearch, tab == TAB_SEARCH);
        setTabColor(ivTabLibrary, tvTabLibrary, tab == TAB_LIBRARY);

        if (tab == TAB_SEARCH) focusSearchInput();
        if (tab == TAB_LIBRARY) refreshPlaylists();
    }

    private void setTabColor(ImageView icon, TextView label, boolean active) {
        int color = active ? ACTIVE_COLOR : INACTIVE_COLOR;
        icon.setColorFilter(color);
        label.setTextColor(color);
    }

    private void focusSearchInput() {
        etSearchInput.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(etSearchInput, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void setupSearchLogic() {
        etSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearchInput.getText().toString().trim());
                hideKeyboard();
                return true;
            }
            return false;
        });

        etSearchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> etSearchInput.setText(""));
    }

    private void performSearch(String query) {
        if (query.isEmpty()) return;
        tvSearchResultStatus.setText("Mencari '" + query + "'...");
        loadSongs(query, searchSongs, searchAdapter,
                hasil -> tvSearchResultStatus.setText(hasil.isEmpty()
                        ? "Tidak ditemukan lagu"
                        : "Hasil untuk '" + query + "' (" + hasil.size() + ")"));
    }

    private void loadSongs(String query, List<Lagu> target, SongAdapter adapter,
                           Consumer<List<Lagu>> onLoaded) {
        new Thread(() -> {
            List<Lagu> hasil = penggunaAktif.cariLagu(query);
            mainHandler.post(() -> {
                target.clear();
                target.addAll(hasil);
                adapter.notifyDataSetChanged();
                onLoaded.accept(hasil);
            });
        }).start();
    }

    private void setupPlaylistsLogic() {
        findViewById(R.id.cardLikedSongs).setOnClickListener(v -> showLikedSongsDialog());
        findViewById(R.id.cardTopHits).setOnClickListener(v -> {
            if (!userPlaylists.isEmpty()) showPlaylistDetailDialog(userPlaylists.get(0));
        });

        findViewById(R.id.btnCreatePlaylistLib).setOnClickListener(v -> showCreatePlaylistDialog());

        lvPlaylists.setOnItemClickListener((p, v, pos, id) -> {
            Playlist selected = userPlaylists.get(pos);
            showPlaylistDetailDialog(selected);
        });
    }

    private void refreshPlaylists() {
        userPlaylists.clear();
        userPlaylists.addAll(penggunaAktif.getDaftarPlaylist());
        playlistAdapter.notifyDataSetChanged();
    }

    private void setupSideMenu() {
        findViewById(R.id.menuBaruDiputar).setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            switchTab(0);
        });

        findViewById(R.id.menuSettings).setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            new AlertDialog.Builder(this)
                    .setTitle("Pengaturan dan Privasi")
                    .setMessage("Username: " + penggunaAktif.getUsername() + "\nEmail: " + penggunaAktif.getEmail() + "\nStatus Akun: Aktif\nLayanan: Deezer API Gratis")
                    .setPositiveButton("Tutup", null)
                    .show();
        });

        findViewById(R.id.menuLogout).setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            penggunaAktif.logout();
            Toast.makeText(this, "Berhasil keluar", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(homePage.this, loginPage.class));
            finish();
        });
    }

    private void putarLaguSekarang(Lagu lagu) {
        penggunaAktif.putarLagu(lagu);
        playerManager.playSong(lagu);
        layoutMiniPlayer.setVisibility(View.VISIBLE);
        tvMiniTitle.setText(lagu.getJudul());
        tvMiniArtist.setText(lagu.getNamaArtis() != null ? lagu.getNamaArtis() : "Unknown");
        btnMiniPlayPause.setImageResource(R.drawable.ic_pause);

        if (lagu.getCoverUrl() != null && !lagu.getCoverUrl().isEmpty()) {
            ImageLoader.load(lagu.getCoverUrl(), ivMiniCover);
        } else {
            ivMiniCover.setImageResource(R.drawable.ic_nav_library);
        }
    }

    private void showCreatePlaylistDialog() {
        EditText input = new EditText(this);
        input.setHint("Beri nama playlist kamu");
        input.setTextColor(0xFFFFFFFF);
        input.setHintTextColor(0xFF888888);

        new AlertDialog.Builder(this)
                .setTitle("Buat Playlist Baru")
                .setView(input)
                .setPositiveButton("Buat", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        Playlist p = penggunaAktif.buatPlaylist(name);
                        refreshPlaylists();
                        Toast.makeText(this, "Playlist '" + p.getNamaPlaylist() + "' dibuat!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showSongOptionsDialog(Lagu lagu) {
        String[] options = {"Putar Sekarang", "Tambah ke Playlist", "Simpan ke Lagu yang Disukai", "Info Lagu"};
        new AlertDialog.Builder(this)
                .setTitle(lagu.getJudul())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        putarLaguSekarang(lagu);
                    } else if (which == 1) {
                        showSelectPlaylistDialog(lagu);
                    } else if (which == 2) {
                        penggunaAktif.simpanLagu(lagu);
                        Toast.makeText(this, "Disimpan ke Lagu yang Disukai (" + penggunaAktif.getLaguTersimpan().size() + ")", Toast.LENGTH_SHORT).show();
                    } else if (which == 3) {
                        new AlertDialog.Builder(this)
                                .setTitle("Info Lagu")
                                .setMessage(lagu.getInformasiLagu())
                                .setPositiveButton("OK", null)
                                .show();
                    }
                })
                .show();
    }

    private void showSelectPlaylistDialog(Lagu lagu) {
        List<Playlist> playlists = penggunaAktif.getDaftarPlaylist();
        if (playlists.isEmpty()) {
            Playlist baru = penggunaAktif.buatPlaylist("Playlist Saya #1");
            baru.tambahLagu(lagu);
            refreshPlaylists();
            Toast.makeText(this, "Ditambahkan ke " + baru.getNamaPlaylist(), Toast.LENGTH_SHORT).show();
            return;
        }

        String[] names = new String[playlists.size() + 1];
        for (int i = 0; i < playlists.size(); i++) {
            names[i] = playlists.get(i).getNamaPlaylist() + " (" + playlists.get(i).getDaftarLagu().size() + " lagu)";
        }
        names[playlists.size()] = "+ Buat Playlist Baru";

        new AlertDialog.Builder(this)
                .setTitle("Pilih Playlist")
                .setItems(names, (dialog, which) -> {
                    if (which == playlists.size()) {
                        showCreateAndAddDialog(lagu);
                    } else {
                        Playlist chosen = playlists.get(which);
                        chosen.tambahLagu(lagu);
                        refreshPlaylists();
                        Toast.makeText(this, "Ditambahkan ke " + chosen.getNamaPlaylist(), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void showCreateAndAddDialog(Lagu lagu) {
        EditText input = new EditText(this);
        input.setHint("Nama playlist");
        input.setTextColor(0xFFFFFFFF);
        input.setHintTextColor(0xFF888888);

        new AlertDialog.Builder(this)
                .setTitle("Playlist Baru")
                .setView(input)
                .setPositiveButton("Buat & Tambah", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        Playlist p = penggunaAktif.buatPlaylist(name);
                        p.tambahLagu(lagu);
                        refreshPlaylists();
                        Toast.makeText(this, "Ditambahkan ke " + p.getNamaPlaylist(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showLikedSongsDialog() {
        List<Lagu> liked = penggunaAktif.getLaguTersimpan();
        if (liked.isEmpty()) {
            Toast.makeText(this, "Belum ada Lagu yang Disukai. Klik titik tiga (⋮) pada lagu untuk menyimpan.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] songTitles = new String[liked.size() + 1];
        songTitles[0] = "▶ Putar Semua Lagu yang Disukai";
        for (int i = 0; i < liked.size(); i++) {
            songTitles[i + 1] = (i + 1) + ". " + liked.get(i).getJudul() + " - " + liked.get(i).getNamaArtis();
        }

        new AlertDialog.Builder(this)
                .setTitle("Lagu yang Disukai (" + liked.size() + ")")
                .setItems(songTitles, (dialog, which) -> {
                    if (which == 0) {
                        putarLaguSekarang(liked.get(0));
                    } else {
                        putarLaguSekarang(liked.get(which - 1));
                    }
                })
                .show();
    }

    private void showPlaylistDetailDialog(Playlist playlist) {
        List<Lagu> songs = playlist.getDaftarLagu();
        if (songs.isEmpty()) {
            Toast.makeText(this, "Playlist " + playlist.getNamaPlaylist() + " masih kosong. Cari lagu dan tambahkan!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] songTitles = new String[songs.size() + 2];
        songTitles[0] = "▶ Putar Playlist";
        songTitles[1] = "🔀 Acak Lagu (Shuffle)";
        for (int i = 0; i < songs.size(); i++) {
            songTitles[i + 2] = (i + 1) + ". " + songs.get(i).getJudul() + " - " + songs.get(i).getNamaArtis();
        }

        new AlertDialog.Builder(this)
                .setTitle(playlist.getNamaPlaylist() + " (" + songs.size() + " lagu)")
                .setItems(songTitles, (dialog, which) -> {
                    if (which == 0) {
                        playlist.putarPlaylist();
                        putarLaguSekarang(songs.get(0));
                    } else if (which == 1) {
                        playlist.acakLagu();
                        putarLaguSekarang(songs.get(0));
                        Toast.makeText(this, "Playlist diacak!", Toast.LENGTH_SHORT).show();
                    } else {
                        putarLaguSekarang(songs.get(which - 2));
                    }
                })
                .show();
    }

    private void loadHomeFeed(String query) {
        loadSongs(query, homeSongs, homeAdapter, hasil -> { /* no status on home */ });
    }

    private void hideKeyboard() {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public void onPlay(Lagu song) {
        mainHandler.post(() -> btnMiniPlayPause.setImageResource(R.drawable.ic_pause));
    }

    @Override
    public void onPause() {
        updateMiniPlayerIcon(R.drawable.ic_play);
    }

    @Override
    public void onStop() {
        updateMiniPlayerIcon(R.drawable.ic_play);
    }

    private void updateMiniPlayerIcon(int drawableId) {
        mainHandler.post(() -> btnMiniPlayPause.setImageResource(drawableId));
    }

    @Override
    public void onError(String msg) {
        mainHandler.post(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }
}