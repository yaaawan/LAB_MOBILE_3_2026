package com.example.libraryapp;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    public interface OnBookClickListener {
        void onBookClick(Book book);
        void onLikeClick(Book book, int position);
    }

    private List<Book> books;
    private Context context;
    private OnBookClickListener listener;

    public BookAdapter(Context context, List<Book> books, OnBookClickListener listener) {
        this.context = context;
        this.books = books;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);

        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvYear.setText(String.valueOf(book.getYear()));
        holder.tvGenre.setText(book.getGenre());
        holder.tvLike.setText(book.isLiked() ? "♥" : "♡");

        // Load cover image
        int resId = context.getResources().getIdentifier(
                book.getCoverImage(), "drawable", context.getPackageName());
        if (resId != 0) {
            holder.ivCover.setImageResource(resId);
        } else {
            holder.ivCover.setImageResource(R.drawable.cover_placeholder);
        }

        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
        holder.tvLike.setOnClickListener(v -> {
            book.setLiked(!book.isLiked());
            holder.tvLike.setText(book.isLiked() ? "♥" : "♡");
            listener.onLikeClick(book, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() { return books.size(); }

    public void updateList(List<Book> newList) {
        this.books = newList;
        notifyDataSetChanged();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvAuthor, tvYear, tvGenre, tvLike;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover  = itemView.findViewById(R.id.iv_cover);
            tvTitle  = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvYear   = itemView.findViewById(R.id.tv_year);
            tvGenre  = itemView.findViewById(R.id.tv_genre);
            tvLike   = itemView.findViewById(R.id.tv_like);
        }
    }
}
