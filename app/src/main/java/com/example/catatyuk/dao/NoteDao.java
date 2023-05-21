package com.example.catatyuk.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.catatyuk.entities.Note;

import java.util.List;

@Dao
public interface NoteDao {

//    Mendapatkan semua catatan dari database dan mengembalikannya dalam urutan terbalik berdasarkan ID.
    @Query("Select * FROM notes ORDER BY id DESC")
    List<Note> getAllNotes();

//    Menyisipkan catatan baru ke dalam database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

//    Menghapus catatan dari database.
    @Delete
    void deleteNote(Note note);
}
