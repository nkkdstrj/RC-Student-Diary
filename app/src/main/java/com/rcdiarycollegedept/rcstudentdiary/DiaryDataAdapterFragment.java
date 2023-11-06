package com.rcdiarycollegedept.rcstudentdiary;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
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
        private static final long COOLDOWN_DELAY = 300;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.itemTv);
            nestedRecyclerView = itemView.findViewById(R.id.child_rv);
        }

        public void bind(DiaryDataModelFragment model) {
            mTextView.setText(model.getMain_btn());
            List<DiaryDataModelFragment> sub_btn = model.getSub_btn();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (model.isExpanding()) {
                        // If it's already expanding, ignore the click
                        return;
                    }

                    // Set the flag to indicate that it's expanding
                    model.setExpanding(true);

                    if (sub_btn.size() == 1) {
                        // If there's only one sub-item, navigate directly to its content
                        DiaryDataModelFragment subItem = sub_btn.get(0);
                        int layoutValue = subItem.getLayout();
                        switch (layoutValue) {
                            case 3:
                                // Replace with DiaryLayout3Fragment
                                // Redirect to the specific layout when clicked
                                replaceFragment(DiaryLayout3Fragment.newInstance(subItem.getPdflink()));
                                break;
                            case 1:
                                // Replace with DiaryLayout1Fragment
                                // Redirect to the specific layout when clicked
                                replaceFragment(DiaryLayout1Fragment.newInstance(
                                        subItem.getAudio(),
                                        subItem.getPdflink()
                                ));
                                break;
                            case 2:
                                // Replace with DiaryLayout2Fragment
                                // Redirect to the specific layout when clicked
                                replaceFragment(DiaryLayout2Fragment.newInstance(subItem.getPdflink()));
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

                    // Add a delay or cooldown period
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Reset the flag after the cooldown period
                            model.setExpanding(false);
                        }
                    }, COOLDOWN_DELAY);
                }
            });

            // If there are multiple sub-items, handle the expandable behavior as before
            boolean isExpandable = model.isExpandable();
            nestedRecyclerView.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

            DiaryNestedAdapterFragment adapter = new DiaryNestedAdapterFragment(sub_btn, itemView.getContext());
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
