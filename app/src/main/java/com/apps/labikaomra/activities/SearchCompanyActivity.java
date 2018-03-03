package com.apps.labikaomra.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.apps.labikaomra.R;
import com.apps.labikaomra.adapters.RecyclerSearchAdapter;
import com.apps.labikaomra.models.Company;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchCompanyActivity extends AppCompatActivity implements RecyclerSearchAdapter.CompanyAdapterListener {
    RecyclerView recyclerViewSearch;
    RecyclerSearchAdapter searchAdapter;
    List<Company> searchDetails;
    SearchView searchView;
    private DatabaseReference mSearchRefData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_company);

        Toolbar toolbar = (Toolbar) findViewById(R.id.searchcompany);
        setSupportActionBar(toolbar);
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Companies");

        mSearchRefData = FirebaseDatabase.getInstance().getReference().child("company");

        recyclerViewSearch = (RecyclerView) findViewById(R.id.recyclesearch);
        recyclerViewSearch.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewSearch.setLayoutManager(layoutManager);

        searchDetails = new ArrayList<>();

        mSearchRefData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Company company = postSnapshot.getValue(Company.class);
//                    Toast.makeText(SearchCompanyActivity.this, company + "", Toast.LENGTH_SHORT).show();
                    searchDetails.add(company);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        searchAdapter = new RecyclerSearchAdapter(getBaseContext(), searchDetails, this);
        recyclerViewSearch.setAdapter(searchAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

//        MenuItem search = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        search(searchView);

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchAdapter != null)
                    searchAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onCompanySelected(Company company) {
        Toast.makeText(getApplicationContext(), "Selected: " + company.getFirstName() + ", " + company.getEmail(), Toast.LENGTH_LONG).show();
        Intent getcompany = new Intent(SearchCompanyActivity.this, Home.class);
        getcompany.putExtra("nameCompany",company.getFirstName());
        startActivity(getcompany);
    }
}
