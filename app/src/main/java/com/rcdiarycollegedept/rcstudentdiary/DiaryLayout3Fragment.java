package com.rcdiarycollegedept.rcstudentdiary;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiaryLayout3Fragment extends Fragment {
    public static final String ARG_CONTENT = "content";

    // Variable to store the current URL
    private String currentPdfUrl = null;

    public static DiaryLayout3Fragment newInstance(String pdfUrl) {
        DiaryLayout3Fragment fragment = new DiaryLayout3Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, pdfUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary_layout3, container, false);
        PDFView pdfView = rootView.findViewById(R.id.pdfView);

        if (getArguments() != null) {
            String pdfUrl = getArguments().getString(ARG_CONTENT);

            // Check if the PDF is already downloaded
            File pdfFile = getLocalPdfFile(pdfUrl);

            if (pdfFile != null) {
                // PDF is already downloaded, load and display it
                loadPdf(pdfView, pdfFile);
            } else {
                // PDF is not downloaded, initiate the download
                new DownloadAndDisplayPdfTask(pdfView, getContext(), pdfUrl).execute();
            }
        }

        return rootView;
    }

    private void loadPdf(PDFView pdfView, File pdfFile) {
        pdfView.fromFile(pdfFile)
                .defaultPage(0)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        // PDF loaded successfully
                    }
                })
                .load();
    }

    private File getLocalPdfFile(String pdfUrl) {
        // Create a unique filename based on the PDF URL
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
                // Download the PDF file from the URL and save it to local storage
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
                // Load and display the downloaded PDF
                loadPdf(pdfView, pdfFile);
            }
        }

        private File downloadFile(String pdfUrl) throws IOException {
            URL url = new URL(pdfUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Create a temporary file to save the PDF
            String filename = String.valueOf(pdfUrl.hashCode()) + ".pdf";
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
}