package com.example.megha.movieplate.MovieFormat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.megha.movieplate.ApiClientOmdb;
import com.example.megha.movieplate.Constants;
import com.example.megha.movieplate.MovieFormat.Results;
import com.example.megha.movieplate.R;
import com.example.megha.movieplate.Search;
import com.example.megha.movieplate.SearchFragment;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Megha on 02-04-2016.
 */
public class SingleMovieActivity extends AppCompatActivity {

    ImageView iv;
    TextView adult, overview, release_date, original_language, title, vote_average, popularity, vote_count;
    Call<Search> mySearch;
    Results results;
    boolean paused;
    ProgressDialog progressDialog;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Movie Detail");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_container);
        toolbar = (Toolbar) findViewById(R.id.toolbar_details);

        Intent i = getIntent();
        results = (Results) i.getSerializableExtra(Constants.SINGLE_MOVIE_DETAILS);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
    }

    @Override
    protected void onResume() {
        paused = false;
        String title = results.title;
        toolbar.setTitle(title);
        mySearch = ApiClientOmdb.getInterface().getMySearch("",title);
        mySearch.enqueue(new Callback<Search>() {
            @Override
            public void onResponse(Call<Search> call, Response<Search> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {
                    Search search = response.body();
                    SearchFragment searchFragment=new SearchFragment();
                    Bundle b=new Bundle();
                    b.putSerializable("SearchContent",search);
                    searchFragment.setArguments(b);
                    if(!paused)
                        getSupportFragmentManager().beginTransaction().replace(R.id.HomeActivityContainer, searchFragment).commit();
                }
                else{
                    Toast.makeText(SingleMovieActivity.this,response.code()+response.message(),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<Search> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SingleMovieActivity.this, "You are not connected to Internet" , Toast.LENGTH_LONG).show();
            }
        });
        super.onResume();
    }

    @Override
    protected void onPause() {
        paused = true;
        if(progressDialog.isShowing())
            progressDialog.dismiss();
        mySearch.cancel();
        super.onPause();
    }
}
