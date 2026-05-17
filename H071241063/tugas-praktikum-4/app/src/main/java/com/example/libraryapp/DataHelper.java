package com.example.libraryapp;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {

    private static List<Book> books = new ArrayList<>();
    private static int nextId = 18;

    static {
        books.add(new Book(1,  "Laskar Pelangi",           "Andrea Hirata",        2005, "Fiksi",      4.8f, "Kisah persahabatan anak-anak Belitung yang penuh semangat dalam mengejar pendidikan di tengah keterbatasan. Sebuah novel mengharukan tentang mimpi dan keberanian.",        "cover_laskar_pelangi"));
        books.add(new Book(2,  "Bumi Manusia",             "Pramoedya Ananta Toer",1980, "Sejarah",    4.9f, "Kisah Minke, seorang pribumi terpelajar di era kolonial Belanda, yang jatuh cinta dengan Annelies. Novel pertama dari Tetralogi Buru yang legendaris.",                   "cover_bumi_manusia"));
        books.add(new Book(3,  "Sapiens",                  "Yuval Noah Harari",    2011, "Sains",      4.7f, "Sejarah singkat umat manusia dari zaman batu hingga era modern. Harari mengajak kita merefleksikan perjalanan panjang Homo Sapiens di bumi ini.",                          "cover_sapiens"));
        books.add(new Book(4,  "Atomic Habits",            "James Clear",          2018, "Psikologi",  4.8f, "Panduan praktis membangun kebiasaan baik dan menghilangkan kebiasaan buruk. Perubahan kecil yang konsisten menghasilkan hasil luar biasa.",                               "cover_atomic_habits"));
        books.add(new Book(5,  "Filosofi Teras",           "Henry Manampiring",    2018, "Filsafat",   4.6f, "Adaptasi filosofi Stoik Yunani-Romawi untuk kehidupan modern Indonesia. Cara menghadapi hal-hal di luar kendali kita dengan ketenangan dan kebijaksanaan.",                "cover_filosofi_teras"));
        books.add(new Book(6,  "The Alchemist",            "Paulo Coelho",         1988, "Fiksi",      4.7f, "Perjalanan Santiago, seorang anak gembala, mengejar mimpinya hingga ke piramida Mesir. Novel tentang mengikuti hati dan menemukan legenda pribadi.",                      "cover_the_alchemist"));
        books.add(new Book(7,  "Thinking Fast and Slow",   "Daniel Kahneman",      2011, "Psikologi",  4.6f, "Kahneman menjelaskan dua sistem berpikir manusia: sistem cepat dan intuitif, serta sistem lambat dan rasional. Bagaimana kita benar-benar membuat keputusan.",             "cover_thinking_fast_slow"));
        books.add(new Book(8,  "Zero to One",              "Peter Thiel",          2014, "Bisnis",     4.5f, "Catatan tentang startup dan bagaimana membangun masa depan. Thiel menantang konvensi bisnis dan mendorong inovasi yang benar-benar baru dan belum pernah ada.",             "cover_zero_to_one"));
        books.add(new Book(9,  "Pulang",                   "Tere Liye",            2015, "Fiksi",      4.5f, "Kisah Bujang, anak rimba Sumatera yang tumbuh menjadi pria terkuat dalam dunia hitam. Novel tentang loyalitas, keluarga, dan pencarian identitas diri.",                  "cover_pulang"));
        books.add(new Book(10, "Educated",                 "Tara Westover",        2018, "Non-Fiksi",  4.7f, "Memoar luar biasa tentang perempuan yang tumbuh tanpa pendidikan formal di pegunungan Idaho dan akhirnya meraih gelar PhD dari Cambridge University.",                    "cover_educated"));
        books.add(new Book(11, "Homo Deus",                "Yuval Noah Harari",    2015, "Sains",      4.6f, "Apa agenda manusia selanjutnya? Harari menelusuri masa depan umat manusia di era kecerdasan buatan, bioteknologi, dan kapitalisme data.",                                  "cover_homo_deus"));
        books.add(new Book(12, "Rich Dad Poor Dad",        "Robert Kiyosaki",      1997, "Bisnis",     4.4f, "Pelajaran keuangan yang tidak diajarkan di sekolah. Perbedaan cara berpikir antara orang kaya dan orang miskin tentang uang, aset, dan investasi.",                       "cover_rich_dad"));
        books.add(new Book(13, "Perahu Kertas",            "Dee Lestari",          2009, "Fiksi",      4.5f, "Kisah cinta Kugy dan Keenan yang penuh dengan mimpi, seni, dan perjalanan menemukan diri sendiri di kota-kota besar Indonesia.",                                          "cover_perahu_kertas"));
        books.add(new Book(14, "Man's Search for Meaning", "Viktor Frankl",        1946, "Filsafat",   4.9f, "Pengalaman psikiater Austria sebagai tahanan kamp konsentrasi Nazi dan penemuannya tentang pentingnya menemukan makna di balik setiap penderitaan.",                       "cover_mans_search"));
        books.add(new Book(15, "Ikigai",                   "Héctor García",        2016, "Psikologi",  4.4f, "Rahasia umur panjang dan kebahagiaan dari orang-orang Jepang di Okinawa. Menemukan alasan untuk bangun dan menjalani setiap hari dengan penuh gairah.",                   "cover_ikigai"));
        books.add(new Book(16, "1984",                     "George Orwell",        1949, "Fiksi",      4.8f, "Distopia klasik tentang masyarakat totaliter di mana Big Brother mengawasi segalanya. Sebuah peringatan abadi tentang bahaya kekuasaan absolut.",                         "cover_1984"));
        books.add(new Book(17, "Deep Work",                "Cal Newport",          2016, "Bisnis",     4.6f, "Kemampuan fokus tanpa gangguan pada tugas kognitif tinggi adalah keterampilan paling berharga di era ekonomi baru yang penuh distraksi digital.",                          "cover_deep_work"));
    }

    public static List<Book> getAllBooks() { return books; }

    public static List<Book> getLikedBooks() {
        List<Book> liked = new ArrayList<>();
        for (Book b : books) if (b.isLiked()) liked.add(b);
        return liked;
    }

    public static void addBook(Book book) {
        book = new Book(nextId++, book.getTitle(), book.getAuthor(), book.getYear(),
                book.getGenre(), book.getRating(), book.getBlurb(), book.getCoverImage());
        books.add(0, book);
    }

    public static List<String> getGenres() {
        List<String> genres = new ArrayList<>();
        genres.add("Semua");
        for (Book b : books) {
            if (!genres.contains(b.getGenre())) genres.add(b.getGenre());
        }
        return genres;
    }
}
