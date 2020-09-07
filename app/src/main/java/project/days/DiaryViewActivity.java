package project.days;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class DiaryViewActivity extends AppCompatActivity {

    private TextView AlertText;
    private FirebaseAuth mAuth;
    private DatabaseReference diaryReference;
    private ImageView backBtn;
    private String currentUserID,type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_view);
        mAuth = FirebaseAuth.getInstance();
        backBtn = (ImageView) findViewById(R.id.bck_arrow_icon_dmain);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(DiaryViewActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }
        });
        currentUserID = mAuth.getCurrentUser().getUid();
        Intent getter = new Intent();
        type = getter.getStringExtra("type");
        if (type.equals("personal"))
            diaryReference = FirebaseDatabase.getInstance().getReference("Users").child("Private Diaries");
        if(type.equals("group"))
            diaryReference = FirebaseDatabase.getInstance().getReference("Users").child("Shared Diaries");
        AlertText = (TextView) findViewById(R.id.visibility_notice);

        diaryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                {
                    AlertText.setText("Oops..! We can't find any diaries of you..!");
                    AlertText.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}