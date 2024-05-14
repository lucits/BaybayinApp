package com.capstonearc.baybayinquizapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.capstonearc.baybayinquizapp.Users;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {
    private List<Users> users;

    public LeaderboardAdapter(List<Users> users) {
        this.users = users;
    }

    @Override
    public void onBindViewHolder(LeaderboardViewHolder holder, int position) {
        Users user = users.get(position);
        holder.rankTextView.setText(String.valueOf(position + 1));
        holder.usernameTextView.setText(user.getUsername());
        holder.scoreTextView.setText(String.valueOf(user.getBattleModeScore()));
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);

        return new LeaderboardViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        public TextView rankTextView;
        public TextView usernameTextView;
        public TextView scoreTextView;

        public LeaderboardViewHolder(View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rank_text_view);
            usernameTextView = itemView.findViewById(R.id.username_text_view);
            scoreTextView = itemView.findViewById(R.id.score_text_view);
        }
    }
}