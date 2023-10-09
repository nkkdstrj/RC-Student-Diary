package com.rcdiarycollegedept.rcstudentdiary;

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DiaryLayout2Fragment extends Fragment {
    private TableLayout tableLayout;
    private String subBtnName;

    public static DiaryLayout2Fragment newInstance(String subBtnName) {
        DiaryLayout2Fragment fragment = new DiaryLayout2Fragment();
        fragment.subBtnName = subBtnName;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary_layout2, container, false);
        tableLayout = rootView.findViewById(R.id.tableLayout);

        setTableBackground();
        fetchTableDataFromFirebase();

        return rootView;
    }

    private void setTableBackground() {
        int borderDrawableId = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) ?
                R.drawable.border : R.drawable.border;

        tableLayout.setBackground(ContextCompat.getDrawable(getContext(), borderDrawableId));
    }

    private void fetchTableDataFromFirebase() {
        DatabaseReference diaryContentRef = FirebaseDatabase.getInstance().getReference()
                .child("diarycontent_btn");
        diaryContentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot buttonSnapshot : dataSnapshot.getChildren()) {
                    if (buttonSnapshot.hasChild("btn_sub_btns")) {
                        DataSnapshot subBtnsSnapshot = buttonSnapshot.child("btn_sub_btns");
                        for (DataSnapshot subBtnSnapshot : subBtnsSnapshot.getChildren()) {
                            if (subBtnSnapshot.hasChild("tables")) {
                                String currentSubBtnName = subBtnSnapshot.child("sub_btn_name").getValue(String.class);
                                if (currentSubBtnName != null && currentSubBtnName.equals(subBtnName)) {
                                    createTableFromSnapshot(subBtnSnapshot.child("tables").child("table1"));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }

    // Inside your DiaryLayout2Fragment class
    private void createTableFromSnapshot(DataSnapshot tableSnapshot) {
        List<String> columnNames = new ArrayList<>();
        TableRow headerRow = new TableRow(getContext());

        // Apply row border background to the header row
        headerRow.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.table_row_border));

        for (DataSnapshot rowSnapshot : tableSnapshot.getChildren()) {
            Map<String, String> rowData = rowSnapshot.getValue(new GenericTypeIndicator<Map<String, String>>() {});

            if (rowData != null) {
                createHeaderRowIfNeeded(rowData, columnNames, headerRow);
                TableRow row = createTableRow(rowData, columnNames);

                // Apply row border background to the data row
                row.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.table_row_border));

                // Apply column borders to each cell in the row
                applyColumnBordersToRow(row, columnNames.size());

                tableLayout.addView(row);
            }
        }

        if (tableLayout.getChildCount() > 0) {
            tableLayout.addView(headerRow, 0);
        }
    }

    private void applyColumnBordersToRow(TableRow row, int numColumns) {
        for (int i = 0; i < numColumns; i++) {
            TextView cell = (TextView) row.getChildAt(i);

            // Apply column border background to each cell in the row
            cell.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.table_cell_border));
        }
    }



    private void createHeaderRowIfNeeded(Map<String, String> rowData, List<String> columnNames, TableRow headerRow) {
        for (String columnName : rowData.keySet()) {
            if (!columnNames.contains(columnName)) {
                columnNames.add(columnName);
                TextView headerTextView = createHeaderTextView(columnName);
                headerRow.addView(headerTextView);
            }
        }
    }

    private TextView createHeaderTextView(String columnName) {
        TextView headerTextView = new TextView(getContext());
        headerTextView.setText(columnName);
        headerTextView.setGravity(Gravity.CENTER);
        return headerTextView;
    }

    private TableRow createTableRow(Map<String, String> rowData, List<String> columnNames) {
        TableRow row = new TableRow(getContext());

        for (String columnName : columnNames) {
            TextView cellTextView = createCellTextView(rowData.get(columnName));
            row.addView(cellTextView);
        }

        return row;
    }

    private TextView createCellTextView(String text) {
        TextView cellTextView = new TextView(getContext());
        cellTextView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        int paddingInDp = 8;
        float cellScale = getResources().getDisplayMetrics().density;
        int paddingInPx = (int) (paddingInDp * cellScale + 0.5f);
        cellTextView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);
        cellTextView.setText(text);
        return cellTextView;
    }
}
