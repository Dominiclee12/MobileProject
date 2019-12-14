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

public class EditPhone extends AppCompatDialogFragment {
    private EditText et_phone;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.editphone,  null);

        builder.setView(view)
                .setTitle("Change New Phone Number")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String phone = et_phone.getText().toString().trim();

                        if(TextUtils.isEmpty(phone)){
                            //stopping the function execution further
                            return;
                        }

                        UserInformation userInformation = new UserInformation();
                        userInformation.setPhone(phone);
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        databaseReference.child("UserInformation").child(user.getUid()).child("phone").setValue(phone);
                        Toast.makeText(getActivity(), "New phone number is changed.", Toast.LENGTH_LONG).show();
                    }
                });

        et_phone = view.findViewById(R.id.et_phone);
        return builder.create();
    }

}
