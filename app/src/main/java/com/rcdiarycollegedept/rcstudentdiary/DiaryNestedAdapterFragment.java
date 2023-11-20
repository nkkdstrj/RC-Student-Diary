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

public class DiaryNestedAdapterFragment extends RecyclerView.Adapter<DiaryNestedAdapterFragment.NestedViewHolder> {

    private List<DiaryDataModelFragment> dataList;
    private Context context;

    public DiaryNestedAdapterFragment(List<DiaryDataModelFragment> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public NestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_diary_nested_adapter, parent, false);
        return new NestedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NestedViewHolder holder, int position) {
        DiaryDataModelFragment model = dataList.get(position);
        holder.mTv.setText(model.getMain_btn());

        holder.mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int layoutValue = model.getLayout();
                switch (layoutValue) {
                    case 3:
                        Fragment diaryLayout3Fragment = DiaryLayout3Fragment.newInstance(
                                model.getPdflink()

                        );
                        replaceFragment(diaryLayout3Fragment);
                        break;
                    case 1:
                        Fragment diaryLayout1Fragment = DiaryLayout1Fragment.newInstance(
                                model.getPdflink(),
                                model.getAudio()
                        );
                        replaceFragment(diaryLayout1Fragment);
                        break;
                    case 2:
                        Fragment diaryLayout2Fragment = DiaryLayout2Fragment.newInstance(
                                model.getPdflink()
                        );
                        replaceFragment(diaryLayout2Fragment);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
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
}
