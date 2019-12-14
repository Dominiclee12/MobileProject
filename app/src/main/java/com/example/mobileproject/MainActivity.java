package com.example.mobileproject;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_email, et_password, et_name, et_phone;
    private Spinner sp_gender;
    private TextView tv_login, tv_date;
    private Button btn_register;

    private ProgressDialog progressDialog;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    //authentication
    private FirebaseAuth.AuthStateListener authStateListener;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing views
        btn_register = (Button) findViewById(R.id.btn_register);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_login = (TextView) findViewById(R.id.tv_login);
        et_name = (EditText) findViewById(R.id.et_name);
        et_phone = (EditText) findViewById(R.id.et_phone);
        sp_gender = (Spinner) findViewById(R.id.sp_gender);
        tv_date = (TextView) findViewById(R.id.tv_date);

        progressDialog = new ProgressDialog(this);

        //initializing firebaseauth object
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("UserInformation");

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

        // attaching listener to button
        btn_register.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        tv_date.setOnClickListener(this);
        spinnerGender();

    }

    private void registerUser() {

        //getting email and password from edits text
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String name = et_name.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        String gender = sp_gender.getSelectedItem().toString();
        String date = tv_date.getText().toString().trim();

        //checking if email or password are empty
        if (TextUtils.isEmpty(email)) {
            // email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(password)) {
            // password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        // checking is it empty or not
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter phone", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(gender)) {
            Toast.makeText(this, "Please enter gender", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Please enter date", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;
        }

        //if validationa are ok
        //we will first show a progressBar
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //checking if success
                        if (task.isSuccessful()) {

                            //send email verification
                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        saveUserInformation();

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            //display some message here
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "The Email Is Registed Already.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    public void saveUserInformation() {

        //getting name, phone from edits text
        String name = et_name.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        String gender = sp_gender.getSelectedItem().toString();
        String email = et_email.getText().toString().trim();
        String date = tv_date.getText().toString().trim();

        //store data to database
        UserInformation userInformation = new UserInformation(name, phone, gender, email, date);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child(user.getUid()).setValue(userInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Registered successfully. Please check your email for verification."
                            , Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });

    }

    public void spinnerGender() {
        List<String> category = new ArrayList<String>();
        category.add(0, "Male");
        category.add("Female");

        ArrayAdapter<String> dataAdapter;

        //style and populate the spinner
        dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, category);

        //Dropdown layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Attaching data adapter to spinner
        sp_gender.setAdapter(dataAdapter);

        sp_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Choose Gender")) {
                    // do nothing
                } else {
                    //on selecting a spinner item
                    String item = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void selectdate() {
        DatePickerDialog.OnDateSetListener mDateListener;

        mDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1; //January is zero so must plus 1
                Log.d(TAG, "onDateSet:mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                tv_date.setText(date);

            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateListener, year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    @Override
    public void onClick(View view) {

        if (view == btn_register) {
            registerUser();
        }

        if (view == tv_login) {
            //will login activity here
            startActivity(new Intent(this, LoginActivity.class));
        }

        if (view == tv_date) {
            selectdate();
        }
    }
}