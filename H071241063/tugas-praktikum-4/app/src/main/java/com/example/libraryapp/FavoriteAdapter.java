package com.example.libraryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavViewHolder> {

    public interface OnFavClickListener {
        void onBookClick(Book book);
        void onUnlikeClick(Book book, int position);
    }

    private List<Book> books;
    private Context context;
    private OnFavClickListener listener;

    public FavoriteAdapter(Context context, List<Book> books, OnFavClickListener listener) {
        this.context = context;
        this.books = books;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        Book book = books.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());

        int resId = context.getResources().getIdentifier(
                book.getCoverImage(), "drawable", context.getPackageName());
        if (resId != 0) {
            holder.ivCover.setImageResource(resId);
        } else {
            holder.ivCover.setImageResource(R.drawable.cover_placeholder);
        }

        holder.itemView.setOnClickListener(v -> listener.onBookClick(book));
        holder.tvUnlike.setOnClickListener(v -> {
            book.setLiked(false);
            listener.onUnlikeClick(book, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() { return books.size(); }

    public void updateList(List<Book> newList) {
        this.books = newList;
        notifyDataSetChanged();
    }

    static class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvAuthor, tvUnlike;

        FavViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover  = itemView.findViewById(R.id.iv_cover);
            tvTitle  = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvUnlike = itemView.findViewById(R.id.tv_unlike);
        }
    }
}
