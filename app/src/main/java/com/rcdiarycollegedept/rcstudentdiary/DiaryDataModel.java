// Modify DiaryDataModel.java
package com.rcdiarycollegedept.rcstudentdiary;

import java.util.List;

public class DiaryDataModel {
    private List<DiaryDataModel> nestedList; // Change the type to DiaryDataModel
    private String itemText;
    private boolean isExpandable;
    private String audio;
    private String content;
    private int layout;
    private String picture;

    // Constructor for main buttons with sub-buttons
    public DiaryDataModel(String itemText, List<DiaryDataModel> nestedList) {
        this.itemText = itemText;
        this.nestedList = nestedList;
        this.isExpandable = false;
    }

    // Constructor for sub-buttons
    public DiaryDataModel(String itemText, String audio, String content, int layout, String picture) {
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

    public List<DiaryDataModel> getNestedList() {
        return nestedList;
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
}
