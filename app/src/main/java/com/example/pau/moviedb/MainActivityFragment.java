package com.example.pau.moviedb;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pau.moviedb.Json.*;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;



/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;
    private String apikey="3abc6154c470ac598df9e7d97700f8cd";
    private final String BaseURL="https://api.themoviedb.org/3/movie/";
    private int id;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String[] dataTest={"Peli 1", "Peli 2", "Peli3"};

        items=new ArrayList<>(Arrays.asList(dataTest));
        adapter = new ArrayAdapter<String>(
                getContext(),
                R.layout.listview_pelicula,
                R.id.pelicula,
                items
        );
        ListView listaPeliculas = (ListView) rootView.findViewById(R.id.listaPeliculas);
        listaPeliculas.setAdapter(adapter);

        setHasOptionsMenu(true);
        return rootView;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_fragment_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void refresh(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MovieDB servei = retrofit.create(MovieDB.class);
        //Call<TopMovies> call = servei.getPelicuesMillorCalificades(apikey);
        Call<Posters> call = servei.getPosters(apikey);
        call.enqueue(new Callback<Posters>() { //call.enqueue(new Callback<TopMovies>() {
            @Override
            public void onResponse(Response<Posters> response, Retrofit retrofit) {//public void onResponse(Response<TopMovies> response, Retrofit retrofit) {
                if(response.isSuccess()){
                    Toast t= Toast.makeText(getContext(), "Success call", Toast.LENGTH_LONG);
                    t.show();
                    Posters pelis=response.body(); //TopMovies pelis=response.body();
                    adapter.clear();

                    for(Backdrop peli: pelis.getBackdrops()){ //for(Pelicula peli: pelis.getResults()){
                        adapter.add("Id : "+peli.getFilePath());  // adapter.add("Id : "+peli.getId());
                    }
                }else{
                    Toast t= Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG);
                    t.show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private interface MovieDB{
        @GET("top_rated")
        Call<TopMovies> getPelicuesMillorCalificades(@Query("api_key")String api_key);

        @GET("129/images")
        Call<Posters> getPosters(@Query("api_key")String api_key);
    };

}
