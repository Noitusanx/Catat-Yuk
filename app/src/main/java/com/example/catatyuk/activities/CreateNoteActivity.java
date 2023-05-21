package com.example.catatyuk.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.catatyuk.R;
import com.example.catatyuk.database.NotesDatabase;
import com.example.catatyuk.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CreateNoteActivity extends AppCompatActivity {
    private EditText inputJudulCatatan, inputTextCatatan;
    private TextView tanggal;

    private String pilihWarnaCatatan;

    private Note catatanSudahTersedia;

    private AlertDialog pesanHapusCatatan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener((v)->{
            onBackPressed();
        });

        // Inisialisasi elemen-elemen tampilan
        inputJudulCatatan = findViewById(R.id.inputJudulCatatan);
        inputTextCatatan = findViewById(R.id.inputTextCatatan);
        tanggal = findViewById(R.id.tanggal);

        tanggal.setText(
                new SimpleDateFormat("EEEE, dd MMMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener((v)-> {
            saveNote();
        });

        pilihWarnaCatatan = "#EEEEEE";

        // Cek apakah aktivitas ini digunakan untuk melihat atau memperbarui catatan
        if(getIntent().getBooleanExtra("isViewOrUpdate", false)){
            catatanSudahTersedia = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }


        initPilihWarna();
    }

    // Mengisi tampilan dengan data catatan yang sudah ada untuk dilihat atau diperbarui
    private void setViewOrUpdateNote(){
        inputJudulCatatan.setText(catatanSudahTersedia.getJudul());
        inputTextCatatan.setText(catatanSudahTersedia.getCatatanTeks());
        tanggal.setText(catatanSudahTersedia.getTanggal());


    }

    // Menyimpan catatan baru atau memperbarui catatan yang sudah ada
    private void saveNote() {
        // Validasi input judul catatan
        if (inputJudulCatatan.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Judul Catatan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Validasi input judul dan isi catatan
        else if (inputJudulCatatan.getText().toString().trim().isEmpty()
                && inputJudulCatatan.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Catatan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Membuat objek Note baru dan mengisi datanya
        final Note note = new Note();
        note.setJudul(inputJudulCatatan.getText().toString());
        note.setCatatanTeks(inputTextCatatan.getText().toString());
        note.setTanggal(tanggal.getText().toString());
        note.setColor(pilihWarnaCatatan);

        // Jika ada catatan yang sudah ada, atur ID catatan yang akan diperbarui
        if (catatanSudahTersedia!=null){
            note.setId(catatanSudahTersedia.getId());
        }
        // Tugas penyimpanan catatan secara asinkron
        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDataBase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // Mengirimkan hasil kegiatan dan menutup aktivitas
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }

    // Inisialisasi fungsi pemilihan warna catatan
    private void initPilihWarna() {
        final LinearLayout layoutPilihWarna = findViewById(R.id.layoutPilihWarna);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutPilihWarna);
        layoutPilihWarna.findViewById(R.id.textPilihWarna).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        // Mendefinisikan ImageView untuk setiap pilihan warna
        final ImageView imageColor1 = layoutPilihWarna.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutPilihWarna.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutPilihWarna.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutPilihWarna.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutPilihWarna.findViewById(R.id.imageColor5);

        // Memberikan fungsi onClickListener untuk setiap pilihan warna
        layoutPilihWarna.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihWarnaCatatan = "#EEEEEE";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
            }
        });

        layoutPilihWarna.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihWarnaCatatan = "#A8D672";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
            }
        });

        layoutPilihWarna.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihWarnaCatatan = "#98B7DB";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
            }
        });

        layoutPilihWarna.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihWarnaCatatan = "#EB7A53";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
            }
        });

        layoutPilihWarna.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihWarnaCatatan = "#F7D44C";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
            }
        });

        // Mengecek warna yang sudah dipilih sebelumnya dan memberikan tanda centang pada pilihannya
        if (catatanSudahTersedia!=null && catatanSudahTersedia.getColor()!=null && !catatanSudahTersedia.getColor().trim().isEmpty()){
            switch (catatanSudahTersedia.getColor()){
                case "#A8D672":
                    layoutPilihWarna.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#98B7DB":
                    layoutPilihWarna.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#EB7A53":
                    layoutPilihWarna.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#F7D44C":
                    layoutPilihWarna.findViewById(R.id.viewColor5).performClick();
                    break;
            }
        }
        if (catatanSudahTersedia!=null){
            layoutPilihWarna.findViewById(R.id.layoutHapusCatatan).setVisibility(View.VISIBLE);
            layoutPilihWarna.findViewById(R.id.layoutHapusCatatan).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showPesanHapusCatatan();
                }
            });
        }
        }

    // Menampilkan dialog konfirmasi penghapusan catatan
    private void showPesanHapusCatatan(){
        if (pesanHapusCatatan==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_hapus_catatan,
                    (ViewGroup) findViewById(R.id.layoutHapusCatatanContainer)
            );
            builder.setView(view);
            pesanHapusCatatan = builder.create();
            if (pesanHapusCatatan.getWindow()!=null){
                pesanHapusCatatan.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textHapusCatatan).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void>{
                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotesDatabase.getDataBase(getApplicationContext()).noteDao().deleteNote(catatanSudahTersedia);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            Intent intent = new Intent();
                            intent.putExtra("isNoteDeleted", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    new DeleteNoteTask().execute();
                }
            });

            view.findViewById(R.id.textBatal).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pesanHapusCatatan.dismiss();
                }
            });
        }
        pesanHapusCatatan.show();
    }
}