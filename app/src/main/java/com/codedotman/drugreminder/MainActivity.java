package com.codedotman.drugreminder;

import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.support.v4.view.GravityCompat;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codedotman.drugreminder.Data.ReminderContract;
import com.codedotman.drugreminder.Data.ReminderDbHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, NavigationView.OnNavigationItemSelectedListener {

    ListView reminder_ListView;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    CustomCursorAdapter mCursorAdapter;
    ReminderDbHelper reminderDbHelper = new ReminderDbHelper(this);
    private static final int VEHICLE_LOADER = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // initialises views
        ButterKnife.bind(this);

        // initialises the auth
        auth = FirebaseAuth.getInstance();


        firebaseUser = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(firebaseUser.getUid());

        //Use you DB reference object and add this method to access realtime data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Connect the views of navigation bar
                NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
                View headerLayout = mNavigationView.getHeaderView(0);
                TextView Name   = (TextView) headerLayout.findViewById(R.id.user_email);
                TextView ID   = (TextView) headerLayout.findViewById(R.id.user_display_name);
                //Fetch values from you database child and set it to the specific view object.
                Name.setText(dataSnapshot.child("name").toString());
                ID.setText(dataSnapshot.child("sid").toString());



            }


            //

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reminder_ListView = (ListView) findViewById(R.id.list);


        mCursorAdapter = new CustomCursorAdapter (this, null);
        reminder_ListView.setAdapter(mCursorAdapter);

        reminder_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, AddReminder.class);

                Uri currentVehicleUri = ContentUris.withAppendedId(ReminderContract.AlarmReminderEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentVehicleUri);

                startActivity(intent);

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddReminder.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        getLoaderManager().initLoader(VEHICLE_LOADER, null, this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_set_reminder) {
            Intent i = new Intent(MainActivity.this, AddReminder.class);
            startActivity(i);

        } else if (id == R.id.nav_view_reminder) {

        } else if (id == R.id.nav_LogOut) {
            signOut();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    //signs user out
    public void signOut() {
        auth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ReminderContract.AlarmReminderEntry._ID,
                ReminderContract.AlarmReminderEntry.KEY_TITLE,
                ReminderContract.AlarmReminderEntry.KEY_DATE,
                ReminderContract.AlarmReminderEntry.KEY_TIME,
                ReminderContract.AlarmReminderEntry.KEY_REPEAT,
                ReminderContract.AlarmReminderEntry.KEY_REPEAT_NO,
                ReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE,
                ReminderContract.AlarmReminderEntry.KEY_ACTIVE

        };

        return new CursorLoader(this,   // Parent activity context
                ReminderContract.AlarmReminderEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }
}
