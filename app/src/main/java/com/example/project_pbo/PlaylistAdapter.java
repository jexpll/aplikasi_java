package com.example.project_pbo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {
    public PlaylistAdapter(Context context, List<Playlist> playlists) {
        super(context, 0, playlists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Playlist p = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_playlist, parent, false);
        }

        TextView tvTitle = convertView.findViewById(R.id.tvPlaylistTitle);
        TextView tvSub = convertView.findViewById(R.id.tvPlaylistSubtitle);

        if (p != null) {
            tvTitle.setText(p.getNamaPlaylist());
            tvSub.setText("Playlist • " + p.getDaftarLagu().size() + " lagu");
        }

        return convertView;
    }
}
