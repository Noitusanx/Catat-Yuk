package com.example.catatyuk.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catatyuk.R;
import com.example.catatyuk.entities.Note;
import com.example.catatyuk.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>{
    private List<Note> notes;
    private NotesListener notesListener;

    private Timer timer;
    private List<Note> notesSource;

    public NotesAdapter(List<Note> notes, NotesListener notesListener) {
        // Membuat tampilan ViewHolder untuk setiap item catatan
        this.notes = notes;
        this.notesListener = notesListener;
        notesSource = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        // Mengikat data catatan ke tampilan ViewHolder
        holder.setNote(notes.get(position));

        // Menetapkan OnClickListener untuk meneruskan aksi klik pada catatan
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesListener.onNoteClicked(notes.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Mengembalikan jumlah item dalam daftar catatan
        return notes.size();
    }

    @Override
    public int getItemViewType(int position){
        // Mengembalikan jenis tampilan untuk posisi tertentu (posisi itu sendiri)
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle, textSubtitle, tanggal;
        LinearLayout layoutNote;

        NoteViewHolder(@NonNull View itemView){
            super(itemView);
            // Menginisialisasi tampilan yang ada dalam item catatan
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            tanggal = itemView.findViewById(R.id.tanggal);
            layoutNote = itemView.findViewById(R.id.layoutNote);
        }

        void setNote(Note note){
            // Mengatur nilai teks dan tampilan pada tampilan ViewHolder berdasarkan objek Note
            textTitle.setText(note.getJudul());
            if(note.getCatatanTeks().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            } else{
                textSubtitle.setText(note.getCatatanTeks());
            }
            tanggal.setText(note.getTanggal());

            // Mengatur warna latar belakang layoutNote berdasarkan warna catatan
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if(note.getColor()!=null){
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            } else {
                gradientDrawable.setColor(Color.parseColor("#EEEEEE"));
            }
        }
    }

    public void searchCatatan(final String searchKeyword){
        // Metode untuk melakukan pencarian catatan berdasarkan kata kunci
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()){
                    // Jika kata kunci pencarian kosong, menampilkan semua catatan
                    notes = notesSource;
                } else {
                    // Jika kata kunci pencarian tidak kosong, mencari catatan berdasarkan kata kunci
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note: notesSource) {
                        if(note.getJudul().toLowerCase().contains(searchKeyword.toLowerCase())||
                        note.getCatatanTeks().toLowerCase().contains(searchKeyword.toLowerCase())){
                            temp.add(note);
                        }
                    }
                    notes = temp;
                }
                // Memperbarui tampilan pada UI utama setelah pencarian selesai
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500); // Delay 500 milidetik sebelum menjalankan pencarian (menghindari pencarian berulang saat user mengetik dengan cepat)
    }

    public void cancelTimer(){
        // Metode untuk membatalkan timer pencarian
        if (timer!=null){
            timer.cancel();
        }
    }
}
