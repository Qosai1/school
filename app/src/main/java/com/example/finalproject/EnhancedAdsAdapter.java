package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EnhancedAdsAdapter extends RecyclerView.Adapter<EnhancedAdsAdapter.AdsViewHolder> {

    private List<EnhancedAdvertisement> adsList;
    private Context context;
    private OnAdClickListener clickListener;


    public interface OnAdClickListener {
        void onAdClick(EnhancedAdvertisement advertisement, int position);
        void onShareClick(EnhancedAdvertisement advertisement);
        void onSaveClick(EnhancedAdvertisement advertisement, int position);
    }

    public EnhancedAdsAdapter(Context context, List<EnhancedAdvertisement> adsList) {
        this.context = context;
        this.adsList = adsList;
    }

    public void setOnAdClickListener(OnAdClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public AdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.enhanced_ads_item_layout, parent, false);
        return new AdsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdsViewHolder holder, int position) {
        EnhancedAdvertisement advertisement = adsList.get(position);

        holder.titleText.setText(advertisement.getTitle());
        holder.descriptionText.setText(advertisement.getDescription());
        holder.imageView.setImageResource(advertisement.getImageResource());

        holder.categoryText.setText(advertisement.getCategory());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        holder.dateText.setText(sdf.format(advertisement.getPublishDate()));

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onAdClick(advertisement, position);
            }
        });

        holder.shareButton.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onShareClick(advertisement);
            } else {
                shareAdvertisement(advertisement);
            }
        });

        holder.saveButton.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onSaveClick(advertisement, position);
            } else {
                // Default save behavior
                Toast.makeText(context, "تم حفظ الإعلان", Toast.LENGTH_SHORT).show();
                advertisement.setSaved(!advertisement.isSaved());
                updateSaveButtonIcon(holder.saveButton, advertisement.isSaved());
            }
        });

        holder.detailsButton.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onAdClick(advertisement, position);
            }
        });

        updateSaveButtonIcon(holder.saveButton, advertisement.isSaved());
    }

    private void updateSaveButtonIcon(ImageButton saveButton, boolean isSaved) {
        if (isSaved) {
            saveButton.setImageResource(R.drawable.ic_bookmark_filled);
        } else {
            saveButton.setImageResource(R.drawable.ic_bookmark_border);
        }
    }

    private void shareAdvertisement(EnhancedAdvertisement advertisement) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, advertisement.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                advertisement.getTitle() + "\n\n" +
                        advertisement.getDescription() + "\n\n" +
                        "مشاركة من تطبيق نظام المدرسة");
        context.startActivity(Intent.createChooser(shareIntent, "مشاركة الإعلان عبر"));
    }

    @Override
    public int getItemCount() {
        return adsList.size();
    }

    public void updateList(List<EnhancedAdvertisement> newList) {
        this.adsList = newList;
        notifyDataSetChanged();
    }

    public class AdsViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descriptionText, categoryText, dateText;
        ImageView imageView;
        Button detailsButton, shareButton;
        ImageButton saveButton;

        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.adTitle);
            descriptionText = itemView.findViewById(R.id.adDescription);
            imageView = itemView.findViewById(R.id.adImage);
            categoryText = itemView.findViewById(R.id.adCategory);
            dateText = itemView.findViewById(R.id.adDate);
            detailsButton = itemView.findViewById(R.id.btnDetails);
            shareButton = itemView.findViewById(R.id.btnShare);
            saveButton = itemView.findViewById(R.id.btnSave);
        }
    }
}