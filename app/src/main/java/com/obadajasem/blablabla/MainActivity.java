package com.obadajasem.blablabla;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.obadajasem.blablabla.adapter.AlbumsAdapter;
import com.obadajasem.blablabla.api.RadioApi;
import com.obadajasem.blablabla.model.Station;
import com.obadajasem.blablabla.Services.PlayerService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements AlbumsAdapter.OnNoteListener  {
    private static final String TAG = "MainActivity";
    public static final String STATION_NAME = "NAME";
    public static final String STATION_STATE = "STATE";
    public static final String STATION_URL = "URL";
    public static final String STATION_IMG = "IMG";
    public static final String STATION_FAVOURIT ="favourit" ;
    public static final String STATION_FAVOURIT_NAME ="NAME" ;
    public static final String STATION_FAVOURIT_URL ="FAV_URL" ;
    public static final String STATION_FAVOURIT_IMG ="FAV_IMG" ;
    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private List<Station> stationList;
    private FirebaseAuth mAuth;
    private TextView welcometv;
    private Menu menu;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcometv = findViewById(R.id.welcome);
        progressBar = findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();

      String  username = mAuth.getCurrentUser().getDisplayName() ;

      if(!(username.equals("")))
        welcometv.setText("Welcome "+username);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();

        recyclerView = findViewById(R.id.recycler_view);
        stationList = new ArrayList<>();
        adapter = new AlbumsAdapter(this, stationList, MainActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        fetchData();
   try {
            Glide.with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchData() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.radio-browser.info/webservice/json/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RadioApi radioApi = retrofit.create(RadioApi.class);
        Call<List<Station>> call = radioApi.getstations();
        call.enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
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


                    Log.d(TAG, "onResponse: " + station.getUrl());

                }
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
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
                    showOption(R.id.action_info);
                    isShow = true;
                } else if (isShow) {

                    collapsingToolbar.setTitle(" ");
                    hideOption(R.id.action_info);
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
        hideOption(R.id.action_info);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            toasty("go to Setting ");
            return true;
        } else if (id == R.id.action_info) {
            toasty("go to info ");
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
        newintent.putExtra(STATION_IMG,stationList.get(position).getFavicon());

        Util.startForegroundService(MainActivity.this, newintent);

        int sr=  Util.getNetworkType(this);

        Log.d(TAG, "onNoteClick: "+sr);

        toasty("You are now listing to " + stationList.get(position).getName());

    }

    @Override
    public void onDotsClick(int position) {
        Intent intent = new Intent(MainActivity.this,FavouritStationActivity.class);
        intent.putExtra(STATION_FAVOURIT,position);
        intent.putExtra(STATION_FAVOURIT_NAME,stationList.get(position).getName());
        intent.putExtra(STATION_FAVOURIT_URL,stationList.get(position).getUrl());
        intent.putExtra(STATION_FAVOURIT_IMG,stationList.get(position).getFavicon());
    }

    /**
//         * RecyclerView item decoration - give equal margin around grid item
//         */
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


}