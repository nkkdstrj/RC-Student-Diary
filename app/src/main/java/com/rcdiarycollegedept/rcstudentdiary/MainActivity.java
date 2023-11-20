package com.rcdiarycollegedept.rcstudentdiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.content.Context;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rcdiarycollegedept.rcstudentdiary.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private FirebaseDatabase mDatabase;
    private DiaryDataAdapterFragment adapter;
    private OnBackPressedListener onBackPressedListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        mDatabase = FirebaseDatabase.getInstance();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.note:
                    replaceFragment(new NotesFragment());
                    break;
                case R.id.calendar:
                    replaceFragment(new CalendarFragment());
                    break;
                case R.id.handbook:
                    replaceFragment(new DiaryFragment());
                    break;
                case R.id.setting:
                    replaceFragment(new SettingsFragment());
                    break;
            }
            return true;
        });

        downloadAllPDFs();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.handbook:

                Log.d("MenuClicked", "Handbook menu item selected");


                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);


                Log.d("BackStackCleared", "BackStack cleared");


                replaceFragment(new DiaryFragment());


                Log.d("DiaryFragmentAdded", "DiaryFragment added");

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Clear the back stack before adding the new fragment
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null); // Add the transaction to the back stack
        fragmentTransaction.commit();
    }


    public interface OnBackPressedListener {
        void onBackPressed();
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        this.onBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null) {

            onBackPressedListener.onBackPressed();
        } else {

            super.onBackPressed();
        }
    }
    private void downloadAllPDFs() {
        mDatabase.getReference("diarycontent_btn").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    List<DiaryDataModelFragment> mList = new ArrayList<>();
                    for (DataSnapshot buttonSnapshot : dataSnapshot.getChildren()) {
                        String buttonName = buttonSnapshot.child("buttonname").getValue(String.class);
                        List<DiaryDataModelFragment> subButtonList = new ArrayList<>();
                        for (DataSnapshot subButtonSnapshot : buttonSnapshot.child("btn_sub_btns").getChildren()) {
                            String subButtonName = subButtonSnapshot.child("sub_btn_name").getValue(String.class);
                            String subButtonAudio = subButtonSnapshot.child("audio").getValue(String.class);
                            String subButtonContent = subButtonSnapshot.child("content").getValue(String.class);
                            Integer subButtonLayout = subButtonSnapshot.child("layout").getValue(Integer.class); // Use Integer instead of int
                            String subButtonPdflink = subButtonSnapshot.child("pdflink").getValue(String.class);

                            // Check for null values before using them
                            if (subButtonName != null && subButtonAudio != null && subButtonContent != null && subButtonLayout != null && subButtonPdflink != null) {
                                downloadAndStorePDF(getApplicationContext(), mList, subButtonPdflink);
                                subButtonList.add(new DiaryDataModelFragment(subButtonName, subButtonAudio, subButtonContent, subButtonLayout, subButtonPdflink));
                            }
                        }
                        mList.add(new DiaryDataModelFragment(buttonName, subButtonList));
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching data from Firebase: " + databaseError.getMessage());
            }
        });
    }

    private void downloadAndStorePDF(Context context, List<DiaryDataModelFragment> mList, String pdfUrl) {
        String filename = String.valueOf(pdfUrl.hashCode()) + ".pdf";
        File pdfFile = new File(context.getFilesDir(), filename);

        if (!pdfFile.exists()) {
            new DownloadAndStorePdfTask(pdfFile, pdfUrl, mList).execute();
        }
    }

    private class DownloadAndStorePdfTask {
        private File pdfFile;
        private String pdfUrl;
        private List<DiaryDataModelFragment> mList;

        public DownloadAndStorePdfTask(File pdfFile, String pdfUrl, List<DiaryDataModelFragment> mList) {
            this.pdfFile = pdfFile;
            this.pdfUrl = pdfUrl;
            this.mList = mList;
        }

        public void execute() {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<Void> future = executor.submit(() -> {

                return null;
            });



            executor.shutdown();
        }


    }
}