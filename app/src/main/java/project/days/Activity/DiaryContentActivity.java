package project.days.Activity;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import project.days.R;

public class DiaryContentActivity extends AppCompatActivity {

    private EditText Diaryheading;
    private FirebaseAuth mAuth;
    private DatabaseReference postRef;
    String currentUserID,diary_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_content);
        Diaryheading = findViewById(R.id.content_diary_name);
    }
}