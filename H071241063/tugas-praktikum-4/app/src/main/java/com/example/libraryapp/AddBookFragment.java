package com.example.libraryapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddBookFragment extends Fragment {

    private static final int PICK_IMAGE = 100;

    private EditText etTitle, etAuthor, etYear, etRating, etBlurb;
    private Spinner spinnerGenre;
    private ImageView ivCoverPreview;
    private Button btnPickImage, btnSave;
    private String selectedImageName = "cover_placeholder";
    private Uri selectedImageUri = null;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTitle       = view.findViewById(R.id.et_title);
        etAuthor      = view.findViewById(R.id.et_author);
        etYear        = view.findViewById(R.id.et_year);
        etRating      = view.findViewById(R.id.et_rating);
        etBlurb       = view.findViewById(R.id.et_blurb);
        spinnerGenre  = view.findViewById(R.id.spinner_genre);
        ivCoverPreview = view.findViewById(R.id.iv_cover_preview);
        btnPickImage  = view.findViewById(R.id.btn_pick_image);
        btnSave       = view.findViewById(R.id.btn_save);

        // Setup genre spinner
        String[] genres = {"Fiksi", "Non-Fiksi", "Sains", "Sejarah", "Psikologi", "Filsafat", "Bisnis"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, genres);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(spinnerAdapter);

        // Image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        ivCoverPreview.setImageURI(selectedImageUri);
                        selectedImageName = "cover_custom_" + System.currentTimeMillis();
                    }
                });

        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> saveBook());
    }

    private void saveBook() {
        String title  = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String yearStr = etYear.getText().toString().trim();
        String ratingStr = etRating.getText().toString().trim();
        String blurb  = etBlurb.getText().toString().trim();
        String genre  = spinnerGenre.getSelectedItem().toString();

        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(getContext(), "Judul dan penulis wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        int year = yearStr.isEmpty() ? 2024 : Integer.parseInt(yearStr);
        float rating = ratingStr.isEmpty() ? 4.0f : Float.parseFloat(ratingStr);
        rating = Math.min(5.0f, Math.max(1.0f, rating));
        if (blurb.isEmpty()) blurb = "Deskripsi belum tersedia.";

        Book newBook = new Book(0, title, author, year, genre, rating, blurb, selectedImageName);
        DataHelper.addBook(newBook);

        Toast.makeText(getContext(), "Buku berhasil ditambahkan!", Toast.LENGTH_SHORT).show();

        // Reset form
        etTitle.setText("");
        etAuthor.setText("");
        etYear.setText("");
        etRating.setText("");
        etBlurb.setText("");
        ivCoverPreview.setImageResource(R.drawable.cover_placeholder);
        selectedImageName = "cover_placeholder";
        selectedImageUri = null;

        // Kembali ke Home
        ((MainActivity) getActivity()).navigateToHome();
    }
}
