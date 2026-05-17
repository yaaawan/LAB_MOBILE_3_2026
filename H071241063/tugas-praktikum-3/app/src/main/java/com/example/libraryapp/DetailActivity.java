package com.example.libraryapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private Book book;
    private Button btnLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        int bookId = getIntent().getIntExtra("book_id", -1);
        book = findBookById(bookId);

        if (book == null) { finish(); return; }

        ImageView ivCover   = findViewById(R.id.iv_detail_cover);
        TextView tvTitle    = findViewById(R.id.tv_detail_title);
        TextView tvAuthor   = findViewById(R.id.tv_detail_author);
        TextView tvYear     = findViewById(R.id.tv_detail_year);
        TextView tvGenre    = findViewById(R.id.tv_detail_genre);
        TextView tvRating   = findViewById(R.id.tv_detail_rating);
        TextView tvStars    = findViewById(R.id.tv_detail_stars);
        TextView tvBlurb    = findViewById(R.id.tv_detail_blurb);
        btnLike             = findViewById(R.id.btn_like);
        ImageView btnBack   = findViewById(R.id.btn_back);

        // Set data
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());
        tvYear.setText(String.valueOf(book.getYear()));
        tvGenre.setText(book.getGenre());
        tvRating.setText(book.getRating() + " / 5");
        tvStars.setText(buildStars(book.getRating()));
        tvBlurb.setText(book.getBlurb());

        // Cover image
        int resId = getResources().getIdentifier(
                book.getCoverImage(), "drawable", getPackageName());
        if (resId != 0) {
            ivCover.setImageResource(resId);
        } else {
            ivCover.setImageResource(R.drawable.cover_placeholder);
        }

        updateLikeButton();

        btnLike.setOnClickListener(v -> {
            book.setLiked(!book.isLiked());
            updateLikeButton();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void updateLikeButton() {
        if (book.isLiked()) {
            btnLike.setText("♥  Hapus dari Favorit");
            btnLike.setBackgroundResource(R.drawable.bg_btn_dark);
            btnLike.setTextColor(getResources().getColor(R.color.white));
        } else {
            btnLike.setText("♡  Tambah ke Favorit");
            btnLike.setBackgroundResource(R.drawable.bg_btn_light);
            btnLike.setTextColor(getResources().getColor(R.color.text_secondary));
        }
    }

    private String buildStars(float rating) {
        int full = (int) rating;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < full; i++) sb.append("★");
        if (rating - full >= 0.5f) sb.append("½");
        int empty = 5 - full - (rating - full >= 0.5f ? 1 : 0);
        for (int i = 0; i < empty; i++) sb.append("☆");
        return sb.toString();
    }

    private Book findBookById(int id) {
        List<Book> all = DataHelper.getAllBooks();
        for (Book b : all) if (b.getId() == id) return b;
        return null;
    }
}
