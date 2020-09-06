package id.sam.omdb2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import id.sam.omdb2.model.Movie;
import id.sam.omdb2.model.TitleMovie;
import id.sam.omdb2.service.APIClient;
import id.sam.omdb2.service.APIInterfacesRest;
import id.sam.omdb2.testing.UploadImg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahData extends AppCompatActivity {

    TextView txtTitle, txtDirectedBy, txtWritenBy, txtStudio;
    Spinner spnRating, spnGenre;
    CalendarView calTheater;
    ImageView imgPoster;
    Button btnSend, btnChoose;
    ProgressBar progressBarTambahData;
    String tanggal = "";
    String image = "";
    private AppDatabase mDb;
    private DatabaseReference mDatabase;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data);
        getSupportActionBar().setTitle("Tambah Data");
        txtTitle = findViewById(R.id.txtTitle);
        spnRating = findViewById(R.id.spnRating);
        spnGenre = findViewById(R.id.spnGenre);
        txtDirectedBy = findViewById(R.id.txtDirectedBy);
        txtWritenBy = findViewById(R.id.txtWritenBy);
        calTheater = findViewById(R.id.calTheater);
        txtStudio = findViewById(R.id.txtStudio);
        imgPoster = findViewById(R.id.imgPoster);
        btnSend = findViewById(R.id.btnSend);
        btnChoose = findViewById(R.id.btnChoose);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("img/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        progressBarTambahData = findViewById(R.id.progressBarTambahData);
        progressBarTambahData.setVisibility(View.GONE);
        mDb = AppDatabase.getInstance(getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        txtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                callTitleMovie(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        tanggal = ft.format(dNow);

        calTheater.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date date = new Date(year-1900, month, dayOfMonth);
                tanggal = sdf.format(date);
                Toast.makeText(TambahData.this, tanggal, Toast.LENGTH_SHORT).show();
            }
        });
    }

    APIInterfacesRest apiInterface;
    public void callTitleMovie(String kota){
        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        progressBarTambahData.setVisibility(View.VISIBLE);
        Call<TitleMovie> call3 = apiInterface.getTitleMovie(kota,"33e0eb02");
        call3.enqueue(new Callback<TitleMovie>() {
            @Override
            public void onResponse(Call<TitleMovie> call, Response<TitleMovie> response) {
                TitleMovie titleMovie = response.body();
                if (titleMovie !=null) {
                    final ArrayList<String>ratingList = new ArrayList<String>();
                    ratingList.clear();
                    if (titleMovie.getResponse().equals("False")) {
                        ratingList.add("");
                    } else {
                        for (int i = 0; i < titleMovie.getRatings().size(); i++) {
                            ratingList.add(titleMovie.getRatings().get(i).getValue() + " : " + titleMovie.getRatings().get(i).getSource());
                        }
                    }
                    ArrayAdapter<String> arrayRating = new ArrayAdapter<String>(TambahData.this, android.R.layout.simple_list_item_1, ratingList);
                    arrayRating.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnRating.setAdapter(arrayRating);
                    String genre = (titleMovie.getResponse().equals("False")) ? "" : titleMovie.getGenre();
                    List<String> genreList = Arrays.asList(genre.split("\\s*,\\s*"));
//                    if (titleMovie.getResponse().equals("False")) {
//                        ratingList.add("");
//                    }
                    ArrayAdapter<String> arrayGenre = new ArrayAdapter<String>(TambahData.this, android.R.layout.simple_list_item_1, genreList);
                    arrayGenre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnGenre.setAdapter(arrayGenre);
                    txtDirectedBy.setText(titleMovie.getDirector());
                    txtWritenBy.setText(titleMovie.getWriter());
                    image = titleMovie.getPoster();
                    Picasso.get().load(image).into(imgPoster);
                    progressBarTambahData.setVisibility(View.GONE);
                } else{

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(TambahData.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(TambahData.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TitleMovie> call, Throwable t) {
                progressBarTambahData.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Maaf koneksi bermasalah",Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });
    }

    public boolean checkMandatory(){
        boolean pass = true;
        if (TextUtils.isEmpty(txtTitle.getText().toString())){
            pass = false;
            txtTitle.setError("Masukkan Judul, mandatory");
        }

        return pass;
    }

    public void send(View view){
        if (checkMandatory()){

            mDatabase.child("movie").child(UUID.randomUUID().toString()).setValue(generateObjectData());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Movie movie = null;
                    movie = mDb.movieDAO().findByTitle(txtTitle.getText().toString());

                    if (movie != null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showErrorDialogDifferentContent();
                            }
                        });
                    } else {
                        mDb.movieDAO().insertAll(generateObjectData());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDialogInfo();
                            }
                        });
                    }
                }
            }).start();
        } else {
            showErrorDialog();
        }
    }

    private void uploadImage(){
        btnSend.setEnabled(false);
        final StorageReference ref = storageReference.child("img/"+ UUID.randomUUID().toString());
        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                image = uri.toString();
                            }
                        });
                        Toast.makeText(TambahData.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        btnSend.setEnabled(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TambahData.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        btnSend.setEnabled(true);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgPoster.setImageBitmap(bitmap);
                uploadImage();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void showErrorDialogDifferentContent(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(TambahData.this);
        alertDialog.setTitle("Peringatan");
        alertDialog.setMessage("Mohon masukkan Judul yang berbeda")
                .setIcon(R.drawable.ic_close)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(TambahData.this, "Cancel ditekan", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public Movie generateObjectData(){
        Movie movie = new Movie();
        movie.setTitle(txtTitle.getText().toString());
        movie.setRating(spnRating.getSelectedItem().toString());
        movie.setGenre(spnGenre.getSelectedItem().toString());
        movie.setDirectedBy(txtDirectedBy.getText().toString());
        movie.setWrittenBy(txtWritenBy.getText().toString());
        movie.setInTheater(tanggal);
        movie.setStudio(txtStudio.getText().toString());
        movie.setImgPoster(image);
        return movie;
    }

    public void showDialogInfo(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(TambahData.this);
        alertDialog.setTitle("Tambah Data");
        alertDialog.setMessage("Berhasil tambah data")
                .setIcon(R.drawable.ic_done_24)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void showErrorDialog(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(TambahData.this);
        alertDialog.setTitle("Peringatan");
        alertDialog.setMessage("Mohon isi field yang mandatory")
                .setIcon(R.drawable.ic_close)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(TambahData.this, "Cancel ditekan", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }
}