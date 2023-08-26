package house.rent;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import house.rent.firebase.FirebaseHandler;
import house.rent.model.Rent;
import house.rent.model.User;

public class HomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView rvHouses;
    private RentAdapter houseAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Rent> rent;

    /*private String USER_TABLE = "User";
    private String RENT_TABLE = "Rent";

    FirebaseHandler firebaseHandler;
    DatabaseReference rentDatabase;
    DatabaseReference userDatabase;*/

    SharedPreferences sp;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle("Home");
        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);

        db = openOrCreateDatabase("HouseRent", MODE_PRIVATE, null);

        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(ID INTEGER PRIMARY KEY AUTOINCREMENT,USERNAME TEXT,EMAIL TEXT,PASSWORD TEXT,ROLE TEXT,NAME TEXT,GENDER TEXT,PHONE TEXT,DOB TEXT,ADDRESS TEXT)";
        db.execSQL(tableQuery);

        String rentQuery = "CREATE TABLE IF NOT EXISTS RENT(ID INTEGER PRIMARY KEY AUTOINCREMENT,USERID TEXT,TITLE TEXT,LOCATION TEXT,RENTFEE TEXT,RENTTYPE TEXT,ADDRESS TEXT,DESCRIPTION TEXT,NOBED TEXT,NOBATH TEXT,CONTACTNAME TEXT,CONTACTNO TEXT,EMAIL TEXT)";
        db.execSQL(rentQuery);

        rvHouses = findViewById(R.id.rvHouses);
        rvHouses.setHasFixedSize(true);

        rent = new ArrayList<Rent>();

        String selectQuery = "SELECT * FROM RENT";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                rent.add(new Rent(cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10), cursor.getString(11),cursor.getString(12), cursor.getString(0)));
            }
            layoutManager = new LinearLayoutManager(HomeActivity.this);
            houseAdapter = new RentAdapter(HomeActivity.this, rent,"Home");

            rvHouses.setLayoutManager(layoutManager);
            rvHouses.setItemAnimator(new DefaultItemAnimator());
            rvHouses.setAdapter(houseAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menusearch, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Rent> newList = new ArrayList<>();
        for (Rent rents : rent) {

            String title = rents.getTitle().toLowerCase();
            String location = rents.getLocation().toLowerCase();
            String price = rents.getFee().toLowerCase();
            String address = rents.getAddress().toLowerCase();

            if (location.contains(newText) || title.contains(newText) ||
                    price.contains(newText) || address.contains(newText)) {
                newList.add(rents);
            }
        }
        houseAdapter.setFilter(newList);
        return true;
    }
}
