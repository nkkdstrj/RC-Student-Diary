package com.rcdiarycollegedept.rcstudentdiary;

import androidx.fragment.app.Fragment;
import java.util.List;
import java.util.Map;

public class DiaryDataModelFragment {
    private List<DiaryDataModelFragment> fragmentList; // Store a list of fragments
    private String itemText;
    private String pdfFilePath;
    private boolean isExpandable;
    private Fragment fragment; // Change the type to Fragment
    private String audio;
    private String content;
    private int layout;
    private String picture;
    private Map<String, Map<String, String>> tables;
    // Constructor for main buttons with sub-buttons
    public DiaryDataModelFragment(String itemText, List<DiaryDataModelFragment> fragmentList) {
        this.itemText = itemText;
        this.fragmentList = fragmentList;
        this.isExpandable = false;


    }

    // Constructor for sub-buttons
    public DiaryDataModelFragment(String itemText, String audio, String content, int layout, String picture) {
        this.itemText = itemText;
        this.audio = audio;
        this.content = content;
        this.layout = layout;
        this.picture = picture;
        this.isExpandable = false;

    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public List<DiaryDataModelFragment> getFragmentList() {
        return fragmentList;
    }

    public String getItemText() {
        return itemText;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public String getAudio() {
        return audio;
    }

    public String getContent() {
        return content;
    }

    public int getLayout() {
        return layout;
    }

    public String getPicture() {
        return picture;
    }

    public Map<String, Map<String, String>> getTables() {
        return tables;
    }
    public String getPdfFilePath() {
        return pdfFilePath;
    }public void setPdfFilePath(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }
    // Setter for tables
    public void setTables(Map<String, Map<String, String>> tables) {
        this.tables = tables;
    }
    }


