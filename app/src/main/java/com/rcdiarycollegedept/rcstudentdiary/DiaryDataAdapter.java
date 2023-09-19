package com.rcdiarycollegedept.rcstudentdiary;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class DiaryDataAdapter extends RecyclerView.Adapter<DiaryDataAdapter.ItemViewHolder> {

    private List<DiaryDataModel> mList;
    private List<String> list = new ArrayList<>();

    public DiaryDataAdapter(List<DiaryDataModel> mList){
        this.mList  = mList;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_main_btn , parent , false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        DiaryDataModel model = mList.get(position);
        holder.mTextView.setText(model.getItemText());

        boolean isExpandable = model.isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

        if (isExpandable) {
            holder.mArrowImage.setImageResource(R.drawable.arrow_up);
        } else {
            holder.mArrowImage.setImageResource(R.drawable.arrow_down);
        }

        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setExpandable(!model.isExpandable());
                notifyItemChanged(position);
            }
        });

        // Initialize the nested RecyclerView with sub-buttons
        DiaryNestedAdapter adapter = new DiaryNestedAdapter(model.getNestedList());
        holder.nestedRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.nestedRecyclerView.setHasFixedSize(true);
        holder.nestedRecyclerView.setAdapter(adapter);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout linearLayout;
        private RelativeLayout expandableLayout;
        private TextView mTextView;
        private ImageView mArrowImage;
        private RecyclerView nestedRecyclerView;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            mTextView = itemView.findViewById(R.id.itemTv);
            mArrowImage = itemView.findViewById(R.id.arro_imageview);
            nestedRecyclerView = itemView.findViewById(R.id.child_rv);
        }
    }
}