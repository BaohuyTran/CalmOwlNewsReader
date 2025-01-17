package com.example.android.calmowlnewsreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.calmowlnewsreader.Model.Articles;
import com.example.android.calmowlnewsreader.Model.Headlines;
//import com.example.android.newsfeed.adapter.CategoryFragmentPagerAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android.calmowlnewsreader.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MainActivity extends AppCompatActivity {

//    private ActivityMainBinding binding;
//    public int currentFragment = Constants.FRAGMENT_HOME;
//    private TabLayout mTabLayout;
//    private ViewPagerAdapter mViewPagerAdapter;
//    private ViewPager2 mViewPager2;


    final String API_KEY = Constants.API_KEY;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    Adapter adapter;
    List<Articles> articles = new ArrayList<>();

    EditText editText;
    Button btnSearch;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        editText = findViewById(R.id.editQuery);
        btnSearch = findViewById(R.id.btnSearch);
        final String country = getCountry();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveJson("", country, API_KEY);
            }
        });
        retrieveJson("", country, API_KEY);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("")){
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            retrieveJson(editText.getText().toString(),country,API_KEY);
                        }
                    });
                    retrieveJson(editText.getText().toString(),country,API_KEY);
                }else{
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            retrieveJson("",country,API_KEY);
                        }
                    });
                    retrieveJson("", country, API_KEY);
                }
            }
        });

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//
//        mViewPager2 = findViewById(R.id.view_pager_2);
//        mTabLayout = findViewById(R.id.sliding_tabs);
//        mViewPagerAdapter = new ViewPagerAdapter(this);
//        mViewPager2.setAdapter(mViewPagerAdapter);
//
//
//        new TabLayoutMediator(mTabLayout, mViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
//            @Override
//            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                switch (position) {
//                    case Constants.FRAGMENT_DASHBOARD:
//                        tab.setText(getString(R.string.dashboard));
//                        break;
//                    case Constants.FRAGMENT_HOME:
//                        tab.setText(getString(R.string.home));
//                        break;
//                    case Constants.FRAGMENT_SPORTS:
//                        tab.setText(getString(R.string.sports));
//                        break;
//                    case Constants.FRAGMENT_SCIENCE:
//                        tab.setText(getString(R.string.science));
//                        break;
//                }
//            }
//        }).attach();
//        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//
//
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        assert navigationView != null;
//        navigationView.setNavigationItemSelectedListener(this);
//        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
//
//
//        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                switch (position) {
//                    case Constants.FRAGMENT_HOME:
//                        currentFragment = Constants.FRAGMENT_HOME;
//                        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
//                        break;
//                    case Constants.FRAGMENT_SPORTS:
//                        currentFragment = Constants.FRAGMENT_SPORTS;
//                        navigationView.getMenu().findItem(R.id.nav_world).setChecked(true);
//                        break;
//                    case Constants.FRAGMENT_SCIENCE:
//                        currentFragment = Constants.FRAGMENT_SCIENCE;
//                        navigationView.getMenu().findItem(R.id.nav_science).setChecked(true);
//                        break;
//                    case Constants.FRAGMENT_DASHBOARD:
//                        currentFragment = Constants.FRAGMENT_DASHBOARD;
//                        navigationView.getMenu().findItem(R.id.nav_dashboard).setChecked(true);
//                        break;
//                }
//            }
//        });



    }


    public void retrieveJson(String query, String country, String apiKey) {
        swipeRefreshLayout.setRefreshing(true);
        Call<Headlines> call;
        if (!editText.getText().toString().equals("")) {
            call = ApiClient.getInstance().getApi().getSpecificData(query, apiKey);
        }
        else {
            call = ApiClient.getInstance().getApi().getHeadlines(country, apiKey);
        }
        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines> call, Response<Headlines> response) {
                if(response.isSuccessful() && response.body().getArticles() != null) {
                    swipeRefreshLayout.setRefreshing(false);
                    articles.clear();
                    articles = response.body().getArticles();
                    adapter = new Adapter(MainActivity.this, articles);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    public String getCountry() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }


    @Override
    // Initialize the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.logout:
                logout();
                // User chose the "Settings" item, show the app settings UI...
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

}