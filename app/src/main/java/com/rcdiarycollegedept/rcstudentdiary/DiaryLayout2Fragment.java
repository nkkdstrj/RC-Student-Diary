package com.rcdiarycollegedept.rcstudentdiary;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class DiaryLayout2Fragment extends Fragment {
    public static final String Arg_PDFLINK = "pdflink";

    // Variable to store the current URL
    private String currentPdfUrl = null;

    public static DiaryLayout2Fragment newInstance(String pdfUrl) {
        DiaryLayout2Fragment fragment = new DiaryLayout2Fragment();
        Bundle args = new Bundle();
        args.putString(Arg_PDFLINK, pdfUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary_layout2, container, false);
        PDFView pdfView = rootView.findViewById(R.id.pdfView);
        Button downloadButton = rootView.findViewById(R.id.dlbtn);

        if (getArguments() != null) {
            String pdfUrl = getArguments().getString(Arg_PDFLINK);

            // Check if the PDF is already downloaded
            File pdfFile = getLocalPdfFile(pdfUrl);

            if (pdfFile != null) {
                // PDF is already downloaded, load and display it
                loadPdf(pdfView, pdfFile);
            } else {
                // PDF is not downloaded, initiate the download
                new DownloadAndDisplayPdfTask(pdfView, getContext(), pdfUrl).execute();
            }

            // Set an onClickListener for the download button
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Initiate the PDF download
                    downloadPdf(pdfUrl);
                }
            });
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
        File pdfFile = new File(requireContext().getFilesDir(), filename);
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

    // Add a method to initiate the PDF download using DownloadManager
    private void downloadPdf(String pdfUrl) {
        DownloadManager downloadManager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(pdfUrl);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        String fileName = "Contract"; // Provide the desired file name
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // Enqueue the download request
        downloadManager.enqueue(request);
    }
}
