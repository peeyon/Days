package project.days.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import project.days.R;


public class Verifyemail extends AppCompatActivity {
    private Button verifybutton;
    private TextView verifymsg;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verifyemail);
        mAuth = FirebaseAuth.getInstance();
        verifybutton = findViewById(R.id.verify_button);
        verifymsg =  findViewById(R.id.verify_msg);
        final FirebaseUser user = mAuth.getCurrentUser();
        if(user.isEmailVerified())
        {
            Toast.makeText(Verifyemail.this, "Great! You're one among us now", Toast.LENGTH_SHORT).show();
            Intent mainIntent = new Intent(Verifyemail.this, PersonalDetailsActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        }
        else
        {
            verifybutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>(){
                        @Override
                        public void onSuccess(Void aVoid){
                            Toast.makeText(Verifyemail.this,"Verification Link Has Been Sent",Toast.LENGTH_SHORT).show();
                            Intent mainIntent = new Intent(Verifyemail.this, PersonalDetailsActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Verifyemail.this, "Email not sent. "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            verifybutton.setText("Resend");
                            verifymsg.setText("Click Resend button to sent the verification link again");
                        }
                    });
                }
            });
        }
    }
}
