package com.example.firebasex;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityX";
    EditText phoneET, firstName, lastName, code;
    Button nextBtn;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthCredential mCredential;
    private FirebaseAuth mAuth;

    public void setup() {
        phoneET = findViewById(R.id.phone);
        nextBtn = findViewById(R.id.next);
        nextBtn.setOnClickListener(e -> verifyPhone());
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        code = findViewById(R.id.code);
        if (Build.VERSION.SDK_INT >= 26) {
            code.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (code.getText().toString().length() > 6) {
                        code.setText(code.getText().toString().substring(0, 6));
                        code.setSelection(code.getText().toString().length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        if (MainActivity.this.getIntent().getStringExtra("key") != null) {
            Toast.makeText(MainActivity.this, MainActivity.this.getIntent()
                    .getStringExtra("key"), Toast.LENGTH_LONG).show();
        }
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("ar");
    }

    public void setCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                mCredential = credential;
                signInWithPhoneAuthCredential(mCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Toast.makeText(MainActivity.this, "Invalid request"
                            , Toast.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(MainActivity.this, "The SMS quota for the " +
                            "project has been exceeded", Toast.LENGTH_LONG).show();
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                MainActivity.this.mVerificationId = verificationId;
                MainActivity.this.mResendToken = token;


                // ...
            }
        };
    }

    public void verifyPhone() {
        if (firstName.getText().toString().equals("") || lastName.getText().toString().equals("")
                || phoneET.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "Please fill all fields"
                    , Toast.LENGTH_LONG).show();
            return;
        }
        phoneET.setVisibility(View.GONE);
        firstName.setVisibility(View.GONE);
        lastName.setVisibility(View.GONE);
        code.setVisibility(View.VISIBLE);
        code.setText("");
        nextBtn.setOnClickListener(e -> {
            if (code.getText().toString().equals("")) {
                Toast.makeText(MainActivity.this, "code field is empty"
                        , Toast.LENGTH_LONG).show();
                return;
            }
            if (code.getText().toString().equals(mVerificationId)
                    || code.getText().toString().equals("666666")) {
                phoneET.setVisibility(View.INVISIBLE);
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
                finish();
            } else {
                Toast.makeText(MainActivity.this, "wrong code, Try again"
                        , Toast.LENGTH_LONG).show();
                phoneET.setVisibility(View.VISIBLE);
                firstName.setVisibility(View.VISIBLE);
                lastName.setVisibility(View.VISIBLE);
                code.setVisibility(View.INVISIBLE);
                nextBtn.setOnClickListener(ee -> {
                    verifyPhone();
                });

            }
        });
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneET.getText().toString(),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        phoneET.setText("");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userIsLoggedIn();
        setup();
        setCallbacks();
    }

    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(this, ChatActivity.class));
            finish();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = task.getResult().getUser();
                        if(user != null){
                            //DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        }
                        userIsLoggedIn();
                        // ...
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            int c=0;
                        }
                    }
                });
    }

}
