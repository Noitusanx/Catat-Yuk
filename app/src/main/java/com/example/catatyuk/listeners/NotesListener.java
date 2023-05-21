package com.example.catatyuk.listeners;

import com.example.catatyuk.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
