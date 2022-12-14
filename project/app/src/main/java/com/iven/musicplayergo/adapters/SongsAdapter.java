package com.iven.musicplayergo.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.iven.musicplayergo.R;
import com.iven.musicplayergo.Utils;
import com.iven.musicplayergo.models.Album;
import com.iven.musicplayergo.models.Song;

import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SimpleViewHolder> implements Filterable {

    private final SongSelectedListener mSongSelectedListener;
    private final Activity mActivity;
    private List<Song> mSongs;
    private List<Song> mFullSongs;
    private Album mAlbum;

    public SongsAdapter(@NonNull Activity activity, Album album) {
        mActivity = activity;
        mAlbum = album;
        mSongs = mAlbum.songs;
        mFullSongs = new ArrayList<>(mSongs);
        mSongSelectedListener = (SongSelectedListener) activity;
    }

    public void swapSongs(Album album) {
        mAlbum = album;
        mSongs = mAlbum.songs;
        mFullSongs = new ArrayList<>(mSongs);
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false);

        return new SimpleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {

        Song song = mSongs.get(holder.getAdapterPosition());
        String songTitle = song.title;

        int songTrack = Song.formatTrack(song.trackNumber);
        Spanned spanned = Utils.buildSpanned(mActivity.getString(R.string.track_title, songTrack, songTitle));
        holder.trackTitle.setText(spanned);
        holder.duration.setText(Song.formatDuration(song.duration));
    }

    @Override
    public int getItemCount() {

        return mSongs.size();
    }

    @Override
    public Filter getFilter() {
        return songFilter;
    }

    public interface SongSelectedListener {
        void onSongSelected(Song song, Album album);
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView trackTitle, duration;

        SimpleViewHolder(View itemView) {
            super(itemView);

            trackTitle = itemView.findViewById(R.id.track_title);
            duration = itemView.findViewById(R.id.duration);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Song song = mSongs.get(getAdapterPosition());
            mSongSelectedListener.onSongSelected(song, mAlbum);
        }
    }

    private Filter songFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Song> filterList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filterList.addAll(mFullSongs);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Song song : mFullSongs) {
                    if (song.title.toLowerCase().contains(filterPattern)) {
                        filterList.add(song);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mSongs.clear();
            mSongs.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public void updateSongs() {
        mSongs.clear();
        mSongs.addAll(mFullSongs);
    }
}