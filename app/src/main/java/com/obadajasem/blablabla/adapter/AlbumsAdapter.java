package com.obadajasem.blablabla.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.obadajasem.blablabla.FavouritStationActivity;
import com.obadajasem.blablabla.MainActivity;
import com.obadajasem.blablabla.R;
import com.obadajasem.blablabla.model.Album;
import com.obadajasem.blablabla.model.Station;

import java.util.List;


public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder>{

    public static final String FAVOURIT = "favourit";
    private Context mContext;
    private List<Station> stationList;
    private   OnNoteListener mOnNoteListener;

    public AlbumsAdapter(Context mContext, List<Station> stationList,  OnNoteListener onNoteListener) {
        this.mContext = mContext;
        this.stationList = stationList;
        this.mOnNoteListener=onNoteListener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title, count;
        private ImageView thumbnail, overflow;
        OnNoteListener mOnNoteListener;

        public MyViewHolder(View view, final OnNoteListener onNoteListener) {
            super(view);
            title =  view.findViewById(R.id.title);
            count =  view.findViewById(R.id.count);
            thumbnail =  view.findViewById(R.id.thumbnail);
            overflow = view.findViewById(R.id.overflow);
            this.mOnNoteListener=onNoteListener;
            overflow.setOnClickListener(this);
            thumbnail.setOnClickListener(this);
//            playiv.setOnClickListener(new View.OnClickListener() {
//                @SuppressLint("ResourceType")
//                @Override
//                public void onClick(View view) {
//                    onNoteListener.onPlayClick(getAdapterPosition());
//                    playiv.setBackgroundResource(R.drawable.ic_pause);
//                }
//            });

        }


        @Override
        public void onClick(View view) {
            mOnNoteListener.onNoteClick(getAdapterPosition());
//            mOnNoteListener.onPlayClick(getAdapterPosition());
            mOnNoteListener.onDotsClick(getAdapterPosition());

        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView,mOnNoteListener);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Station currentstation = stationList.get(position);
        holder.title.setText(currentstation.getName());
        holder.count.setText(currentstation.getState());

        //init the placeholder
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.placeholder);

        // loading album cover using Glide library
        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(currentstation.getFavicon())
                .into(holder.thumbnail);
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu

        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:

                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return stationList.size();
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
        void onDotsClick(int position);
//        void onPlayClick(int position);

    }

}
