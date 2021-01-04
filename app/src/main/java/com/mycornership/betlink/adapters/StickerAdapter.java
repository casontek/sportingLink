package com.mycornership.betlink.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mycornership.betlink.R;
import com.mycornership.betlink.models.BirthdaySticker;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.SHolder> {
    private List<BirthdaySticker> stickers;
    private Context context;

    public StickerAdapter(Context c, List<BirthdaySticker> s) {
        this.stickers = s;
        this.context = c;
    }

    @NonNull
    @Override
    public SHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sticker_inflator, parent, false);
        return new SHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SHolder holder, int position) {
        BirthdaySticker sticker = stickers.get(position);
        //display the liked or loved person
        if(sticker.getSticker().equals("love"))
            holder.label.setBackground(context.getDrawable(R.drawable.fb_love1));
        else
            holder.label.setBackground(context.getDrawable(R.drawable.fb_like1));
        //display the persons picture
        Glide.with(context).load(sticker.getUserPhoto()).into(holder.pix);
    }

    @Override
    public int getItemCount() {
        return stickers.size();
    }

    public class SHolder extends RecyclerView.ViewHolder {
        CircleImageView pix;
        View label;

        public SHolder(@NonNull View itemView) {
            super(itemView);
            pix = itemView.findViewById(R.id.user_pix);
            label = itemView.findViewById(R.id.label);
        }

    }
}
