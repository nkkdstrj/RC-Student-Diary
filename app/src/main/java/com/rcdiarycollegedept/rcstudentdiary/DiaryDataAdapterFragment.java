package com.rcdiarycollegedept.rcstudentdiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DiaryDataAdapterFragment extends RecyclerView.Adapter<DiaryDataAdapterFragment.ItemViewHolder> {

    private List<DiaryDataModelFragment> mList;

    public DiaryDataAdapterFragment(List<DiaryDataModelFragment> mList) {
        this.mList = mList;
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

    // Add this method to update the dataset with search results
    public void updateDataset(List<DiaryDataModelFragment> updatedList) {
        mList.clear();
        mList.addAll(updatedList);
        notifyDataSetChanged();
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
            boolean isExpandable = model.isExpandable();
            nestedRecyclerView.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model.setExpandable(!model.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            DiaryNestedAdapterFragment adapter = new DiaryNestedAdapterFragment(model.getFragmentList(), itemView.getContext());
            nestedRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            nestedRecyclerView.setHasFixedSize(true);
            nestedRecyclerView.setAdapter(adapter);
        }
    }
}
