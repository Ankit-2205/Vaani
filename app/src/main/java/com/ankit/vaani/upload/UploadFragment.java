package com.ankit.vaani.upload;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.fragment.app.DialogFragment;

import com.ankit.vaani.adaptor.ImageAdaptor;
import com.ankit.vaani.model.Image;
import com.ankit.vaani.repository.ImageRepo;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UploadFragment extends DialogFragment {

    private ImageRepo imageRepo;
    private ImageAdaptor imageAdaptor;
    public static String TAG = "UploadFragment";
    public static String UPLOAD_IMAGE_PATH = "UPLOAD_IMAGE_PATH";

    public UploadFragment(ImageRepo imageRepo, ImageAdaptor imageAdaptor) {
        this.imageRepo = imageRepo;
        this.imageAdaptor = imageAdaptor;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        // Use the Builder class for convenient dialog construction.
        Builder builder = new Builder(requireActivity());
        final byte[] imageData = getArguments() != null ?
                getArguments().getByteArray(UPLOAD_IMAGE_PATH) : null;

        Image image = new Image();
        if (imageData != null && imageData.length > 0) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                Log.i(TAG, "execution started");
                Bitmap scaledImage = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageData,
                        0, imageData.length), 400, 400, true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                scaledImage.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                scaledImage.recycle();
                image.setData(stream.toByteArray());
                Log.i(TAG, "successfully compressed image");
            });

            executor.shutdown();
            try {
                Log.i(TAG, "waiting for image compression to be completed");
                executor.awaitTermination(100, TimeUnit.SECONDS);
                Log.i(TAG, "image compression completed");
            } catch (InterruptedException e) {
                Log.e(TAG, "error occurred while compressing image", e);
            }

            Log.i(TAG, "handler execution started");
            builder.setTitle("Describe the image");
            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String imageDesc = input.getText().toString();
                    image.setDescription(imageDesc);
                    imageRepo.addImage(image);
                    imageAdaptor.addImage(image);
                    Log.d(TAG, String.format("Uploaded Image: %d bytes, with Description: %s",
                            imageData.length, imageDesc));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            Log.i(TAG, "creating dialog");
            // Create the AlertDialog object and return it.
            return builder.create();
        }

        // If no image selected just show an alert and allow navigation to MainActivity
        builder.setTitle("No image selected");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }
}
