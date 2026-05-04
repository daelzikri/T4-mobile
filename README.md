- **Nama:** Pudael Zikri
- **NIM:** F1D02310088

## Deskripsi Singkat
**Student Directory** adalah aplikasi direktori mahasiswa yang memungkinkan pengguna untuk:
1. Melakukan **Login** dengan fitur *Remember Me*.
2. Melakukan operasi **CRUD** (Create, Read, Update, Delete) pada data mahasiswa (Nama, NIM, Prodi, Email, Semester).
3. Mencari data mahasiswa berdasarkan Nama atau NIM.
4. Menambahkan **Catatan Khusus** untuk setiap mahasiswa yang disimpan dalam file teks terpisah.

## Screenshot Aplikasi
*Pastikan Anda telah mengambil screenshot dan menyimpannya di folder `screenshots/` agar gambar di bawah muncul.*

| Login Page | Student List (Main) |
|:---:|:---:|
| ![Login](screenshots/login.png) | ![List](screenshots/main.png) |

| Detail & Notes | Add/Edit Form |
|:---:|:---:|
| ![Detail](screenshots/detail.png) | ![Form](screenshots/form.png) |


## Metode Penyimpanan Data
Aplikasi ini menggunakan tiga lapis penyimpanan data sesuai dengan kebutuhan:

1. **SharedPreferences**:
   - **Kegunaan**: Menyimpan status login dan preferensi *Remember Me*.
   - **Alasan**: Sangat efisien untuk menyimpan data sederhana berupa pasangan key-value (Boolean & String) yang perlu diakses cepat saat aplikasi pertama kali dibuka.

2. **Room Database (SQLite)**:
   - **Kegunaan**: Menyimpan data utama mahasiswa secara terstruktur (Nama, NIM, dll).
   - **Alasan**: Memberikan abstraksi di atas SQLite yang aman secara tipe (*type-safe*), memudahkan kueri data yang kompleks, dan memastikan integritas data terjamin.

3. **Internal Storage (File System)**:
   - **Kegunaan**: Menyimpan catatan khusus per mahasiswa dalam format `.txt`.
   - **Alasan**: Cocok untuk menyimpan data teks yang bersifat opsional atau panjang tanpa membebani ukuran baris dalam tabel database. Data tersimpan secara privat dalam direktori file aplikasi.

---

## Kendala dan Solusi
Selama pengembangan, terdapat beberapa kendala teknis yang berhasil diatasi:

1. **Kendala: Aplikasi Crash setelah Perubahan Schema**
   - **Penyebab**: Menambahkan kolom `email` dan `semester` pada `StudentEntity` tanpa menaikkan versi database Room.
   - **Solusi**: Menaikkan versi database pada `AppDatabase.kt` dan mengaktifkan `.fallbackToDestructiveMigration()` untuk mereset schema secara otomatis.

2. **Kendala: Error Build AndroidX**
   - **Penyebab**: Konfigurasi `android.useAndroidX` belum diaktifkan di properti project.
   - **Solusi**: Menambahkan `android.useAndroidX=true` dan `android.enableJetifier=true` pada file `gradle.properties`.

3. **Kendala: Error "No value passed for parameter email/semester"**
   - **Penyebab**: Fungsi `getSampleData()` masih menggunakan konstruktor lama.
   - **Solusi**: Memperbarui semua instansiasi `StudentEntity` untuk menyertakan data email dan semester yang valid.

---

## Cara Menjalankan Project
1. Clone repository ini.
2. Buka di **Android Studio (Ladybug atau yang lebih baru)**.
3. Tunggu proses **Gradle Sync** selesai.
4. Pastikan `gradle.properties` sudah memiliki `android.useAndroidX=true`.
5. Jalankan di Emulator atau Device (Min SDK 26).
6. **Login Default**: 
   - Username: `admin`
   - Password: `123456`
