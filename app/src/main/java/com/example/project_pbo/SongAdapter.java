package com.example.project_pbo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class SongAdapter extends ArrayAdapter<Lagu> {
    public interface OnMoreClickListener {
        void onMoreClick(Lagu song);
    }

    private final OnMoreClickListener moreListener;

    public SongAdapter(Context context, List<Lagu> songs, OnMoreClickListener moreListener) {
        super(context, 0, songs);
        this.moreListener = moreListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Lagu song = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_song, parent, false);
        }

        ImageView ivCover = convertView.findViewById(R.id.ivItemCover);
        TextView tvTitle = convertView.findViewById(R.id.tvItemTitle);
        TextView tvArtist = convertView.findViewById(R.id.tvItemArtist);
        TextView btnMore = convertView.findViewById(R.id.btnItemMore);

        if (song != null) {
            tvTitle.setText(song.getJudul());
            String artistInfo = (song.getNamaArtis() != null ? song.getNamaArtis() : "Unknown")
                    + " • " + song.getDurasi();
            tvArtist.setText(artistInfo);

            if (song.getCoverUrl() != null && !song.getCoverUrl().isEmpty()) {
                ImageLoader.load(song.getCoverUrl(), ivCover);
            } else {
                ivCover.setImageResource(android.R.drawable.ic_media_play);
            }

            btnMore.setOnClickListener(v -> {
                if (moreListener != null) moreListener.onMoreClick(song);
            });
        }

        return convertView;
    }
}

