package id.sam.omdb2.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.squareup.picasso.Picasso;

import java.util.List;

import id.sam.omdb2.AppDatabase;
import id.sam.omdb2.MainActivity;
import id.sam.omdb2.R;
import id.sam.omdb2.model.Movie;

public class AdapterListMovie extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AppDatabase mDb;
    private List<Movie> items;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    String image = "";

    public interface OnItemClickListener {
        void onItemClick(View view, Movie obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListMovie(Context context, List<Movie> items) {
        this.items = items;
        ctx = context;
        mDb = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "movie_db").allowMainThreadQueries().build();
        mOnItemClickListener = (MainActivity)context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgPoster;
        public TextView txtTitle;
        public TextView txtRating;
        public TextView txtGenre;
        public TextView txtDirectedBy;
        public TextView txtTheater;
        public CardView parentLayout;
        public Button btnDelete, btnEdit;

        public OriginalViewHolder(View v) {
            super(v);
            imgPoster = v.findViewById(R.id.imgPoster);
            txtTitle = v.findViewById(R.id.txtTitle);
            txtRating = v.findViewById(R.id.txtRating);
            txtGenre = v.findViewById(R.id.txtGenre);
            txtDirectedBy = v.findViewById(R.id.txtDirectedBy);
            txtTheater = v.findViewById(R.id.txtTheater);
            parentLayout = v.findViewById(R.id.cardViewListItemMovie);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            Movie movie = items.get(position);
            view.txtTitle.setText(movie.getTitle());
            view.txtRating.setText(movie.getRating());
            view.txtGenre.setText(movie.getGenre());
            view.txtDirectedBy.setText(movie.getDirectedBy());
            view.txtTheater.setText(movie.getInTheater());
            image = movie.getImgPoster();
            Picasso.get().load(image).into(view.imgPoster);
            view.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}