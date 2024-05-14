package com.capstonearc.baybayinquizapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.capstonearc.baybayinquizapp.Domain.LessonsDomain;
import com.capstonearc.baybayinquizapp.R;

import java.util.ArrayList;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.ViewHolder> {
    ArrayList<LessonsDomain> items;
    Context context;
    OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int id);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public LessonsAdapter(ArrayList<LessonsDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public LessonsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        View inflator = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_lessons,parent, false);

        return new ViewHolder(inflator);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonsAdapter.ViewHolder holder, int position) {

        holder.title.setText(items.get(position).getTitle());
        holder.subtitle.setText(items.get(position).getSubtitle());

        int drawableResourceId = holder.itemView.getResources().getIdentifier(items.get(position).getPicture(), "drawable", holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(30, 30,0,0))
                .into(holder.picture);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!= null) {
                    listener.onItemClick(items.get(position).getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, subtitle;
        ImageView picture;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.titleTxt);
            subtitle = itemView.findViewById(R.id.subtitleText);
            picture = itemView.findViewById(R.id.picture);
        }
    }
}