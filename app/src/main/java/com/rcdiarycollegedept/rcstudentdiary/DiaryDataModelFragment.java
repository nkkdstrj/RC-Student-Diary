package com.rcdiarycollegedept.rcstudentdiary;

import androidx.fragment.app.Fragment;
import java.util.List;
import java.util.Map;

public class DiaryDataModelFragment {
    private List<DiaryDataModelFragment> sub_btn; // Store a list of fragments
    private String main_btn;
    private String pdfFilePath;
    private boolean isExpanding = false;
    private boolean isExpandable;
    private Fragment fragment; // Change the type to Fragment
    private String audio;
    private String content;
    private int layout;
    private String pdflink;
    private Map<String, Map<String, String>> tables;
    // Constructor for main buttons with sub-buttons
    public DiaryDataModelFragment(String main_btn, List<DiaryDataModelFragment> sub_btn) {
        this.main_btn = main_btn;
        this.sub_btn = sub_btn;
        this.isExpandable = false;


    }

    // Constructor for sub-buttons
    public DiaryDataModelFragment(String main_btn, String audio, String content, int layout, String pdflink) {
        this.main_btn = main_btn;
        this.audio = audio;
        this.content = content;
        this.layout = layout;
        this.pdflink = pdflink;
        this.isExpandable = false;

    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }
    public void setExpanding(boolean expanding) {
        isExpanding = expanding;
    }

    public List<DiaryDataModelFragment> getSub_btn() {
        return sub_btn;
    }

    public String getMain_btn() {
        return main_btn;
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
    public boolean isExpanding() {
        return isExpanding;
    }
    public String getPdflink() {
        return pdflink;
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