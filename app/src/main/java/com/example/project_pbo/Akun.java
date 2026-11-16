package com.example.project_pbo;

public abstract class Akun {
    protected String idAkun;
    protected String username;
    protected String password;

    public Akun(String idAkun, String username, String password) {
        this.idAkun = idAkun;
        this.username = username;
        this.password = password;
    }

    public String getIdAkun() {
        return idAkun;
    }

    public void setIdAkun(String idAkun) {
        this.idAkun = idAkun;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Ponytail: Abstraksi dasar otentikasi akun, dioverride Pengguna dan Artis (Polimorfisme)
    public abstract boolean login();

    public abstract void logout();
}
