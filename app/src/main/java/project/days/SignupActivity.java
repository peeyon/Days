package project.days;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    private RelativeLayout rlayout;
    private Animation animation;
    private EditText EmailET, PasswordET, RePasswordET;
    private Button SignupButton;
    private FirebaseAuth mAuth;
    String email, password, re_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = findViewById(R.id.bgHeader);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rlayout = findViewById(R.id.rlayout);
        animation = AnimationUtils.loadAnimation(this,R.anim.uptodowndiagonal);
        rlayout.setAnimation(animation);

        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.spin_kit_reg);
        Sprite doubleBounce = new FoldingCube();
        progressBar.setIndeterminateDrawable(doubleBounce);

        EmailET = (EditText) findViewById(R.id.signup_email);
        PasswordET = (EditText) findViewById(R.id.signup_password);
        RePasswordET = (EditText) findViewById(R.id.signup_confirm_password);
        SignupButton = (Button) findViewById(R.id.signupButton);

        mAuth = FirebaseAuth.getInstance();

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = EmailET.getText().toString();
                password = PasswordET.getText().toString();
                re_password = RePasswordET.getText().toString();

                if(!(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)
                        &&TextUtils.isEmpty(re_password) ))
                {
                    if(password.equals(re_password))
                    {
                        mAuth.createUserWithEmailAndPassword(email,password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful())
                                        {
                                            SignupButton.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.green));
                                            progressBar.setVisibility(View.VISIBLE);
                                            Toast.makeText(SignupActivity.this, "Great! You're one among us now", Toast.LENGTH_SHORT).show();
                                            Intent mainIntent = new Intent(SignupActivity.this, PersonalDetailsActivity.class);
                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(mainIntent);
                                        }
                                        else
                                        {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(SignupActivity.this, "Error occured. "+message, Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                    }
                }
                else
                {
                    Toast.makeText(SignupActivity.this, "All the fields are mandatory", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}