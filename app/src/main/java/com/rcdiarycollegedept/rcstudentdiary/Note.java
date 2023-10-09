package com.rcdiarycollegedept.rcstudentdiary;

public class Note {
    private String noteId;
    private String noteTitle;
    private String noteContent;

    // Required default constructor for Firebase
    public Note() {
    }

    public Note(String noteId, String noteTitle, String noteContent) {
        this.noteId = noteId;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }
}

