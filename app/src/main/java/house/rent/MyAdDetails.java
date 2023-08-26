package house.rent;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import house.rent.firebase.FirebaseHandler;

public class MyAdDetails extends AppCompatActivity {

    private EditText tvMyPostTitle;
    private EditText tvMyLocation;
    private ImageView ivMyPostImage;
    private EditText tvMyPostHousePrice;
    private Spinner tvMyPostPeriod;
    private EditText tvMyAddress;
    private EditText tvMyPostDescription;
    private EditText tvMyNumOfBeds;
    private EditText tvMyNumOfBaths;
    private EditText tvMyContact;
    private EditText tvMyEmail;
    private Button btnMyLocation;

    String selectedKey;
    SQLiteDatabase db;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ad_details);
        getSupportActionBar().setTitle("Home Details");
        sp = getSharedPreferences(ConstantSp.PREF, MODE_PRIVATE);

        db = openOrCreateDatabase("HouseRent", MODE_PRIVATE, null);

        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(ID INTEGER PRIMARY KEY AUTOINCREMENT,USERNAME TEXT,EMAIL TEXT,PASSWORD TEXT,ROLE TEXT,NAME TEXT,GENDER TEXT,PHONE TEXT,DOB TEXT,ADDRESS TEXT)";
        db.execSQL(tableQuery);

        String rentQuery = "CREATE TABLE IF NOT EXISTS RENT(ID INTEGER PRIMARY KEY AUTOINCREMENT,USERID TEXT,TITLE TEXT,LOCATION TEXT,RENTFEE TEXT,RENTTYPE TEXT,ADDRESS TEXT,DESCRIPTION TEXT,NOBED TEXT,NOBATH TEXT,CONTACTNAME TEXT,CONTACTNO TEXT,EMAIL TEXT)";
        db.execSQL(rentQuery);

        tvMyPostTitle = findViewById(R.id.tvMyPostTitle);
        tvMyLocation = findViewById(R.id.tvMyLocation);
        ivMyPostImage = findViewById(R.id.ivMyPostImage);
        tvMyPostHousePrice = findViewById(R.id.tvMyPostHousePrice);
        tvMyAddress = findViewById(R.id.tvMyAddress);
        tvMyPostPeriod = findViewById(R.id.tvMyPostPeriod);
        tvMyPostDescription = findViewById(R.id.tvMyPostDescription);
        tvMyNumOfBeds = findViewById(R.id.tvMyNumOfBeds);
        tvMyNumOfBaths = findViewById(R.id.tvMyNumOfBaths);
        tvMyContact = findViewById(R.id.tvMyContact);
        tvMyEmail = findViewById(R.id.tvMyEmail);
        btnMyLocation = findViewById(R.id.btnMyLocation);

        ivMyPostImage.setImageResource(R.drawable.ic_for_rent_signage);
        Bundle bundle = getIntent().getExtras();

        String title = bundle.getString("title");
        String postBy = bundle.getString("postBy");
        String date = bundle.getString("date");
        String location = bundle.getString("location");
        String fee = bundle.getString("fee");
        String period = bundle.getString("period");
        final String address = bundle.getString("address");
        String description = bundle.getString("description");
        int numBeds = bundle.getInt("beds");
        int numBaths = bundle.getInt("baths");
        final String contact = bundle.getString("contact");
        String email = bundle.getString("email");
        selectedKey = bundle.getString("key");

        tvMyPostTitle.setText(title);
        tvMyLocation.setText(location);
        tvMyPostHousePrice.setText(fee);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.period, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tvMyPostPeriod.setAdapter(adapter);
        if (period != null) {
            if (period.equals("month")) {
                tvMyPostPeriod.setSelection(adapter.getPosition("Monthly"));
            } else {
                tvMyPostPeriod.setSelection(adapter.getPosition("Yearly"));
            }
        }

        tvMyAddress.setText(address);
        tvMyPostDescription.setText(description);
        tvMyNumOfBeds.setText(numBeds + "");
        tvMyNumOfBaths.setText(numBaths + "");
        tvMyContact.setText(contact);
        tvMyEmail.setText(email);

        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address)));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(MyAdDetails.this);
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuEditPost) {
            String sType = "";
            if (tvMyPostPeriod.getSelectedItem().toString().equals("Monthly")) {
                sType = "month";
            } else {
                sType = "year";
            }
            String updateQuery = "UPDATE RENT SET TITLE='" + tvMyPostTitle.getText().toString().trim() + "',LOCATION='" + tvMyLocation.getText().toString().trim() + "',RENTFEE='" + tvMyPostHousePrice.getText().toString().trim() + "',RENTTYPE='" + sType + "',ADDRESS='" + tvMyAddress.getText().toString().trim() + "',DESCRIPTION='" + tvMyPostDescription.getText().toString().trim() + "',NOBED='" + tvMyNumOfBeds.getText().toString() + "',NOBATH='" + tvMyNumOfBaths.getText().toString() + "',CONTACTNO='" + tvMyContact.getText().toString().trim() + "' WHERE ID='" + selectedKey + "'";
            db.execSQL(updateQuery);
            new AlertDialog.Builder(MyAdDetails.this).setTitle("Success").setMessage("Updated successfully!").show();
            startActivity(new Intent(MyAdDetails.this, MyPosts.class));
        } else if (item.getItemId() == R.id.menuDeletePost) {
            new AlertDialog.Builder(MyAdDetails.this).setTitle("Warning").setMessage("Do you really want to Delete this post?").setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    String deleteQuery = "DELETE FROM RENT WHERE ID='" + selectedKey + "'";
                    db.execSQL(deleteQuery);
                    new AlertDialog.Builder(MyAdDetails.this).setTitle("Success").setMessage("Post has been deleted!").setIcon(R.drawable.ic_done_black_24dp).show();
                    startActivity(new Intent(MyAdDetails.this, MyPosts.class));
                }
            }).setNegativeButton(android.R.string.no, null).show();
        }
        return true;
    }
}