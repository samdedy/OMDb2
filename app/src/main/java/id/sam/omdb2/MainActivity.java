package id.sam.omdb2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.sam.omdb2.adapter.AdapterListMovie;
import id.sam.omdb2.model.Movie;

public class MainActivity extends AppCompatActivity implements AdapterListMovie.OnItemClickListener{

    private AppDatabase mDb;
    RecyclerView rvListMovie;
    private AdapterListMovie adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDb = AppDatabase.getInstance(getApplicationContext());
        rvListMovie = findViewById(R.id.rvListMovie);
        rvListMovie.setHasFixedSize(true);
        rvListMovie.setLayoutManager(new LinearLayoutManager(this));
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadDatabase();
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                startActivity(new Intent(MainActivity.this,TambahData.class));
                return true;
            default:
                return true;
        }
    }

    public void loadDatabase(){
        List<Movie> movieList = null;
        movieList = mDb.movieDAO().getAll();
        adapter = new AdapterListMovie(MainActivity.this, movieList);
        adapter.setOnItemClickListener(MainActivity.this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvListMovie.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                rvListMovie.setItemAnimator(new DefaultItemAnimator());
                rvListMovie.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onItemClick(View view, Movie obj, int position) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadDatabase();
            }
        }).start();
    }
}