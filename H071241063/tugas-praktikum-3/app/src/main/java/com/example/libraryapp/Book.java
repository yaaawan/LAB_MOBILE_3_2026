package com.example.libraryapp;

public class Book {
    private int id;
    private String title;
    private String author;
    private int year;
    private String genre;
    private float rating;
    private String blurb;
    private String coverImage; // nama file di drawable, misal "cover_laskar_pelangi"
    private boolean liked;

    public Book(int id, String title, String author, int year, String genre,
                float rating, String blurb, String coverImage) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.rating = rating;
        this.blurb = blurb;
        this.coverImage = coverImage;
        this.liked = false;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public String getGenre() { return genre; }
    public float getRating() { return rating; }
    public String getBlurb() { return blurb; }
    public String getCoverImage() { return coverImage; }
    public boolean isLiked() { return liked; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setYear(int year) { this.year = year; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setRating(float rating) { this.rating = rating; }
    public void setBlurb(String blurb) { this.blurb = blurb; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public void setLiked(boolean liked) { this.liked = liked; }
}
