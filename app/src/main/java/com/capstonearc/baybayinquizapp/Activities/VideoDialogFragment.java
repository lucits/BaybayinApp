package com.capstonearc.baybayinquizapp.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.capstonearc.baybayinquizapp.R;

public class VideoDialogFragment extends DialogFragment {

    private VideoView videoView;
    private ImageView closeDialog;
    private Uri videoUri;
    private View drawingView;


    public static VideoDialogFragment newInstance(Uri videoUri) {
        VideoDialogFragment fragment = new VideoDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("video_uri", videoUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoUri = getArguments().getParcelable("video_uri");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.video_dialog, null);
        videoView = view.findViewById(R.id.videoView);
        closeDialog = view.findViewById(R.id.closeDialog);
        videoView.setVideoURI(videoUri);

        drawingView = view.findViewById(R.id.drawing_view);

        ImageView clearButton = view.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawingView) drawingView).clearCanvas();
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                videoView.start();
            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });




        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setCancelable(true)
                .create();
    }
}