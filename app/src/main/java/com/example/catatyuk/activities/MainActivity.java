package com.example.catatyuk.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.catatyuk.R;
import com.example.catatyuk.adapters.NotesAdapter;
import com.example.catatyuk.database.NotesDatabase;
import com.example.catatyuk.entities.Note;
import com.example.catatyuk.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {

    private static final int REQUEST_CODE_ADD_NOTE = 1;

    private static final int REQUEST_CODE_UPDATE_NOTE = 2;

    private static final int REQUEST_CODE_SHOW_NOTE = 3;

    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;

    private int noteClickedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView addNoteMain = findViewById(R.id.addNoteMain);


        addNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Menjalankan CreateNoteActivity untuk menambah catatan baru
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
         new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );


        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        notesRecyclerView.setAdapter(notesAdapter);


        // Memuat daftar catatan saat aktivitas dibuka
        getNotes(REQUEST_CODE_SHOW_NOTE, false);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Mencari catatan berdasarkan input pengguna saat teks berubah
                if(noteList.size() != 0){
                    notesAdapter.searchCatatan(editable.toString());
                }
            }
        });
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }

    // Metode untuk mendapatkan daftar catatan dari database berdasarkan permintaan
    private void getNotes(final int requestCode, final boolean isNoteDeleted){
        @SuppressLint("StaticFieldLeak")
                class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {
            @Override
            protected List<Note> doInBackground(Void... voids){
                // Mendapatkan daftar catatan dari database secara asinkron
                return NotesDatabase
                        .getDataBase(getApplicationContext())
                        .noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes){
                super.onPostExecute(notes);
                // Menangani hasil permintaan berdasarkan kode permintaan
               if (requestCode == REQUEST_CODE_SHOW_NOTE){
                   // Menampilkan catatan ke daftar saat aktivitas pertama kali dibuka
                   noteList.addAll(notes);
                   notesAdapter.notifyDataSetChanged();
               } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                   // Menambahkan catatan baru ke daftar saat catatan ditambahkan
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
               } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                   // Memperbarui catatan dalam daftar saat catatan diperbarui
                   noteList.remove(noteClickedPosition);
                   if (isNoteDeleted){
                       // Menghapus catatan dari daftar jika catatan dihapus
                       notesAdapter.notifyItemRemoved(noteClickedPosition);
                   } else{
                       // Menyisipkan kembali catatan ke daftar jika catatan diperbarui
                       noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                       notesAdapter.notifyItemChanged(noteClickedPosition);
                   }
               }
            }
        }
        // Menjalankan AsyncTask untuk mendapatkan catatan dari database
        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            // Mendapatkan daftar catatan saat catatan baru ditambahkan
            getNotes(REQUEST_CODE_ADD_NOTE, false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            if(data != null){
                // Mendapatkan daftar catatan saat catatan diperbarui atau dihapus
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
            }
        }
    }
}