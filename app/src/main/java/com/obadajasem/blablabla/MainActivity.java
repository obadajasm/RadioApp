package com.obadajasem.blablabla;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.obadajasem.blablabla.Services.PlayerService;
import com.obadajasem.blablabla.adapter.AlbumsAdapter;
import com.obadajasem.blablabla.api.RadioApi;
import com.obadajasem.blablabla.model.Country;
import com.obadajasem.blablabla.model.Station;
import com.obadajasem.blablabla.sign.SignIn;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements AlbumsAdapter.OnNoteListener {
    private static final String TAG = "MainActivity";
    public static final String STATION_NAME = "NAME";
    public static final String STATION_STATE = "STATE";
    public static final String STATION_URL = "URL";
    public static final String STATION_IMG = "IMG";
    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private List<Station> stationList;
    private List<Country> countryList;
    private FirebaseAuth mAuth;
    private Menu menu;
    private ProgressBar progressBar;
    private AdView mAdView;
    private String SelectedCountry;
    private String SavedSelctedCountry;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressbar);
        mAdView = findViewById(R.id.adView);
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout= findViewById(R.id.swipe);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

                Log.d(TAG, "onInitializationComplete: ");
            }

        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initCollapsingToolbar();
        stationList = new ArrayList<>();
        countryList = new ArrayList<>();
        adapter = new AlbumsAdapter(this, stationList, MainActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        try {
            Glide.with(this).load(R.drawable.newcover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences settings = MainActivity.this.getSharedPreferences("PREF", Context.MODE_PRIVATE);
        SavedSelctedCountry = settings.getString("key", "syria");

        if( countryList.isEmpty()){
            fetchCountries();
        }else {
            fetchDataByCountry(SavedSelctedCountry);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchDataByCountry(SavedSelctedCountry);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }



    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
//                    showOption(R.id.action_info);
                    isShow = true;
                } else if (isShow) {

                    collapsingToolbar.setTitle(" ");
//                    hideOption(R.id.action_info);
                    isShow = false;
                }
            }
        });
    }

    // -----------------Adding the main menu-------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        hideOption(R.id.action_info);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            if (mAuth.getCurrentUser() != null){
                mAuth.signOut();
            Intent i = new Intent(MainActivity.this, SignIn.class);
            startActivity(i);
            finish();
            }else{
                Toast.makeText(this,"SIgnIn first !",Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_signin) {

            Intent i = new Intent(MainActivity.this, SignIn.class);
            startActivity(i);
            return true;
        }else if (id == R.id.exit) {
            Intent i = new Intent(MainActivity.this, PlayerService.class);
            stopService(i);
            finish();

            return true;
        }else if (id == R.id.action_country) {
           BuildAlert();

            return true;
        }else if (id == R.id.about) {
      AboutAlert222();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public void onNoteClick(int position) {
//        Intent i = new Intent(MainActivity.this,FavouritStationActivity.class);
//        startActivity(i);

        Intent newintent = new Intent(MainActivity.this, PlayerService.class);
        newintent.putExtra(STATION_URL, stationList.get(position).getUrl());
        newintent.putExtra(STATION_NAME, stationList.get(position).getName());
        newintent.putExtra(STATION_STATE, stationList.get(position).getState());
        newintent.putExtra(STATION_IMG, stationList.get(position).getFavicon());

        Util.startForegroundService(MainActivity.this, newintent);

        toasty("You are now listing to " + stationList.get(position).getName());

    }

    /**
     * //         * RecyclerView item decoration - give equal margin around grid item
     * //
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void toasty(String s) {
        Toast.makeText(this, " " + s, Toast.LENGTH_SHORT).show();
    }


    private void fetchDataByCountry(String country ) {
        stationList.clear();
        Retrofit retrofit = new Retrofit.Builder()
                //http://www.radio-browser.info/webservice/json/
                //https://fr1.api.radio-browser.info/json/stations/bycountry/syria
                .baseUrl("https://fr1.api.radio-browser.info/json/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RadioApi radioApi = retrofit.create(RadioApi.class);
        Call<List<Station>> call = radioApi.getStationsByCountry(country);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful()) {
                    toasty("onResponse code :" + response.code());
                    return;
                }
                List<Station> stations = response.body();
                for (Station station : stations) {
                    Station stationholder = new Station();
                    stationholder.setName(station.getName());
                    stationholder.setFavicon(station.getFavicon());
                    stationholder.setState(station.getState());
                    stationholder.setVotes(station.getVotes());
                    stationholder.setUrl(station.getUrl());
                    stationList.add(stationholder);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {

                Toast.makeText(MainActivity.this, "Something Went Bad ..." +
                        "Check Your Internet Connection Or Try Again", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });


    }

    private void fetchCountries() {

        Retrofit retrofit = new Retrofit.Builder()
                //http://www.radio-browser.info/webservice/json/
                //https://fr1.api.radio-browser.info/json/stations/bycountry/syria
                .baseUrl("https://fr1.api.radio-browser.info/json/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RadioApi radioApi = retrofit.create(RadioApi.class);
        Call<List<Country>> call = radioApi.getCountriesList();
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful()) {
                    toasty("onResponse code :" + response.code());
                    return;
                }
                List<Country> countries = response.body();
                for (Country country : countries) {
                    Country Countryholder = new Country();
                    Countryholder.setName(country.getName());
                    countryList.add(Countryholder);
                }
        if(SavedSelctedCountry==null){ BuildAlert();}else{fetchDataByCountry(SavedSelctedCountry);}


            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {

                Toast.makeText(MainActivity.this, "Something Went Bad ..." +
                        "Check Your Internet Connection Or Try Again", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });


    }




    public  void BuildAlert(){


if( ! countryList.isEmpty()){

    AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);

    builderSingle.setTitle(" Select  Country  :   ");

    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);

    for(Country country : countryList){
        arrayAdapter.add(country.getName());
    }

    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    });

    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String strName = arrayAdapter.getItem(which);
            AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
            builderInner.setMessage(strName);
            SelectedCountry=strName;
            fetchDataByCountry(SelectedCountry);


            SharedPreferences settings = MainActivity.this.getSharedPreferences("PREF", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("key", SelectedCountry);
            editor.apply();



    }
    });

    builderSingle.show();
}


    }


    public void AboutAlert222(){
        final FlatDialog flatDialog = new FlatDialog(MainActivity.this);
        flatDialog.setTitle("Author")
                .setSubtitle("Contact me")
                .setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark))
                .setFirstButtonText("obadajasm0@gmail.com")
                .setFirstButtonColor(Color.GRAY)
                .setSecondButtonText("CANCEL")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"obadajasm0@gmail.com" });

                        startActivity(Intent.createChooser(intent, "Choose an email client"));
                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        flatDialog.dismiss();
                    }
                })
                .show();
    }

}