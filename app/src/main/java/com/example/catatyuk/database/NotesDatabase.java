package com.example.catatyuk.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.catatyuk.dao.NoteDao;
import com.example.catatyuk.entities.Note;

@Database(entities = Note.class, version = 1, exportSchema = false)
//membuat dan mengakses database
public abstract class NotesDatabase extends RoomDatabase {
    private static NotesDatabase notesDatabase;

    public static synchronized NotesDatabase getDataBase(Context context){
        if(notesDatabase == null){
            notesDatabase = Room.databaseBuilder(
                    context, NotesDatabase.class,
                    "notes_db"
            ).build();
        }
        return notesDatabase;
    }
//    Mengakses metode yang didefinisikan dalam NoteDao interface
    public abstract NoteDao noteDao();
}

