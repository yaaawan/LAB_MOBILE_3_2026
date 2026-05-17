package com.example.libraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private LinearLayout emptyState;
    private TextView tvFavCount;
    private ProgressBar progressBar;

    // Background thread tools
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private List<Book> likedBooks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_favorites);
        emptyState   = view.findViewById(R.id.empty_state);
        tvFavCount   = view.findViewById(R.id.tv_fav_count);
        progressBar  = view.findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FavoriteAdapter(getContext(), likedBooks, new FavoriteAdapter.OnFavClickListener() {
            @Override
            public void onBookClick(Book book) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("book_id", book.getId());
                startActivity(intent);
            }

            @Override
            public void onUnlikeClick(Book book, int position) {
                // Setelah unlike, reload data lewat background thread
                loadFavoritesInBackground();
            }
        });

        recyclerView.setAdapter(adapter);

        // Load awal pakai background thread
        loadFavoritesInBackground();
    }

    /**
     * Proses ambil data favorit dijalankan di background thread (Executor).
     * Hasilnya dikembalikan ke Main Thread lewat Handler untuk update UI.
     */
    private void loadFavoritesInBackground() {
        showLoading(true);

        executor.execute(() -> {
            // ── BACKGROUND THREAD ──
            // Simulasi delay proses (sesuai materi)
            try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

            // Ambil data buku yang di-like
            List<Book> hasil = DataHelper.getLikedBooks();

            // ── KEMBALI KE MAIN THREAD ──
            mainHandler.post(() -> {
                likedBooks.clear();
                likedBooks.addAll(hasil);
                adapter.updateList(likedBooks);
                updateEmptyState(likedBooks);
                showLoading(false);
            });
        });
    }

    private void showLoading(boolean isLoading) {
        if (progressBar == null || recyclerView == null) return;
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.GONE);
            tvFavCount.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            tvFavCount.setVisibility(View.VISIBLE);
        }
    }

    private void updateEmptyState(List<Book> list) {
        tvFavCount.setText(list.size() + " buku disukai");
        if (list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh setiap kali tab Favorit dibuka
        loadFavoritesInBackground();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // wajib: matikan executor saat fragment destroy
    }
}