package com.rcdiarycollegedept.rcstudentdiary;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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
import androidx.fragment.app.FragmentTransaction;

import com.github.barteksc.pdfviewer.PDFView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiaryLayout1Fragment extends Fragment {
    public static final String Arg_PDFLINK = "pdflink";
    public static final String Arg_AUDIO = "audio";
    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();
    private TextView playerPosition;
    private TextView playerDuration;

    public static DiaryLayout1Fragment newInstance(String pdfUrl, String audioUrl) {
        DiaryLayout1Fragment fragment = new DiaryLayout1Fragment();
        Bundle args = new Bundle();
        args.putString(Arg_PDFLINK, pdfUrl);
        args.putString(Arg_AUDIO, audioUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary_layout1, container, false);
        PDFView pdfView = rootView.findViewById(R.id.pdfView);


        mediaPlayer = new MediaPlayer();
        ImageView playButton = rootView.findViewById(R.id.bt_play);
        ImageView pauseButton = rootView.findViewById(R.id.bt_pause);
        SeekBar seekBar = rootView.findViewById(R.id.seek_bar);
        playerPosition = rootView.findViewById(R.id.player_position);
        playerDuration = rootView.findViewById(R.id.player_duration);

        if (getArguments() != null) {
            String pdfUrl = getArguments().getString(Arg_PDFLINK);


            File pdfFile = getLocalPdfFile(pdfUrl);

            if (pdfFile != null) {

                loadPdf(pdfView, pdfFile);
            } else {

                new DownloadAndDisplayPdfTask(pdfView, getContext(), pdfUrl).execute();
            }

            String audioUrl = getArguments().getString(Arg_AUDIO);
            setupAudioPlayer(playButton, pauseButton, seekBar, audioUrl);
        }

        return rootView;
    }

    private void setupAudioPlayer(ImageView playButton, ImageView pauseButton, SeekBar seekBar, String audioUrl) {
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();


            int duration = mediaPlayer.getDuration();
            seekBar.setMax(duration);
            playerDuration.setText(formatTime(duration));
        } catch (IOException e) {
            e.printStackTrace();
        }

        playButton.setOnClickListener(v -> {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
            }
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
            mediaPlayer.seekTo(0);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                playerPosition.setText(formatTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        final Runnable updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    playerPosition.setText(formatTime(currentPosition));
                    handler.postDelayed(this, 100);
                }
            }
        };

        handler.postDelayed(updateSeekBar, 0);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            handler.removeCallbacksAndMessages(null);
        }
    }
    private String formatTime(int ms) {
        int seconds = (ms / 1000) % 60;
        int minutes = (ms / 1000) / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void loadPdf(PDFView pdfView, File pdfFile) {
        pdfView.fromFile(pdfFile)
                .defaultPage(0)
                .onLoad(nbPages -> {

                })
                .load();
    }

    private File getLocalPdfFile(String pdfUrl) {

        String filename = String.valueOf(pdfUrl.hashCode()) + ".pdf";
        File pdfFile = new File(getContext().getFilesDir(), filename);
        if (pdfFile.exists()) {
            return pdfFile;
        }
        return null;
    }

    private class DownloadAndDisplayPdfTask extends AsyncTask<Void, Void, File> {
        private PDFView pdfView;
        private Context context;
        private String pdfUrl;

        public DownloadAndDisplayPdfTask(PDFView pdfView, Context context, String pdfUrl) {
            this.pdfView = pdfView;
            this.context = context;
            this.pdfUrl = pdfUrl;
        }

        @Override
        protected File doInBackground(Void... voids) {
            try {

                File pdfFile = downloadFile(pdfUrl);

                if (pdfFile != null) {
                    return pdfFile;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(File pdfFile) {
            if (pdfFile != null) {

                loadPdf(pdfView, pdfFile);
            }
        }

        private File downloadFile(String pdfUrl) throws IOException {
            URL url = new URL(pdfUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            String filename = (pdfUrl.hashCode()) + ".pdf";
            File pdfFile = new File(context.getFilesDir(), filename);

            try (InputStream input = connection.getInputStream(); FileOutputStream output = new FileOutputStream(pdfFile)) {
                byte[] buffer = new byte[4 * 1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }

            return pdfFile;
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setOnBackPressedListener(() -> {

                if (isVisible()) {

                    getActivity().getSupportFragmentManager().popBackStack();


                    replaceDiaryFragment();
                }
            });
        }
    }

    private void replaceDiaryFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new DiaryFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}