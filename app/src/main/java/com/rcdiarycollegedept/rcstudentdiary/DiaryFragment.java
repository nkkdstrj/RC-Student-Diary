package com.rcdiarycollegedept.rcstudentdiary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import com.rcdiarycollegedept.rcstudentdiary.databinding.FragmentDiaryBinding;

public class DiaryFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<DiaryDataModelFragment> mList;
    private DiaryDataAdapterFragment adapter;
    private DiaryNestedSearchAdapterFragment searchResultsAdapter;
    private FragmentDiaryBinding binding;
    private DatabaseReference mDatabase;

    private List<DiaryDataModelFragment> matchingSubButtons = new ArrayList<>();

    private RecyclerView searchResultsRecyclerView; // Added to hold search results RecyclerView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDiaryBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        recyclerView = rootView.findViewById(R.id.main_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mList = new ArrayList<>();

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Fetch data from Firebase
        fetchDataFromFirebase();

        // Initialize the adapter and set it to the RecyclerView
        adapter = new DiaryDataAdapterFragment(mList);
        recyclerView.setAdapter(adapter);

        // Initialize search results RecyclerView
        searchResultsRecyclerView = rootView.findViewById(R.id.search_results_recyclerview); // Use the correct ID
        searchResultsRecyclerView.setHasFixedSize(true);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsAdapter = new DiaryNestedSearchAdapterFragment(new ArrayList<>(), getContext());
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);

        // Implement search functionality
        SearchView searchView = rootView.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Handle search when the user submits the query (optional).
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // If the search text is empty, show the main RecyclerView (fragment list)
                    recyclerView.setVisibility(View.VISIBLE);
                    searchResultsRecyclerView.setVisibility(View.GONE);
                } else {
                    // If there is text in the search, hide the main RecyclerView
                    recyclerView.setVisibility(View.GONE);
                    // Handle search as the user types.
                    performSearch(newText);
                }
                return true;
            }
        });

        return rootView;
    }

    private void fetchDataFromFirebase() {
        DatabaseReference diaryContentRef = mDatabase.child("diarycontent_btn");

        diaryContentRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        int subButtonLayout = subButtonSnapshot.child("layout").getValue(Integer.class);
                        String subButtonPicture = subButtonSnapshot.child("picture").getValue(String.class);

                        subButtonList.add(new DiaryDataModelFragment(subButtonName, subButtonAudio, subButtonContent, subButtonLayout, subButtonPicture));
                    }

                    mList.add(new DiaryDataModelFragment(buttonName, subButtonList));
                }

                // Notify the adapter that the dataset has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching data from Firebase: " + databaseError.getMessage());
            }
        });
    }

    private void performSearch(String query) {
        DatabaseReference diaryContentRef = mDatabase.child("diarycontent_btn");

        diaryContentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                matchingSubButtons.clear(); // Clear the previous search results

                for (DataSnapshot buttonSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot subButtonSnapshot : buttonSnapshot.child("btn_sub_btns").getChildren()) {
                        String subButtonContent = subButtonSnapshot.child("content").getValue(String.class);

                        if (subButtonContent != null && subButtonContent.toLowerCase().contains(query.toLowerCase())) {
                            String subButtonName = subButtonSnapshot.child("sub_btn_name").getValue(String.class);
                            String subButtonAudio = subButtonSnapshot.child("audio").getValue(String.class);
                            int subButtonLayout = subButtonSnapshot.child("layout").getValue(Integer.class);
                            String subButtonPicture = subButtonSnapshot.child("picture").getValue(String.class);
                            matchingSubButtons.add(new DiaryDataModelFragment(subButtonName, subButtonAudio, subButtonContent, subButtonLayout, subButtonPicture));
                        }
                    }
                }

                // Update the search results adapter's dataset with search results
                searchResultsAdapter.updateDataset(matchingSubButtons);

                // Show/hide the search results RecyclerView based on whether there are results
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
