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
import androidx.fragment.app.FragmentTransaction;

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

            File pdfFile = getLocalPdfFile(pdfUrl);

            if (pdfFile != null) {

                loadPdf(pdfView, pdfFile);
            } else {

                new DownloadAndDisplayPdfTask(pdfView, getContext(), pdfUrl).execute();
            }


            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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

                    }
                })
                .load();
    }

    private File getLocalPdfFile(String pdfUrl) {

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


    private void downloadPdf(String pdfUrl) {
        DownloadManager downloadManager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(pdfUrl);

        String[] splitUrl = pdfUrl.split("/");
        String pdfTitle = splitUrl[splitUrl.length - 1];

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        String fileName = pdfTitle;
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        downloadManager.enqueue(request);
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