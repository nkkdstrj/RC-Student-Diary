package com.rcdiarycollegedept.rcstudentdiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DiaryNestedSearchAdapterFragment extends RecyclerView.Adapter<DiaryNestedSearchAdapterFragment.NestedViewHolder> {

    private List<DiaryDataModelFragment> searchResults;
    private Context context;

    public DiaryNestedSearchAdapterFragment(List<DiaryDataModelFragment> searchResults, Context context) {
        this.searchResults = searchResults;
        this.context = context;
    }

    @NonNull
    @Override
    public NestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for search results (fragment_diary_nested_adapter.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_diary_nested_adapter, parent, false);
        return new NestedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NestedViewHolder holder, int position) {
        DiaryDataModelFragment model = searchResults.get(position);

        // Bind data for search result using the same layout
        holder.mTv.setText(model.getItemText());

        holder.mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int layoutValue = model.getLayout();
                switch (layoutValue) {
                    case 3:
                        // Replace with DiaryLayout1Fragment
                        Fragment diaryLayout3Fragment = DiaryLayout3Fragment.newInstance(
                                model.getContent()

                        );
                        // Redirect to the specific layout when clicked
                        replaceFragment(diaryLayout3Fragment);
                        break;
                    case 1:
                        // Replace with DiaryLayout1Fragment
                        Fragment diaryLayout1Fragment = DiaryLayout1Fragment.newInstance(
                                model.getContent(),
                                model.getAudio(),
                                model.getPicture()

                        );
                        // Redirect to the specific layout when clicked
                        replaceFragment(diaryLayout1Fragment);
                        break;
                    case 2:
                        // Replace with DiaryLayout2Fragment
                        Fragment diaryLayout2Fragment = DiaryLayout2Fragment.newInstance(
                                model.getItemText()
                        );
                        // Redirect to the specific layout when clicked
                        replaceFragment(diaryLayout2Fragment);
                        break;
                    // Add cases for other layout values as needed
                    default:
                        // Handle default case or error
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public class NestedViewHolder extends RecyclerView.ViewHolder {
        private TextView mTv;

        public NestedViewHolder(@NonNull View itemView) {
            super(itemView);
            mTv = itemView.findViewById(R.id.buttonn);
        }
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, newFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    // Add this method to update the dataset with search results
    public void updateDataset(List<DiaryDataModelFragment> updatedList) {
        searchResults.clear();
        searchResults.addAll(updatedList);
        notifyDataSetChanged();
    }
}
