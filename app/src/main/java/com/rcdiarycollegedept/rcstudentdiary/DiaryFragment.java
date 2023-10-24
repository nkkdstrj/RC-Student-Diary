package com.rcdiarycollegedept.rcstudentdiary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rcdiarycollegedept.rcstudentdiary.databinding.FragmentDiaryBinding;

import java.util.ArrayList;
import java.util.List;

public class DiaryFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<DiaryDataModelFragment> mList;
    private DiaryDataAdapterFragment adapter;
    private DiaryNestedSearchAdapterFragment searchResultsAdapter;
    private FragmentDiaryBinding binding;
    private DatabaseReference mDatabase;

    private List<DiaryDataModelFragment> matchingSubButtons = new ArrayList<>();

    private RecyclerView searchResultsRecyclerView;

    private ValueEventListener dataListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDiaryBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        recyclerView = rootView.findViewById(R.id.main_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mList = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        adapter = new DiaryDataAdapterFragment(mList, getParentFragmentManager()); // Use getParentFragmentManager()
        recyclerView.setAdapter(adapter);

        recyclerView.setAdapter(adapter);

        // Initialize the search functionality
        searchResultsRecyclerView = rootView.findViewById(R.id.search_results_recyclerview);
        searchResultsRecyclerView.setHasFixedSize(true);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsAdapter = new DiaryNestedSearchAdapterFragment(new ArrayList<>(), getContext());
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);

        SearchView searchView = rootView.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    searchResultsRecyclerView.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    performSearch(newText);
                }
                return true;
            }
        });

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    if (dataListener == null) {
                        addDataListener();
                    }
                } else {
                    if (dataListener != null) {
                        mDatabase.child("diarycontent_btn").removeEventListener(dataListener);
                        dataListener = null;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", "Error checking network connectivity: " + error.getMessage());
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadDiaryData();
    }

    private void loadDiaryData() {
        mDatabase.child("diarycontent_btn").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                for (DataSnapshot buttonSnapshot : dataSnapshot.getChildren()) {
                    String buttonName = buttonSnapshot.child("buttonname").getValue(String.class);
                    List<DiaryDataModelFragment> subButtonList = new ArrayList<>();
                    for (DataSnapshot subButtonSnapshot : buttonSnapshot.child("btn_sub_btns").getChildren()) {
                        String subButtonName = subButtonSnapshot.child("sub_btn_name").getValue(String.class);
                        String subButtonAudio = subButtonSnapshot.child("audio").getValue(String.class);
                        String subButtonContent = subButtonSnapshot.child("content").getValue(String.class);
                        String subButtonPicture = subButtonSnapshot.child("picture").getValue(String.class);

                        // Initialize subButtonLayout with a default value (e.g., -1)
                        int subButtonLayout = -1;

                        // Check if the layout data is available and a valid integer
                        if (subButtonSnapshot.hasChild("layout")) {
                            try {
                                subButtonLayout = subButtonSnapshot.child("layout").getValue(Integer.class);
                            } catch (Exception e) {
                                // Handle the exception (e.g., invalid data format)
                                subButtonLayout = -1; // Assign a default value or handle the error appropriately
                            }
                        }

                        subButtonList.add(new DiaryDataModelFragment(subButtonName, subButtonAudio, subButtonContent, subButtonLayout, subButtonPicture));
                    }
                    mList.add(new DiaryDataModelFragment(buttonName, subButtonList));
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching data from Firebase: " + databaseError.getMessage());
            }
        });
    }

    private void addDataListener() {
        dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                for (DataSnapshot buttonSnapshot : dataSnapshot.getChildren()) {
                    String buttonName = buttonSnapshot.child("buttonname").getValue(String.class);
                    List<DiaryDataModelFragment> subButtonList = new ArrayList<>();
                    for (DataSnapshot subButtonSnapshot : buttonSnapshot.child("btn_sub_btns").getChildren()) {
                        String subButtonName = subButtonSnapshot.child("sub_btn_name").getValue(String.class);
                        String subButtonAudio = subButtonSnapshot.child("audio").getValue(String.class);
                        String subButtonContent = subButtonSnapshot.child("content").getValue(String.class);
                        String subButtonPicture = subButtonSnapshot.child("picture").getValue(String.class);

                        // Initialize subButtonLayout with a default value (e.g., -1)
                        int subButtonLayout = -1;

                        // Check if the layout data is available and a valid integer
                        if (subButtonSnapshot.hasChild("layout")) {
                            try {
                                subButtonLayout = subButtonSnapshot.child("layout").getValue(Integer.class);
                            } catch (Exception e) {
                                // Handle the exception (e.g., invalid data format)
                                subButtonLayout = -1; // Assign a default value or handle the error appropriately
                            }
                        }

                        subButtonList.add(new DiaryDataModelFragment(subButtonName, subButtonAudio, subButtonContent, subButtonLayout, subButtonPicture));
                    }
                    mList.add(new DiaryDataModelFragment(buttonName, subButtonList));
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching data from Firebase: " + databaseError.getMessage());
            }
        };

        mDatabase.child("diarycontent_btn").addValueEventListener(dataListener);
    }

    private void performSearch(String query) {
        DatabaseReference diaryContentRef = mDatabase.child("diarycontent_btn");

        diaryContentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                matchingSubButtons.clear();
                for (DataSnapshot buttonSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot subButtonSnapshot : buttonSnapshot.child("btn_sub_btns").getChildren()) {
                        String subButtonAudio = subButtonSnapshot.child("audio").getValue(String.class);
                        if (subButtonAudio != null && subButtonAudio.toLowerCase().contains(query.toLowerCase())) {
                            String subButtonName = subButtonSnapshot.child("sub_btn_name").getValue(String.class);
                            String subButtonContent = subButtonSnapshot.child("content").getValue(String.class);
                            int subButtonLayout = subButtonSnapshot.child("layout").getValue(Integer.class);
                            String subButtonPicture = subButtonSnapshot.child("picture").getValue(String.class);
                            matchingSubButtons.add(new DiaryDataModelFragment(subButtonName, subButtonAudio, subButtonContent, subButtonLayout, subButtonPicture));
                        }
                    }
                }
                searchResultsAdapter.updateDataset(matchingSubButtons);
                if (matchingSubButtons.isEmpty()) {
                    searchResultsRecyclerView.setVisibility(View.GONE);
                } else {
                    searchResultsRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching data from Firebase: " + databaseError.getMessage());
            }
        });
    }


}
