package id.sam.omdb2.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MovieDAO {
    @Query("SELECT * FROM Movie")
    List<Movie> getAll();

    @Query("SELECT * FROM Movie WHERE title LIKE '%' || :title || '%'")
    List<Movie> findByNama(String title);

    @Query("SELECT * FROM Movie WHERE title = :title")
    Movie findByTitle(String title);

    @Insert
    void insertAll(Movie... movies);

    @Delete
    void deleteBiodata(Movie movie);

    @Update
    int updateBiodata(Movie movie);
}
