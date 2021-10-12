package com.example.businesscardstore;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.example.businesscardstore.databinding.ActivityImageBinding;

public class ImageActivity extends AppCompatActivity {
    ActivityImageBinding binding;
    private Bitmap imgBitMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imgBitMap = getIntent().getParcelableExtra("imageBitMap");
        binding.imageView.setImageBitmap(imgBitMap);

    }
}