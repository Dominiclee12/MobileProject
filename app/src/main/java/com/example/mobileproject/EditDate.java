package com.example.mobileproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditDate extends AppCompatDialogFragment {
    private EditText tv_date;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private static final String TAG = "EditDate";


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.editdate,  null);

        builder.setView(view)
                .setTitle("Change New Date Of Birth")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String date = tv_date.getText().toString().trim();

                        if(TextUtils.isEmpty(date)){
                            //stopping the function execution further
                            return;
                        }

                        UserInformation userInformation = new UserInformation();
                        userInformation.setDate(date);
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        databaseReference.child("UserInformation").child(user.getUid()).child("date").setValue(date);
                        Toast.makeText(getActivity(), "New date is changed.", Toast.LENGTH_LONG).show();
                    }
                });

        tv_date = view.findViewById(R.id.tv_date);
        return builder.create();
    }

}
