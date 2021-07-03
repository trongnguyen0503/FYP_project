package com.tntstudios.feedyourpig.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tntstudios.feedyourpig.R;
import com.tntstudios.feedyourpig.gameplay.GameInterface;

import java.util.List;

public class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.DataViewHolder> {
    private Context context;
    private List<String> list_box;
    private GameInterface gameInterface;
    private int[] boxbg_id ={R.drawable.ironmazeicon,R.drawable.woodboxicon,R.drawable.iceboxicon};
    public BoxAdapter(Context context, List<String> list_box) {
        this.context = context;
        this.list_box = list_box;
        gameInterface = (GameInterface)context;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rv_list_box, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, final int position) {
        holder.txt_name.setText(list_box.get(position));
        Glide.with(context)
                .load(boxbg_id[position])
                .into(holder.img);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameInterface.ChooseBox(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_box.size();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder{
        private TextView txt_name;
        private ImageView img;
        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.rv_list_box_name);
            img = itemView.findViewById(R.id.rv_list_box_image);
        }
    }
}
