package com.ankit.vaani;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ankit.vaani.adaptor.ImageAdaptor;
import com.ankit.vaani.model.Image;
import com.ankit.vaani.repository.ImageRepo;
import com.ankit.vaani.repository.impl.ImageRepoImpl;
import com.ankit.vaani.upload.UploadFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PIC = 0;
    private static final int REQUEST_CAMERA_PERMISSION = 1000;
    private static final String TAG = "MainActivity";
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.ankit.vaani.fileprovider";

    private static ImageRepo imageRepo;
    private static ImageAdaptor imageAdaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        imageRepo = new ImageRepoImpl(getApplicationContext());
        List<Image> images = imageRepo.getAllImages();
        imageAdaptor = new ImageAdaptor(images, getApplicationContext());

        RecyclerView rcvImages = (RecyclerView) findViewById(R.id.showImages);
        rcvImages.setAdapter(imageAdaptor);
        rcvImages.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
    }

    public void launchAddImage(View view) {

        // Initialize Camera Intent
        File path = new File(MainActivity.this.getFilesDir(), "images/");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, "image.jpg");
        Uri imageUri = FileProvider.getUriForFile(MainActivity.this, CAPTURE_IMAGE_FILE_PROVIDER, image);

        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        // Initialize Gallery Intent
        Intent pickPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("image/*");
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Choose between Camera and Gallery
        Intent chooser = Intent.createChooser(pickPhoto, "Select from:");

        Intent[] intentArray = { takePicture };
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);


        startActivityForResult(chooser, REQUEST_PIC);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Camera permissions granted");
            } else {
                MainActivity.this.finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_PIC && resultCode == Activity.RESULT_OK && data != null) {
            Bundle bundle = new Bundle();
            Uri selectedImage = data.getData();
            if (selectedImage == null) {
                File path = new File(MainActivity.this.getFilesDir(), "images/");
                if (!path.exists()) path.mkdirs();
                File image = new File(path, "image.jpg");
                selectedImage = FileProvider.getUriForFile(MainActivity.this, CAPTURE_IMAGE_FILE_PROVIDER, image);
            } else {
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(selectedImage, takeFlags);
            }
            try {
                InputStream iStream = getContentResolver().openInputStream(selectedImage);
                byte[] inputData = getBytes(iStream);
                bundle.putByteArray(UploadFragment.UPLOAD_IMAGE_PATH, inputData);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            UploadFragment uploadFragment = new UploadFragment(imageRepo, imageAdaptor);
            uploadFragment.setArguments(bundle);
            uploadFragment.show(getSupportFragmentManager(), UploadFragment.TAG);
        }
    }


    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}