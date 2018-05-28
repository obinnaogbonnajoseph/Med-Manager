/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.med_manager.ui;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.example.android.med_manager.R;
import com.example.android.med_manager.adapter.PrescriptionListAdapter;
import com.example.android.med_manager.data.PrescriptionInfo;
import com.example.android.med_manager.databinding.ActivityHomePageBinding;
import com.example.android.med_manager.databinding.ContentHomePageBinding;
import com.example.android.med_manager.model.MedViewModel;

import java.util.Date;
import java.util.List;


public class HomePageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PrescriptionListAdapter.AdapterOnClickHandler {
    // View Model instance
    private MedViewModel mModel;
    // DataBinding instance
    ActivityHomePageBinding mBinding;
    ContentHomePageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // get view model form ViewModelProvider
        mModel = ViewModelProviders.of(this).get(MedViewModel.class);

        // Set up the recycler view
        final PrescriptionListAdapter adapter = new PrescriptionListAdapter(this, this);
        binding.myRecyclerView.setAdapter(adapter);
        binding.myRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // add observer for LiveData returned by getAllMeds()
        mModel.getAllMeds().observe(this, new Observer<List<PrescriptionInfo>>() {
            @Override
            public void onChanged(@Nullable List<PrescriptionInfo> prescriptionInfos) {
                // Update the cached copy of the data in the adapter
                adapter.setPrescriptions(prescriptionInfos);
            }
        });

        // Handle the search intent
        handleIntent(getIntent());

        // Finds the empty view layout
        LinearLayout layout = findViewById(R.id.empty_view);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Opens the add prescription activity
                Intent intent = new Intent(HomePageActivity.this, PrescriptionActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Check to see if database is empty or not
        layout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(getIntent());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // Close the app
           Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        // Associate searchable configuration with the SearchView
        SearchManager manager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (manager != null) {
            searchView.setSearchableInfo(
                    manager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconified(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            onSearchRequested();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch(id) {
            case R.id.nav_profile:
                break;
            case R.id.nav_about:
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(HomePageActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(PrescriptionInfo prescription) {
        // Get the necessary objects to be sent to the prescription activity
        List<Date> dates = prescription.getDates();
        List<String> times = prescription.getTimes();
        List<Integer> weekdays = prescription.getWeekdays();
        int freq = prescription.getFrequency();
        String medDescription = prescription.getMedDescription();
        String medName = prescription.getMedname();
    }

    private void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // Search for the data

            Toast.makeText(this,query+" received",Toast.LENGTH_SHORT).show();
        }
    }
}
