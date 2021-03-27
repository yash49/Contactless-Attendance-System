package com.rte.contactless_attendance_admin;

import android.Manifest;
import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeActivity extends AppCompatActivity {

    LinearLayout loginContainer, signupContainer;
    Button loginBtn, signupBtn;
    EditText signupEmail,signupName,signupPassword,loginEmail,loginPassword;

    boolean switchContainer = false;
    FirebaseAuth authHandler;
    FirebaseUser user = null;

    @Override
    protected void onStart() {
        super.onStart();
        if (authHandler.getCurrentUser() != null && authHandler.getCurrentUser().isEmailVerified()) {
            user = authHandler.getCurrentUser();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if(ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
        }
        authHandler = FirebaseAuth.getInstance();
        loginContainer = findViewById(R.id.login_container);
        signupContainer = findViewById(R.id.signup_container);

        signupBtn = findViewById(R.id.signup_btn);
        loginBtn = findViewById(R.id.login_btn);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        final FloatingActionButton signupToggle = findViewById(R.id.signup_toggle);

        signupToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout container = null;
                if(!switchContainer){
                    loginContainer.setVisibility(View.GONE);
                    signupContainer.setVisibility(View.INVISIBLE);
                    container = signupContainer;
                    signupToggle.setImageDrawable(getResources().getDrawable(R.drawable.close));
                }
                else{
                    signupContainer.setVisibility(View.GONE);
                    container = loginContainer;
                    signupToggle.setImageDrawable(getResources().getDrawable(R.drawable.add_person));
                }
                container.setVisibility(View.VISIBLE);
                switchContainer = !switchContainer;

                int dx = container.getWidth();
                int dy =  container.getHeight();
                float finalRadius = (float) Math.hypot(dx, dy);

                Animator animator =
                        null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    animator = ViewAnimationUtils.createCircularReveal(container, container.getRight(), container.getTop(), 0, finalRadius);
                }
                if(animator != null){
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    container.setVisibility(View.VISIBLE);
                    animator.setDuration(1000);
                    animator.start();
                }
                else{
                    container.setVisibility(View.VISIBLE);
                }
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(signupEmail.getText().toString().trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(signupEmail.getText().toString()).matches()){
                    signupEmail.setError("Please enter email address properly");
                    return;
                }
                if(signupName.getText().toString().trim().length() == 0){
                    signupName.setError("Please enter your name properly");
                    return;
                }
                if(signupPassword.getText().toString().isEmpty() || signupPassword.getText().toString().length() < 6){
                    signupPassword.setError("Password must be atleast 6 characters long!");
                    return;
                }
                authHandler.createUserWithEmailAndPassword(signupEmail.getText().toString(),
                                                            signupPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                             public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    user = authHandler.getCurrentUser();
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> vtask) {
                                                    if (vtask.isSuccessful()) {
                                                        Toast.makeText(HomeActivity.this, "Email verification is sent to your email address!",Toast.LENGTH_LONG).show();
                                                        signupName.setText("");
                                                        signupEmail.setText("");
                                                        signupPassword.setText("");
                                                    }
                                                }});

                                } else {
                                    Toast.makeText(HomeActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginEmail.getText().toString().trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(loginEmail.getText().toString()).matches()){
                    loginEmail.setError("Please enter email address properly");
                    return;
                }
                if(loginPassword.getText().toString().trim().length() == 0){
                    loginPassword.setError("Password must be atleast 6 characters long!");
                    return;
                }
                authHandler.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    user = authHandler.getCurrentUser();
                                    startActivity(new Intent(HomeActivity.this,MainActivity.class));
                                }
                                else{
                                    Toast.makeText(HomeActivity.this,"Wrong credentials! try again!",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 100 && requestCode != 101) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contactless Attendance")
                .setMessage("We need storage permission! please grant from settings.")
                .setPositiveButton("OK", listener)
                .show();
    }

}
