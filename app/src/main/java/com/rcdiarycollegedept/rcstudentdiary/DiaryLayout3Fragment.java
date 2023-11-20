package com.rcdiarycollegedept.rcstudentdiary;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class DiaryLayout3Fragment extends Fragment {
    public static final String Arg_PDFLINK = "pdflink";

    public static DiaryLayout3Fragment newInstance(String pdfUrl) {
        DiaryLayout3Fragment fragment = new DiaryLayout3Fragment();
        Bundle args = new Bundle();
        args.putString(Arg_PDFLINK, pdfUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary_layout3, container, false);
        PDFView pdfView = rootView.findViewById(R.id.pdfView);

        if (getArguments() != null) {
            String pdfUrl = getArguments().getString(Arg_PDFLINK);


            File pdfFile = getLocalPdfFile(pdfUrl);

            if (pdfFile != null) {

                loadPdf(pdfView, pdfFile);
            } else {

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

        String filename = String.valueOf(pdfUrl.hashCode()) + ".pdf";
        File pdfFile = new File(getContext().getFilesDir(), filename);
        if (pdfFile.exists()) {
            return pdfFile;
        }
        return null;
    }

    private class DownloadAndDisplayPdfTask extends android.os.AsyncTask<Void, Void, File> {
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


            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

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
        transaction.addToBackStack(null); // Add to the back stack
        transaction.commit();
    }
}
