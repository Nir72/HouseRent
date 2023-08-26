package house.rent;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import house.rent.firebase.FirebaseHandler;
import house.rent.model.User;

public class SignupActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Spinner spinnerRoles;
    private Button btnSignUp;
    private TextView tvSignIn;

    private Context context;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle("Signup");

        db = openOrCreateDatabase("HouseRent", MODE_PRIVATE, null);
        String tableQuery = "CREATE TABLE IF NOT EXISTS USERS(ID INTEGER PRIMARY KEY AUTOINCREMENT,USERNAME TEXT,EMAIL TEXT,PASSWORD TEXT,ROLE TEXT,NAME TEXT,GENDER TEXT,PHONE TEXT,DOB TEXT,ADDRESS TEXT)";
        db.execSQL(tableQuery);

        String rentQuery = "CREATE TABLE IF NOT EXISTS RENT(ID INTEGER PRIMARY KEY AUTOINCREMENT,USERID TEXT,TITLE TEXT,LOCATION TEXT,RENTFEE TEXT,RENTTYPE TEXT,ADDRESS TEXT,DESCRIPTION TEXT,NOBED TEXT,NOBATH TEXT,CONTACTNAME TEXT,CONTACTNO TEXT,EMAIL TEXT)";
        db.execSQL(rentQuery);

        context = SignupActivity.this;

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spinnerRoles = findViewById(R.id.spinnerRoles);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignIn = findViewById(R.id.tvSignIn);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = etUsername.getText().toString().trim();
                final String email = etEmail.getText().toString().trim();
                final String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                final String role = spinnerRoles.getSelectedItem().toString();

                if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
                    alert.setTitle("Failure");
                    alert.setMessage("Fill up all the fields!");
                    alert.show();
//                    Toast.makeText(context, "Fill up all the fields!", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
                    alert.setTitle("Failure");
                    alert.setMessage("Passwords don't matched!");
                    alert.show();
//                    Toast.makeText(context, "Passwords don't matched!", Toast.LENGTH_SHORT).show();
                } else {
                    String selectQuery = "SELECT * FROM USERS WHERE USERNAME='" + userName + "' AND ROLE='" + role + "'";
                    Cursor cursor = db.rawQuery(selectQuery, null);
                    if (cursor.getCount() > 0) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
                        alert.setTitle("Failure");
                        alert.setMessage("Username already taken!");
                        alert.show();
                    } else {
                        String insertQuery = "INSERT INTO USERS VALUES(NULL,'" + userName + "','" + email + "','" + password + "','" + role + "','','','','','')";
                        db.execSQL(insertQuery);

                        AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
                        alert.setTitle("Success");
                        alert.setMessage("Sign up done successfully!");
                        alert.show();
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                    }
                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(context, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }


}
