# Library App — Background Thread
> Tugas Praktikum 5 — Background Thread · Pemrograman Mobile

Update aplikasi Library App dengan menerapkan Background Thread pada proses pencarian dan pemuatan data favorit, disertai animasi ProgressBar sebagai indikator loading.

---

## Tentang Tugas

Tugas ini merupakan pengembangan dari Library App (Modul Fragment) dengan menerapkan konsep **Background Thread** menggunakan **Executor** dan **Handler**. Proses yang berpotensi berat — seperti pencarian dan pemuatan data — dipindahkan ke background thread agar aplikasi tetap responsif dan Main Thread tidak terbebani.

Selain itu, ditambahkan **ProgressBar** sebagai feedback visual kepada pengguna bahwa ada proses yang sedang berjalan di latar belakang.

---

## File yang Diubah

Dari seluruh file Library App, hanya empat file yang diperbarui pada tugas ini:

| File | Status | Keterangan |
|---|---|---|
| `HomeFragment.java` | ✅ Diubah | Ditambahkan Executor + Handler untuk proses search di background thread, serta ProgressBar loading |
| `FavoriteFragment.java` | ✅ Diubah | Ditambahkan Executor + Handler untuk proses load data favorit di background thread, serta ProgressBar loading |
| `fragment_home.xml` | ✅ Diubah | Ditambahkan komponen ProgressBar dengan visibility GONE sebagai default |
| `fragment_favorite.xml` | ✅ Diubah | Ditambahkan komponen ProgressBar dengan visibility GONE sebagai default |
| File lainnya | — Tidak berubah | MainActivity, DetailActivity, Adapter, dll tidak diubah sama sekali |

---

## Konsep Background Thread yang Diterapkan

### 1. Executor — mengelola background thread

Menggunakan `ExecutorService` dengan tipe `newSingleThreadExecutor()` sesuai yang diajarkan di materi — karena setiap Fragment hanya butuh satu background thread untuk menangani tugasnya:

```java
// Dideklarasikan sebagai variabel class
private ExecutorService executor = Executors.newSingleThreadExecutor();
private Handler mainHandler = new Handler(Looper.getMainLooper());
```

Tipe-tipe Executor yang diajarkan di materi:

| Tipe | Penjelasan | Dipakai di tugas ini |
|---|---|---|
| `newSingleThreadExecutor()` | Satu thread — untuk satu jenis tugas | ✅ Ya |
| `newFixedThreadPool(n)` | Pool thread tetap — untuk tugas paralel | Tidak |
| `newCachedThreadPool()` | Thread dinamis — untuk banyak tugas pendek | Tidak |

### 2. Handler — jembatan ke Main Thread

Sesuai aturan Android, update UI hanya boleh dilakukan dari Main Thread. Handler dengan `Looper.getMainLooper()` digunakan untuk mengirim hasil dari background thread kembali ke Main Thread:

```java
// Kirim tugas ke Main Thread setelah background selesai
mainHandler.post(() -> {
    adapter.updateList(hasil); // update UI — aman karena sudah di Main Thread
    showLoading(false);
});
```

> Tanpa `mainHandler.post()`, mencoba update UI dari background thread akan menyebabkan aplikasi crash dengan error `CalledFromWrongThreadException`.

### 3. Pola lengkap Executor + Handler

Berikut pola utama yang diterapkan di `filterBooksInBackground()` dan `loadFavoritesInBackground()`:

```java
private void filterBooksInBackground() {
    // Ambil query di Main Thread sebelum masuk background
    String query = etSearch.getText().toString().toLowerCase().trim();

    showLoading(true); // tampilkan ProgressBar — masih di Main Thread

    executor.execute(() -> {
        // ── INI BACKGROUND THREAD ──
        try { Thread.sleep(400); } // simulasi proses berat
        catch (InterruptedException e) { e.printStackTrace(); }

        // proses filter data
        List<Book> hasil = new ArrayList<>();
        for (Book b : DataHelper.getAllBooks()) {
            if (b.getTitle().toLowerCase().contains(query)) hasil.add(b);
        }

        mainHandler.post(() -> {
            // ── KEMBALI KE MAIN THREAD ──
            adapter.updateList(hasil);
            showLoading(false); // sembunyikan ProgressBar
        });
    });
}
```

### 4. ProgressBar — feedback visual loading

Ditambahkan ke XML dengan `visibility="gone"` sebagai default, lalu dikontrol dari Java:

```xml
<!-- fragment_home.xml & fragment_favorite.xml -->
<ProgressBar
    android:id="@+id/progress_bar"
    style="?android:attr/progressBarStyle"
    android:indeterminateTint="@color/text_primary"
    android:visibility="gone" />
```

```java
// Fungsi showLoading() yang mengontrol tampilan
private void showLoading(boolean isLoading) {
    if (isLoading) {
        progressBar.setVisibility(View.VISIBLE);   // ProgressBar tampil
        recyclerView.setVisibility(View.GONE);     // list disembunyikan
    } else {
        progressBar.setVisibility(View.GONE);      // ProgressBar hilang
        recyclerView.setVisibility(View.VISIBLE);  // list tampil
    }
}
```

### 5. Cleanup — executor.shutdown() di onDestroy()

Executor wajib dimatikan saat Fragment tidak lagi digunakan untuk mencegah memory leak:

```java
@Override
public void onDestroy() {
    super.onDestroy();
    executor.shutdown(); // wajib — matikan thread saat Fragment destroy
}
```

> Tanpa `executor.shutdown()`, thread akan terus berjalan di background meski Fragment sudah tidak ada — ini disebut memory leak dan dapat memperlambat aplikasi.

---

## Alur Kerja Background Thread

```
[Main Thread]       User ketik di search / buka tab Favorit
                            ↓
[Main Thread]       showLoading(true) → ProgressBar VISIBLE, RecyclerView GONE
                            ↓
[Background Thread] executor.execute() → Thread.sleep() → proses filter/load data
                            ↓
[Background Thread] Data selesai diproses, siap dikirim ke UI
                            ↓
[Main Thread]       mainHandler.post() → adapter diperbarui, showLoading(false)
                            ↓
[Main Thread]       ProgressBar GONE, RecyclerView VISIBLE, data tampil
```

---

## Perbandingan: Sebelum vs Sesudah Update

| Aspek | Sebelum (Modul Fragment) | Sesudah (+ Thread) |
|---|---|---|
| Proses search | Di Main Thread langsung | Di Background Thread via Executor |
| Proses load favorit | Di Main Thread langsung | Di Background Thread via Executor |
| Update UI setelah proses | Langsung | Lewat `mainHandler.post()` |
| Feedback loading | Tidak ada | ProgressBar muncul saat proses berjalan |
| Cleanup resource | Tidak perlu | `executor.shutdown()` di `onDestroy()` |
| Risiko freeze UI | Ada jika data banyak | Tidak ada — proses berat terpisah |

---

## Mengapa Tidak Pakai Thread Manual?

Materi mengajarkan tiga cara: Thread manual, Handler, dan Executor. Di tugas ini dipilih Executor karena lebih terstruktur dan efisien — tidak perlu membuat thread satu per satu, cukup kirim tugas ke Executor dan dia yang mengelola thread-nya secara otomatis.

Untuk aplikasi skala kecil seperti Library App, `newSingleThreadExecutor()` sudah lebih dari cukup. Jika data yang diproses lebih kompleks dan berjumlah besar, bisa ditingkatkan ke `newFixedThreadPool(n)`.