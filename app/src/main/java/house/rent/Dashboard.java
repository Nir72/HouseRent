package house.rent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import house.rent.firebase.FirebaseHandler;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView ivDisplayPicture;
    private ImageView ivHeaderDisplayPicture;
    private TextView tvWelcomeUser;
    private TextView tvHeaderUserName;
    private TextView tvHeaderEmail;

    private TextView tvTotalAds;
    private TextView tvTotalUsers;

    /*private String USER_TABLE = "User";
    private String RENT_TABLE = "Rent";

    private DatabaseReference userDatabase;
    private DatabaseReference rentDatabase;*/

    SharedPreferences sp;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sp = getSharedPreferences(ConstantSp.PREF,MODE_PRIVATE);

        db = openOrCreateDatabase("HouseRent", MODE_PRIVATE, null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(ID INTEGER PRIMARY KEY AUTOINCREMENT,USERNAME TEXT,EMAIL TEXT,PASSWORD TEXT,ROLE TEXT,NAME TEXT,GENDER TEXT,PHONE TEXT,DOB TEXT,ADDRESS TEXT)";
        db.execSQL(tableQuery);

        String rentQuery = "CREATE TABLE IF NOT EXISTS RENT(ID INTEGER PRIMARY KEY AUTOINCREMENT,USERID TEXT,TITLE TEXT,LOCATION TEXT,RENTFEE TEXT,RENTTYPE TEXT,ADDRESS TEXT,DESCRIPTION TEXT,NOBED TEXT,NOBATH TEXT,CONTACTNAME TEXT,CONTACTNO TEXT,EMAIL TEXT)";
        db.execSQL(rentQuery);

        ivDisplayPicture = findViewById(R.id.ivDisplayPicture);
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);

        tvTotalAds = findViewById(R.id.tvTotalAds);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);



        if (sp.getString(ConstantSp.ROLE,"").equals("Renter")) {
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.getMenu().findItem(R.id.itemAddRent).setVisible(false);
            navigationView.getMenu().findItem(R.id.itemMyPosts).setVisible(false);
        } else if (sp.getString(ConstantSp.ROLE,"").equals("Owner")) {
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.getMenu().findItem(R.id.itemAddRent).setVisible(true);
            navigationView.getMenu().findItem(R.id.itemMyPosts).setVisible(true);
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(Dashboard.this);
        alert.setTitle("Welcome");
        alert.setMessage("" + sp.getString(ConstantSp.NAME,""));
        alert.show();
        tvWelcomeUser.setText("Welcome\n" + sp.getString(ConstantSp.EMAIL,""));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        ivHeaderDisplayPicture = headerView.findViewById(R.id.ivHeaderDisplayPicture);
        tvHeaderUserName = headerView.findViewById(R.id.tvHeaderUserName);
        tvHeaderEmail = headerView.findViewById(R.id.tvHeaderEmail);

        tvHeaderUserName.setText(sp.getString(ConstantSp.NAME,""));
        tvHeaderEmail.setText(sp.getString(ConstantSp.EMAIL,""));

        String selectQuery = "SELECT * FROM USERS";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.getCount() == 0) {
            tvTotalUsers.setText(cursor.getCount() + "\nUsers");
        } else if (cursor.getCount() > 500) {
            tvTotalUsers.setText(500 + "+\nUsers");
        } else {
            tvTotalUsers.setText(cursor.getCount() + "\nUsers");
        }

        String selectRentQuery = "SELECT * FROM RENT";
        Cursor rentCursor = db.rawQuery(selectRentQuery,null);
        if (rentCursor.getCount() == 0) {
            tvTotalAds.setText(rentCursor.getCount() + "\nAds");
        } else if (rentCursor.getCount() > 500) {
            tvTotalAds.setText(500 + "+\nAds");
        } else {
            tvTotalAds.setText(rentCursor.getCount() + "\nAds");
        }

        /*rentDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();

                if (count == 0) {
                    tvTotalAds.setText(count + "\nAds");
                } else if (count > 500) {
                    tvTotalAds.setText(500 + "+\nAds");
                } else {
                    tvTotalAds.setText(count + "\nAds");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
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
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuAbout) {
            AlertDialog aboutme = new AlertDialog.Builder(Dashboard.this)
                    .setTitle("ABOUT Developer")
                    .setIcon(android.R.drawable.ic_menu_agenda)
                    .setMessage("Name: Niraj Gohil\n" +
                            "Email: nirajgohil7372@gmail.com")
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.itemHome) {
            Intent homeIntent = new Intent(Dashboard.this, HomeActivity.class);
            startActivity(homeIntent);
        } else if(id == R.id.itemAddRent) {
            Intent postAdIntent = new Intent(Dashboard.this, PostAdActivity.class);
            startActivity(postAdIntent);
        } else if (id == R.id.itemMyPosts) {
            Intent myPostIntent = new Intent(Dashboard.this, MyPosts.class);
            startActivity(myPostIntent);
        } else if (id == R.id.itemProfile) {
            Intent profileIntent = new Intent(Dashboard.this, Profile.class);
            startActivity(profileIntent);
        } else if (id == R.id.itemSignOut) {
            super.onBackPressed();
            Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
