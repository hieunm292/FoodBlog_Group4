package com.example.jeremy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_pass_confirm_field;
    private Button reg_btn;
    private Button reg_login_btn;
    private ProgressBar reg_rogress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        reg_email_field = (EditText) findViewById(R.id.reg_email);
        reg_pass_field =(EditText)  findViewById(R.id.reg_password);
        reg_pass_confirm_field = (EditText) findViewById(R.id.reg_confirm_password);
        reg_btn = (Button) findViewById(R.id.reg_btn);
        reg_login_btn =(Button) findViewById(R.id.reg_login_btn);
        reg_rogress = (ProgressBar) findViewById(R.id.reg_progress);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = reg_email_field.getText().toString().trim();
                String pass = reg_pass_field.getText().toString().trim();
                String passConf = reg_pass_confirm_field.getText().toString().trim();

                if(!emailVerify(email)){
                    Toast.makeText(RegisterActivity.this, "Email cant be empty !", Toast.LENGTH_SHORT).show();
                }else{
                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
                        if (pass.equals(passConf)) {
                            reg_rogress.setVisibility(View.VISIBLE);
                            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        sendToSetup();
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    reg_rogress.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Passsword and Password Confirm doesn't match .", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(RegisterActivity.this, "Please enter all the details .", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        reg_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });


    }

    private boolean emailVerify(String email){
        int flag=0,f=0;
        for(int i=0;i<email.length();i++){
            if(email.charAt(i) == '@'){
                flag=1;
            } else if(email.charAt(i) == '.' && flag==1){
                flag=2;
            } else if((flag==2 && (int)email.charAt(i) >= 97 && (int)email.charAt(i) <= 122)){
                f=1;
            } else if(f==1){
                return false;
            }
        }
        if(f==1){
            return true;
        } else{
            return false;
        }
    }

    @Override
    protected void onStart() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            sendToMain();
        }
        super.onStart();
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void sendToSetup() {
        Intent setUpIntent = new Intent(RegisterActivity.this, SetupActivity.class);
        startActivity(setUpIntent);
        finish();
    }
}