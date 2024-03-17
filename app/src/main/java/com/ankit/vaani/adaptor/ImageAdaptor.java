package com.ankit.vaani.adaptor;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ankit.vaani.R;
import com.ankit.vaani.model.Image;
import com.ankit.vaani.utils.ThumbnailUtils;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;

public class ImageAdaptor extends RecyclerView.Adapter<ImageAdaptor.ViewHolder> {

    private final List<Image> images;
    private final Context context;
    private final TextToSpeech textToSpeech;

    public ImageAdaptor(List<Image> images, Context context) {
        this.images = images;
        this.context = context;

        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if (i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(new Locale("en", "IN"));
                }
            }
        });
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View imageView = inflater.inflate(R.layout.image_card_view, parent, false);

        ViewHolder viewHolder = new ViewHolder(imageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Image image = images.get(position);
        holder.description.setText(image.getDescription());

        Bitmap bitmap = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
        ThumbnailUtils.setThumbnail(bitmap, holder.data, context);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence description = ((TextView) v.findViewById(R.id.image_description)).getText();
                textToSpeech.speak(description, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void addImage(Image image) {
        images.add(image);
        this.notifyItemInserted(images.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView data;

        public TextView description;

        public View card;

        public ViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.image_description);
            data = (ImageView) itemView.findViewById(R.id.image_data);
            card = itemView;
        }
    }

}
