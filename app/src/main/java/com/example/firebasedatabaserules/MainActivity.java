package com.example.firebasedatabaserules;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.username_et)
    EditText editTextUsername;

    @BindView(R.id.password_et)
    EditText editTextPassword;

    @BindView(R.id.data_et)
    EditText editTextData;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser != null) {

                    // User is signed in
                    Log.d("FIREBASE_DATABASE_RULES", "onAuthStateChanged:signed_in:" + firebaseUser.getUid());

                } else {

                    // User is signed out
                    Log.d("FIREBASE_DATABASE_RULES", "onAuthStateChanged:signed_out");
                }
            }
        };

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {

            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @OnClick(R.id.login_button)
    public void login() {

        firebaseAuth.signInWithEmailAndPassword(editTextUsername.getText().toString(), editTextPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d("signInWithEmail", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            Log.w("signInWithEmail", "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, "Auth Failed", Toast.LENGTH_SHORT).show();
                            
                        } else {

                            Toast.makeText(MainActivity.this, "You are Looged In!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @OnClick(R.id.signup_button)
    public void signup() {

        firebaseAuth.createUserWithEmailAndPassword(editTextUsername.getText().toString(), editTextPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d("createUserWithEmail", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign up fails, display a message to the user. If sign up succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed up user can be handled in the listener.

                        if (!task.isSuccessful()) {

                            Toast.makeText(MainActivity.this, "Signup Auth Failed", Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(MainActivity.this, "Signup Success! Try to Login!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @OnClick(R.id.logout_button)
    public void logout() {

        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logged Out!", Toast.LENGTH_SHORT).show();

    }

    @OnClick(R.id.save_button)
    public void save() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("message");

        databaseReference.setValue(editTextData.getText().toString());
    }

}