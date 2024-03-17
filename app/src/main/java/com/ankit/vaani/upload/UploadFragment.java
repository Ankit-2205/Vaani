package com.ankit.vaani.upload;


import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.fragment.app.DialogFragment;

import com.ankit.vaani.adaptor.ImageAdaptor;
import com.ankit.vaani.model.Image;
import com.ankit.vaani.repository.ImageRepo;

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
        // Use the Builder class for convenient dialog construction.
        Builder builder = new Builder(requireActivity());
        final byte[] imageData = getArguments() != null ?
                getArguments().getByteArray(UPLOAD_IMAGE_PATH) : null;

        Image image = new Image();
        image.setData(imageData);
        if (imageData != null && imageData.length > 0) {
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
