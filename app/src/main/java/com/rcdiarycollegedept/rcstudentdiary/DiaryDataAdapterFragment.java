package com.rcdiarycollegedept.rcstudentdiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class DiaryDataAdapterFragment extends RecyclerView.Adapter<DiaryDataAdapterFragment.ItemViewHolder> {

    private List<DiaryDataModelFragment> mList;
    private FragmentManager fragmentManager; // Add this field

    public DiaryDataAdapterFragment(List<DiaryDataModelFragment> mList, FragmentManager fragmentManager) {
        this.mList = mList;
        this.fragmentManager = fragmentManager; // Initialize the fragmentManager
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_diary_data_adapter, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        DiaryDataModelFragment model = mList.get(position);
        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;
        private RecyclerView nestedRecyclerView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.itemTv);
            nestedRecyclerView = itemView.findViewById(R.id.child_rv);
        }

        public void bind(DiaryDataModelFragment model) {
            mTextView.setText(model.getItemText());
            List<DiaryDataModelFragment> fragmentList = model.getFragmentList();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragmentList.size() == 1) {
                        // If there's only one sub-item, navigate directly to its content
                        DiaryDataModelFragment subItem = fragmentList.get(0);
                        int layoutValue = subItem.getLayout();
                        switch (layoutValue) {
                            case 3:
                                // Replace with DiaryLayout3Fragment
                                Fragment diaryLayout3Fragment = DiaryLayout3Fragment.newInstance(subItem.getContent());
                                // Redirect to the specific layout when clicked
                                replaceFragment(diaryLayout3Fragment);
                                break;
                            case 1:
                                // Replace with DiaryLayout1Fragment
                                Fragment diaryLayout1Fragment = DiaryLayout1Fragment.newInstance(
                                        subItem.getContent(),
                                        subItem.getAudio(),
                                        subItem.getPicture()
                                );
                                // Redirect to the specific layout when clicked
                                replaceFragment(diaryLayout1Fragment);
                                break;
                            case 2:
                                // Replace with DiaryLayout2Fragment
                                Fragment diaryLayout2Fragment = DiaryLayout2Fragment.newInstance(subItem.getItemText());
                                // Redirect to the specific layout when clicked
                                replaceFragment(diaryLayout2Fragment);
                                break;
                            // Add cases for other layout values as needed
                            default:
                                // Handle default case or error
                                break;
                        }
                    } else {
                        // Handle the case when there are multiple sub-items
                        model.setExpandable(!model.isExpandable());
                        notifyItemChanged(getAdapterPosition());
                    }
                }
            });

            // If there are multiple sub-items, handle the expandable behavior as before
            boolean isExpandable = model.isExpandable();
            nestedRecyclerView.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

            DiaryNestedAdapterFragment adapter = new DiaryNestedAdapterFragment(fragmentList, itemView.getContext());
            nestedRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            nestedRecyclerView.setHasFixedSize(true);
            nestedRecyclerView.setAdapter(adapter);
        }


        private void replaceFragment(Fragment newFragment) {

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, newFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}