package com.example.android.popularmovie;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMovieFragment extends Fragment {

    private GridViewAdapter adp;
    private GridView gridView;

    public PopularMovieFragment() {
    }

    public void onStart() {
        super.onStart();
        update();
    }

    private void update() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = sharedPrefs.getString(
                getString(R.string.pref_order_key),
                getString(R.string.default_order_preference_pop));

        FetchPopularMovie fpm = new FetchPopularMovie();
        fpm.execute(sortOrder);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_popular_movie, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridview);

        return rootView;
    }

    public class FetchPopularMovie extends AsyncTask<String, Void, ArrayList<String>> {
        private final String LOG_TAG = FetchPopularMovie.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(String... params) {
           /* if (params.length == 0) {
                return null;
            }*/

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String jsonStr = null;
            String sortOrder = params[0];

            try {

                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String ORDER = "sort_by";
                final String API = "api_key";
                final String KEY = "a94b7c391c5ddc0b2ef8b10460966a65";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(ORDER, sortOrder+".desc")
                        .appendQueryParameter(API, KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();

                Log.d(LOG_TAG, "Movie string: " + jsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getUrlFromJson(jsonStr);
            } catch(JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        private ArrayList<String> getUrlFromJson(String movieString) throws JSONException {
            JSONObject json = new JSONObject(movieString);
            JSONArray movieArray = json.getJSONArray("results");

            ArrayList<String> movieUrls = new ArrayList<>();
            for (int i = 0; i < movieArray.length(); i++) {
                movieUrls.add("http://image.tmdb.org/t/p/w185" + movieArray.getJSONObject(i).getString("poster_path"));
            }
            Log.d(LOG_TAG, movieUrls.toString());
            return movieUrls;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {

            if (result != null) {
                adp = new GridViewAdapter(getActivity(), result);
                gridView.setAdapter(adp);
            }
        }
    }
}
