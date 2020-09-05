package id.sam.omdb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import id.sam.omdb2.model.TitleMovie;
import id.sam.omdb2.service.APIClient;
import id.sam.omdb2.service.APIInterfacesRest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahData extends AppCompatActivity {

    TextView txtTitle, txtDirectedBy, txtWritenBy, txtStudio;
    Spinner spnRating, spnGenre;
    CalendarView calTheater;
    ImageView imgPoster;
    Button btnSend;
    ProgressBar progressBarTambahData;
    String tanggal = "";

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
        progressBarTambahData = findViewById(R.id.progressBarTambahData);
        progressBarTambahData.setVisibility(View.GONE);
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

        calTheater.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
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
                    String image = titleMovie.getPoster();
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
}