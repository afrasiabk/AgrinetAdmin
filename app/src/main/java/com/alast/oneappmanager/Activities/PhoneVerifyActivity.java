package com.alast.oneappmanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.alast.oneappmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.concurrent.TimeUnit;

public class PhoneVerifyActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText phone_input, code_input;
    private TextView textView, login_btn, resend_btn;
    private String codeSent;
    private String authPhoneNumber;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);
        backBtn();
        assignViews();
        listeners();
    }

    private void assignViews() {
        progressBar = (ProgressBar) findViewById(R.id.phone_verify_pb);
        login_btn = (TextView) findViewById(R.id.phone_login_btn);
        textView = (TextView) findViewById(R.id.phone_verify_text);
        phone_input = (EditText) findViewById(R.id.phone_verify_phn);
        code_input = (EditText) findViewById(R.id.phone_verify_code);
        resend_btn = (TextView) findViewById(R.id.pv_resend);
        Picasso.get().load(R.drawable.logo_icon).fit().centerCrop().into((ImageView) findViewById(R.id.phone_verify_icon));
    }

    private void listeners() {
            screens("phone"); //first time
            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (login_btn.getText().equals("Login")){
                        checkNumber();
                    }
                    else if (login_btn.getText().equals("Verify")){
                        verifySignInCode();
                    }
                }
            });
            resend_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    regenerateCode();
                }
            });
    }

    //calls check_register
    private void checkNumber(){

        String phone = phone_input.getText().toString();

        if(phone.isEmpty()){
            phone_input.setError("Phone number is required");
            phone_input.requestFocus();
            return;
        }

        if ((phone.length() != 11 )&&(phone.length() != 13 )){
            phone_input.setError("Please enter a valid phone");
            phone_input.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        textView.setText("Checking Credentials");

        if (phone.substring(0,3).equals("+92")) {
            authPhoneNumber = phone;
            check_register();
        }
        else{
            authPhoneNumber = "+92" + phone.substring(1, 11);
            check_register();
        }
    }

    //calls generateCode()
    private void check_register() {
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference("Managers");
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(authPhoneNumber).exists()){
                    generateCode();
                }
                else{
                    Toast.makeText(PhoneVerifyActivity.this, "This number is not registered", Toast.LENGTH_SHORT).show();
                    recreate();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PhoneVerifyActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //sets callbacks
    private void generateCode() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                authPhoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    //sets callbacks
    private void regenerateCode() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                authPhoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,
                resendToken);        // OnVerificationStateChangedCallbacks
    }

    //auto verification ->calls auth_sign_in
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            screens("code");
            codeSent = s;
            resendToken = forceResendingToken;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            auth_sign_in(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            screens("phone");

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                textView.setText("invalid phone number");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                textView.setText("verification attempts exceeded\ntry again later");
            }
            else{
                textView.setText("Failed\ntry again later");
            }

        }

        @Override
        public void onCodeAutoRetrievalTimeOut(String s) {
            if (login_btn.getText().equals("Verify")) {
                textView.setText("Timeout\nEnter code manually.");
                progressBar.setVisibility(View.INVISIBLE);
            }
            super.onCodeAutoRetrievalTimeOut(s);
        }
    };

    //manual verification ->calls auth_sign_in
    private void verifySignInCode(){
        if(code_input.getText().toString().isEmpty()){
            code_input.setError("Code is required");
            code_input.requestFocus();
            return;
        }
        textView.setText("Verifying code");
        progressBar.setVisibility(View.VISIBLE);
        auth_sign_in(PhoneAuthProvider.getCredential(codeSent, code_input.getText().toString()));
    }

    //auth signed in if code credential in correct ->calls login()
    private void auth_sign_in(final PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            login();
                        }
                        else{
                            code_input.getText().clear();
                        }
                    }
                });
    }

    private void login() {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(PhoneVerifyActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PhoneVerifyActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void screens(String type){
        if (type.equals("phone")){
            progressBar.setVisibility(View.INVISIBLE);
            login_btn.setText("Login");
            textView.setText("login with phone number");
            phone_input.setVisibility(View.VISIBLE);
            code_input.setVisibility(View.INVISIBLE);
            resend_btn.setVisibility(View.INVISIBLE);
        }
        else if (type.equals("code")){
            progressBar.setVisibility(View.VISIBLE);
            login_btn.setText("Verify");
            textView.setText("Automatic Verification in Progress\nYou  may enter the code manually");
            phone_input.setVisibility(View.INVISIBLE);
            code_input.setVisibility(View.VISIBLE);
            resend_btn.setVisibility(View.VISIBLE);
        }

        else{
            if (getSupportActionBar()!=null) {
                getSupportActionBar().setTitle("Login");
            }
            screens("phone");
            authPhoneNumber = "";
            code_input.getText().clear();
            codeSent = "";
        }
    }

    @Override
    public void onBackPressed() {
        if (!login_btn.getText().equals("Login")){
            screens("fresh");
            return;
        }
        Intent intent = new Intent(PhoneVerifyActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void backBtn() {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Login");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
