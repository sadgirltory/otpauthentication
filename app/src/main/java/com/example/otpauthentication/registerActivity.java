package com.example.otpauthentication;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class registerActivity extends AppCompatActivity {

    FirebaseAuth fauth;
    EditText phoneNumber,codEnter;
    Button nextbtn;
    ProgressBar progressBar;
    TextView state;
    CountryCodePicker codepicker;
    String  VerficationId;
    Boolean verficationInProgress = false;
    PhoneAuthProvider.ForceResendingToken Token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fauth = FirebaseAuth.getInstance();
        phoneNumber = findViewById(R.id.phone);
        codEnter = findViewById(R.id.codeEnter);
        progressBar = findViewById(R.id.progressBar);
        nextbtn = findViewById(R.id.nextBtn);
        state = findViewById(R.id.state);
        codepicker  = findViewById(R.id.ccp);

        
        
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verficationInProgress){
                    if(!phoneNumber.getText().toString().isEmpty() && phoneNumber.getText().toString().length() == 11){
                        String phonenum = "+"+ codepicker.getSelectedCountryCode()+phoneNumber.getText().toString();
                        Log.d("TAG","onClick:  Phone number"+ phonenum);
                        progressBar.setVisibility(View.VISIBLE);
                        state.setText("Sending OTP.....");
                        state.setVisibility(View.VISIBLE);
                        nextbtn.setEnabled(false);
                        requestOTP(phonenum);


                    }
                    else{
                        phoneNumber.setError("phone number is not valid");
                    }
                }else{
                    String userOtp = codEnter.getText().toString();
                    if(!userOtp.isEmpty() && userOtp.length() == 6){
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerficationId,userOtp);
                        verfyAuth(credential);

                    }else {
                        codEnter.setError("Valid OTP is required");
                    }
                }
            }
        });
    }

    private void verfyAuth(PhoneAuthCredential credential) {
        fauth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(registerActivity.this, "Authentication is succesful.", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(registerActivity.this, "Authentication is failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestOTP(String phonenum) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressBar.setVisibility(View.GONE);

                state.setVisibility(View.GONE);
                codEnter.setVisibility(View.VISIBLE);
                VerficationId = s;
                Token = forceResendingToken;
                nextbtn.setText("Verfiy");
                nextbtn.setEnabled(false);
                verficationInProgress = true;

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(registerActivity.this, "Cannot create an acount"+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
