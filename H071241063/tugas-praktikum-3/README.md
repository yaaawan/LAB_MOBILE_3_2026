# Library App
> Tugas Praktikum 4 — Fragment · Pemrograman Mobile

Aplikasi Android sederhana berbasis Fragment untuk mengelola koleksi buku digital — menampilkan daftar, pencarian, favorit, dan penambahan buku baru.

---

## Tentang Tugas

Tugas ini mengimplementasikan konsep **Fragment** dalam Android untuk membangun aplikasi Library App yang terdiri dari tiga halaman utama yang dikelola dalam satu Activity menggunakan Bottom Navigation. Setiap halaman direpresentasikan sebagai Fragment terpisah yang dapat digunakan kembali dan dikelola secara independen.

---

## Fitur Aplikasi

| Halaman | Fitur |
|---|---|
| Beranda | Daftar 17 buku dengan RecyclerView, pencarian judul & penulis, filter genre |
| Favorit | Daftar buku yang di-like, refresh otomatis setiap tab dibuka, empty state |
| Tambah Buku | Form input data buku, pilih foto cover dari galeri, validasi input |
| Detail Buku | Info lengkap buku, rating bintang, sinopsis, tombol like/unlike |

---

## Struktur File

### Java

| File | Keterangan |
|---|---|
| `MainActivity.java` | Activity utama yang mengelola semua Fragment via FragmentManager dan BottomNavigationView |
| `HomeFragment.java` | Fragment halaman Beranda — menampilkan daftar buku, search, dan filter genre |
| `FavoriteFragment.java` | Fragment halaman Favorit — menampilkan buku yang di-like dengan refresh otomatis |
| `AddBookFragment.java` | Fragment halaman Tambah — form input buku baru dan pilih cover dari galeri |
| `DetailActivity.java` | Activity terpisah untuk halaman detail buku — menerima data via Intent |
| `BookAdapter.java` | RecyclerView Adapter untuk daftar buku di Beranda |
| `FavoriteAdapter.java` | RecyclerView Adapter untuk daftar buku di Favorit |
| `Book.java` | Model data buku (id, judul, penulis, tahun, genre, rating, blurb, cover, liked) |
| `DataHelper.java` | Sumber data in-memory — 17 dummy buku + fungsi CRUD sederhana |

### Layout XML

| File | Keterangan |
|---|---|
| `activity_main.xml` | FrameLayout sebagai wadah Fragment + BottomNavigationView |
| `fragment_home.xml` | Search bar, ChipGroup genre, RecyclerView daftar buku |
| `fragment_favorite.xml` | RecyclerView favorit + empty state |
| `fragment_add_book.xml` | Form input dengan EditText, Spinner genre, ImageView preview cover |
| `activity_detail.xml` | Hero cover image, info buku lengkap, tombol like |
| `item_book.xml` | Card item buku untuk RecyclerView di Beranda |
| `item_favorite.xml` | Card item buku untuk RecyclerView di Favorit |

---

## Konsep Fragment yang Diterapkan

### 1. Fragment Lifecycle — onCreateView & onViewCreated

Setiap Fragment mengikuti siklus hidup yang diajarkan di materi. Pemisahan antara inflate layout dan setup logika UI dilakukan sesuai standar:

```java
// onCreateView — hanya inflate layout
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
    return inflater.inflate(R.layout.fragment_home, container, false);
}

// onViewCreated — setup semua logika UI di sini
@Override
public void onViewCreated(View view, Bundle savedInstanceState) {
    recyclerView = view.findViewById(R.id.recycler_books); // view.findViewById
    etSearch = view.findViewById(R.id.et_search);
}
```

> Perbedaan utama dari Activity: di Fragment menggunakan `view.findViewById()`, bukan langsung `findViewById()` — karena view harus dicari di dalam layout Fragment, bukan Activity.

### 2. FragmentManager & FragmentTransaction

MainActivity mengelola pergantian Fragment menggunakan `getSupportFragmentManager()`:

```java
private boolean loadFragment(Fragment fragment) {
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_container, fragment) // ganti isi container
        .commit();                                  // konfirmasi transaksi
    return true;
}
```

### 3. onResume untuk Refresh Data

Memanfaatkan Fragment Lifecycle — `onResume()` dipanggil setiap Fragment kembali ke layar, digunakan untuk memastikan data selalu ter-update:

```java
@Override
public void onResume() {
    super.onResume();
    filterBooks(); // refresh status like saat balik dari DetailActivity
}
```

### 4. Komunikasi Fragment → Activity → Fragment

Data mengalir dari AddBookFragment ke MainActivity, lalu navigasi dikelola Activity:

```java
// Di AddBookFragment — memanggil fungsi di Activity induk
((MainActivity) getActivity()).navigateToHome();

// Di MainActivity — fungsi yang dipanggil
public void navigateToHome() {
    bottomNav.setSelectedItemId(R.id.nav_home);
}
```

### 5. Intent untuk Kirim Data ke Activity

Data buku dikirim ke DetailActivity menggunakan Intent dan diterima via `getIntent()`:

```java
// Pengirim — di HomeFragment
Intent intent = new Intent(getActivity(), DetailActivity.class);
intent.putExtra("book_id", book.getId());
startActivity(intent);

// Penerima — di DetailActivity
int bookId = getIntent().getIntExtra("book_id", -1);
```

---

## Alur Kerja Aplikasi

```
App Launch → MainActivity.onCreate() → loadFragment(HomeFragment) → Tap tab → replace Fragment

Klik buku → Intent + book_id → DetailActivity → Back → onResume() refresh

Isi form Tambah → DataHelper.addBook() → navigateToHome() → HomeFragment refresh
```

---

## Perbedaan Activity vs Fragment dalam Kode

| Aspek | Activity | Fragment |
|---|---|---|
| Method utama | `onCreate()` | `onViewCreated()` |
| Akses View | `findViewById()` | `view.findViewById()` |
| Akses Context | `this` | `getActivity()` |
| Inflate layout | via `setContentView()` | via `onCreateView()` |
| Navigasi ke halaman baru | `startActivity(Intent)` | lewat FragmentManager atau Activity induk |

---

## Dependencies

```gradle
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'androidx.cardview:cardview:1.0.0'
implementation 'androidx.fragment:fragment:1.6.2'
```