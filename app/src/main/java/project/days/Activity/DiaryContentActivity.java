package project.days;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class DiaryContentActivity extends AppCompatActivity {

    private EditText Diaryheading;
    private FirebaseAuth mAuth;
    private DatabaseReference postRef;
    String currentUserID,diary_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_content);
        Diaryheading = (EditText) findViewById(R.id.content_diary_name);
    }
}