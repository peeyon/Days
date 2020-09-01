package project.days;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String email, password;
    private EditText EmailET, PasswordET;
    private Button LoginButton;
    private TextView ForgotLink,tvLogin;
    private ImageButton signupLink;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        EmailET = (EditText) findViewById(R.id.email_et);
        PasswordET = (EditText) findViewById(R.id.password_et);
        LoginButton = (Button) findViewById(R.id.login_button);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        ForgotLink = (TextView) findViewById(R.id.tvForgot);
        signupLink = (ImageButton) findViewById(R.id.btRegister);



        LoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                email = EmailET.getText().toString();
                password = PasswordET.getText().toString();
                if(!(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)))
                {
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(LoginActivity.this, "Authenticated successfully", Toast.LENGTH_SHORT).show();
                                        usersRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
                                        usersRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists())
                                                {
                                                    Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);
                                                }
                                                else
                                                {
                                                    Intent mainIntent = new Intent(LoginActivity.this,PersonalDetailsActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                    else
                                    {
                                        String message = task.getException().getMessage().toString();
                                        Toast.makeText(LoginActivity.this, "Error occured. "+message, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Please fill all the credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ForgotLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                email = EmailET.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(LoginActivity.this, "Please provide your email address to reset your password", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this, "Password reset link has been sent to your inbox.", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message = task.getException().getMessage().toString();
                                Toast.makeText(LoginActivity.this, "Error occured. "+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupIntent =  new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(signupIntent);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onClick(View v) {
        if (v==LoginButton){
            Intent intent   = new Intent(LoginActivity.this,SignupActivity.class);
            Pair[] pairs    = new Pair[1];
            pairs[0] = new Pair<View,String>(tvLogin,"tvLogin");
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,pairs);
            startActivity(intent,activityOptions.toBundle());
        }
    }
}