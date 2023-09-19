    package com.rcdiarycollegedept.rcstudentdiary;

    import android.content.Intent;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import java.util.List;

    public class DiaryNestedAdapter extends RecyclerView.Adapter<DiaryNestedAdapter.NestedViewHolder> {

        private List<DiaryDataModel> mList;

        public DiaryNestedAdapter(List<DiaryDataModel> mList){
            this.mList = mList;
        }

        @NonNull
        @Override
        public NestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_sub_btn , parent , false);
            return new NestedViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NestedViewHolder holder, int position) {
            DiaryDataModel model = mList.get(position);
            holder.mTv.setText(model.getItemText());

            holder.mTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int layoutValue = model.getLayout();
                    Intent intent;

                    switch (layoutValue) {
                        case 1:
                            intent = new Intent(v.getContext(), DiaryLayout1.class);
                            break;
                        case 2:
                            intent = new Intent(v.getContext(), DiaryLayout2.class);
                            break;
                        // Add cases for other layout values as needed
                        default:
                            // Handle default case or error
                            return;
                    }

                    intent.putExtra("content", model.getContent());
                    intent.putExtra("audio", model.getAudio());
                    intent.putExtra("picture", model.getPicture());

                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class NestedViewHolder extends RecyclerView.ViewHolder{
            private TextView mTv;

            public NestedViewHolder(@NonNull View itemView) {
                super(itemView);
                mTv = itemView.findViewById(R.id.buttonn);
            }
        }
    }
