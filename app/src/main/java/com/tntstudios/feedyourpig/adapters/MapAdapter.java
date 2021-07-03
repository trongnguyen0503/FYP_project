package com.tntstudios.feedyourpig.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tntstudios.feedyourpig.R;
import com.tntstudios.feedyourpig.gameplay.GameInterface;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.DataViewHolder> {
    private Context context;
    private GameInterface gameInterface;
    private int[] list_star_map;
    public MapAdapter(Context context,int[] list_star_map) {
        this.gameInterface = (GameInterface)context;
        this.context=context;
        this.list_star_map=list_star_map;
    }

    @NonNull
    @Override
    public MapAdapter.DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rv_list_map, parent, false);
        return new MapAdapter.DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapAdapter.DataViewHolder holder, final int position) {
        holder.txt_map.setText(String.valueOf(position+1));
        Glide.with(context)
                .load(R.drawable.boxicon)
                .into(holder.btn_map);
        switch (list_star_map[position]){
            case -1:{
                Glide.with(context)
                        .load(R.drawable.lock)
                        .into(holder.star);
                break;
            }
            case 0:{
                Glide.with(context)
                        .load(R.drawable.star_point_0)
                        .into(holder.star);
                break;
            }
            case 1:{
                Glide.with(context)
                        .load(R.drawable.star_point_1)
                        .into(holder.star);
                break;
            }
            case 2:{
                Glide.with(context)
                        .load(R.drawable.star_point_2)
                        .into(holder.star);
                break;
            }
            case 3:{
                Glide.with(context)
                        .load(R.drawable.star_point_3)
                        .into(holder.star);
                break;
            }
        }
        holder.btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameInterface.ChooseMap(position);
            }
        });
    }
    @Override
    public int getItemCount() {
        return 25;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder{
        private ImageView btn_map,star;
        private TextView txt_map;
        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            btn_map = itemView.findViewById(R.id.rv_list_btn_map);
            txt_map = itemView.findViewById(R.id.rv_list_txt_map);
            star = itemView.findViewById(R.id.rv_list_img_star);
        }
    }
}
