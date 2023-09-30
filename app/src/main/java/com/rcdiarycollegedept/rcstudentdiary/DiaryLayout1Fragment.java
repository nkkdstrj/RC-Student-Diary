package com.rcdiarycollegedept.rcstudentdiary;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DiaryLayout1Fragment extends Fragment {
    public static final String ARG_CONTENT = "content";
    public static final String ARG_AUDIO = "audio";

    private String content;
    private String audio ="https://firebasestorage.googleapis.com/v0/b/rc-student-diary-5cad7.appspot.com/o/silang.mp3?alt=media&token=15c2a6bc-9f69-4fd4-86e0-ab686ae9e4ea";

    private TextView contentTextView;
    private TextView playerPosition;
    private TextView playerDuration;
    private SeekBar seekbar;
    private ImageView btplay;
    private ImageView btpause;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable runnable;
    private boolean isPrepared = false;
    private boolean isPlaying = false;

    public static DiaryLayout1Fragment newInstance(String content, String audio, String picture) {
        DiaryLayout1Fragment fragment = new DiaryLayout1Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, content);
        args.putString(ARG_AUDIO, audio);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary_layout1, container, false);


        contentTextView = rootView.findViewById(R.id.textViewLyrics);
        playerPosition = rootView.findViewById(R.id.player_position);
        playerDuration = rootView.findViewById(R.id.player_duration);
        seekbar = rootView.findViewById(R.id.seek_bar);
        btplay = rootView.findViewById(R.id.bt_play);
        btpause = rootView.findViewById(R.id.bt_pause);

        if (getArguments() != null) {
            content = getArguments().getString(ARG_CONTENT);
            audio = getArguments().getString(ARG_AUDIO);
            contentTextView.setText(content);
        }

        mediaPlayer = new MediaPlayer();
        handler = new Handler();

        try {
            mediaPlayer.setDataSource(audio);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Audio is prepared, you can update the UI as needed
                    int duration = mediaPlayer.getDuration();
                    String sDuration = convertFormat(duration);
                    playerDuration.setText(sDuration);
                    seekbar.setMax(mediaPlayer.getDuration());
                    isPrepared = true;
                }
            });
            mediaPlayer.prepareAsync(); // Start preparing the audio immediately
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Set click listeners for play and pause buttons
        btplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePlayback();
            }
        });

        btpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePlayback();
            }
        });

        // Set seek bar change listener

        return rootView;
    }

    private void togglePlayback() {
        if (isPrepared) {
            if (isPlaying) {
                // Pause the audio
                btpause.setVisibility(View.GONE);
                btplay.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                handler.removeCallbacks(runnable);
                isPlaying = false;
            } else {
                // Start playing the audio
                btplay.setVisibility(View.GONE);
                btpause.setVisibility(View.VISIBLE);
                mediaPlayer.start();
                seekbar.setMax(mediaPlayer.getDuration());
                handler.postDelayed(runnable, 0);
                isPlaying = true;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAudioPlayback();
    }

    private void stopAudioPlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(runnable);
    }


    private String convertFormat(int duration) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }
}
