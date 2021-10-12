package com.example.businesscardstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.businesscardstore.Adapter.Adapter;
import com.example.businesscardstore.Model.model;
import com.example.businesscardstore.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


import java.util.ArrayList;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PERMISSION_REQUEST_CODE = 3;
    private Bitmap imageBitmap;
    boolean isCaptured = false;
    AlertDialog dialog;
    ArrayList<model>  modelsInMain ;
    Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        modelsInMain =new ArrayList<>();
        model temp = new model("This is Sample text !!!");
        modelsInMain.add(temp);
        binding.capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
                isCaptured = true;



            }
        });
         adapter = new Adapter( getApplicationContext(),modelsInMain);
        binding.rcvid.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rcvid.setLayoutManager(linearLayoutManager);
        binding.captureImgId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageBitmap !=null){
                    Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                    intent.putExtra("imageBitMap", imageBitmap);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please take photo",Toast.LENGTH_SHORT).show();
                }

            }
        });
        binding.detectTextid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isCaptured){ showLoadingDialog();
                detectText();}
                else{Toast.makeText(getApplicationContext(), "Capture image",Toast.LENGTH_SHORT).show();}

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraToCapture();
            } else {
                // Toast showing permission is denied
            }
        }
    }
    private void detectText() {

        InputImage inputImage = InputImage.fromBitmap(imageBitmap,0);
        TextRecognizer  textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = textRecognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                    processTextFromImage(text);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure( Exception e) {
                Toast.makeText(getApplicationContext(), "Error is : "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processTextFromImage(Text text) {
        StringBuilder builder = new StringBuilder();
        builder.append(text.getText());
        modelsInMain.clear();
        for(Text.TextBlock block : text.getTextBlocks()){
            String blockText = block.getText();
            Point[] blockCornerPoint = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for(Text.Line line : block.getLines()){
                String lineText = line.getText();
                modelsInMain.add(new model(lineText));
                Point[] lineCornerPoint = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for(Text.Element element : line.getElements()){
                    String elementText = element.getText();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();          }
            }
            adapter.notifyDataSetChanged();

        }
        dismissLoadingDialog();
    }

    void showLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_loading_dialog, null));
        builder.setCancelable(true);
Toast.makeText(getApplicationContext(),"dialog will be shown", Toast.LENGTH_SHORT).show();
        dialog = builder.create();
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Handler run", Toast.LENGTH_SHORT).show();

            }
        }, 5000);
        Toast.makeText(getApplicationContext(),"dialog delayed", Toast.LENGTH_SHORT).show();

    }
    void dismissLoadingDialog(){
        Toast.makeText(getApplicationContext(),"dismissing dialog", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
   private void checkPermissions(){

       if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
               || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
               || checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
           // Ask for all permissions on runtime
           String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
           requestPermissions(permissions, PERMISSION_REQUEST_CODE);
       } else {
           openCameraToCapture();
           // permissions are already granted
       }
//        int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
//        int externalPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        return  (cameraPermission == PackageManager.PERMISSION_GRANTED) && (externalPermission==PackageManager.PERMISSION_GRANTED);
//
}

    private void openCameraToCapture() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Take image from Camera");
       // img_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, img_uri);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            binding.captureImgId.setImageBitmap(imageBitmap);
        }
    }

    private void dispatchTakePictureIntent() {
        // in the method we are displaying an intent to capture our image.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // on below line we are calling a start activity
        // for result method to get the image captured.
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // calling on activity result method.
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            // on below line we are getting
//            // data from our bundles. .
//            Bundle extras = data.getExtras();
//            imageBitmap = (Bitmap) extras.get("data");
//
//            // below line is to set the
//            // image bitmap to our image.
//            binding.captureImgId.setImageBitmap(imageBitmap);
//        }
//    }


//
//    private void detectTxt() {
//        // this is a method to detect a text from image.
//        // below line is to create variable for firebase
//        // vision image and we are getting image bitmap.
//        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
//
//        // below line is to create a variable for detector and we
//        // are getting vision text detector from our firebase vision.
//        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
//
//        // adding on success listener method to detect the text from image.
//        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//            @Override
//            public void onSuccess(FirebaseVisionText firebaseVisionText) {
//                // calling a method to process
//                // our text after extracting.
//                processTxt(firebaseVisionText);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                // handling an error listener.
//                Toast.makeText(MainActivity.this, "Fail to detect the text from image..", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void processTxt(FirebaseVisionText text) {
//        // below line is to create a list of vision blocks which
//        // we will get from our firebase vision text.
//        List<FirebaseVisionText.Block> blocks = text.getBlocks();
//
//        // checking if the size of the
//        // block is not equal to zero.
//        if (blocks.size() == 0) {
//            // if the size of blocks is zero then we are displaying
//            // a toast message as no text detected.
//            Toast.makeText(MainActivity.this, "No Text ", Toast.LENGTH_LONG).show();
//            return;
//        }
//        // extracting data from each block using a for loop.
//        for (FirebaseVisionText.Block block : text.getBlocks()) {
//            // below line is to get text
//            // from each block.
//            String txt = block.getText();
//
//            // below line is to set our
//            // string to our text view.
//            binding.detectedTextId.setText(txt);
//        }
//    }
}