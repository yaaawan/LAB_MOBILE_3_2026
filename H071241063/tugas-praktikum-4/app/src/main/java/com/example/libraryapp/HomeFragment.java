package com.example.libraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private EditText etSearch;
    private ChipGroup chipGroup;
    private TextView tvBookCount;
    private ProgressBar progressBar;

    // Background thread tools
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private String selectedGenre = "Semua";
    private List<Book> displayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_books);
        etSearch     = view.findViewById(R.id.et_search);
        chipGroup    = view.findViewById(R.id.chip_group_genre);
        tvBookCount  = view.findViewById(R.id.tv_book_count);
        progressBar  = view.findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BookAdapter(getContext(), displayList, new BookAdapter.OnBookClickListener() {
            @Override
            public void onBookClick(Book book) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("book_id", book.getId());
                startActivity(intent);
            }

            @Override
            public void onLikeClick(Book book, int position) {
                // Like status sudah diubah langsung di adapter
            }
        });

        recyclerView.setAdapter(adapter);

        buildGenreChips();
        filterBooksInBackground(); // load awal pakai background thread

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBooksInBackground(); // tiap ketik → background thread
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void buildGenreChips() {
        chipGroup.removeAllViews();
        List<String> genres = DataHelper.getGenres();

        for (String genre : genres) {
            Chip chip = new Chip(getContext());
            chip.setText(genre);
            chip.setCheckable(true);
            chip.setChecked(genre.equals(selectedGenre));
            chip.setChipBackgroundColorResource(R.color.chip_bg_selector);
            chip.setTextColor(getResources().getColorStateList(R.color.chip_text_selector));
            chip.setChipStrokeColorResource(R.color.chip_stroke_selector);
            chip.setChipStrokeWidth(1f);
            chip.setTextSize(11f);
            chip.setOnClickListener(v -> {
                selectedGenre = genre;
                buildGenreChips();
                filterBooksInBackground();
            });
            chipGroup.addView(chip);
        }
    }

    /**
     * Proses filter dijalankan di background thread (Executor).
     * Hasilnya dikembalikan ke Main Thread lewat Handler untuk update UI.
     */
    private void filterBooksInBackground() {
        // Ambil query sekarang (harus di main thread sebelum masuk background)
        String query = etSearch.getText().toString().toLowerCase().trim();

        // Tampilkan ProgressBar, sembunyikan RecyclerView
        showLoading(true);

        executor.execute(() -> {
            // ── BACKGROUND THREAD ──
            // Simulasi delay agar efek loading terasa (sesuai materi)
            try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }

            // Proses filter data
            List<Book> all = new ArrayList<>(DataHelper.getAllBooks());
            Collections.sort(all, (a, b) -> b.getId() - a.getId()); // terbaru di atas

            List<Book> hasil = new ArrayList<>();
            for (Book b : all) {
                boolean matchGenre = selectedGenre.equals("Semua") || b.getGenre().equals(selectedGenre);
                boolean matchQuery = query.isEmpty()
                        || b.getTitle().toLowerCase().contains(query)
                        || b.getAuthor().toLowerCase().contains(query);
                if (matchGenre && matchQuery) hasil.add(b);
            }

            // ── KEMBALI KE MAIN THREAD ──
            mainHandler.post(() -> {
                displayList.clear();
                displayList.addAll(hasil);
                adapter.updateList(displayList);
                tvBookCount.setText(hasil.size() + " buku");
                showLoading(false); // sembunyikan ProgressBar
            });
        });
    }

    private void showLoading(boolean isLoading) {
        if (progressBar == null || recyclerView == null) return;
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvBookCount.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            tvBookCount.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        filterBooksInBackground(); // refresh saat kembali dari DetailActivity
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // wajib: matikan executor saat fragment destroy
    }
}