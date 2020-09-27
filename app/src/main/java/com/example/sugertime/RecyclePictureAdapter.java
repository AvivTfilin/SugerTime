package com.example.sugertime;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclePictureAdapter extends RecyclerView.Adapter<RecyclePictureAdapter.ViewHolder> {

    ArrayList<String> pictureList;
    Context context;

    public RecyclePictureAdapter(Context applicationContext, ArrayList<String> imageList) {
        this.context = applicationContext;
        this.pictureList = imageList;
    }

    @NonNull
    @Override
    public RecyclePictureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        final ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image_IMG_picture.setImageURI(Uri.parse(pictureList.get(position)));

        Glide.with(context).load(pictureList.get(position)).into(holder.image_IMG_picture);
    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image_IMG_picture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_IMG_picture = itemView.findViewById(R.id.image_IMG_picture);
        }
    }

}
