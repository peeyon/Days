package project.days.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import project.days.R;


public class DiaryContentActivity extends AppCompatActivity {

    EditText DiaryNameTV;
    CircleImageView EditButton, DeleteButton;
    FirebaseAuth mAuth;
    DatabaseReference diariesRef, usersRef;
    String currentUserID, type, diary_id;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_diary_content);
    mAuth = FirebaseAuth.getInstance();
    usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    currentUserID = mAuth.getCurrentUser().getUid();
    type = getIntent().getStringExtra("type");
    diary_id = getIntent().getStringExtra("diary_id");
    diariesRef = FirebaseDatabase.getInstance().getReference().child("Diaries");

    DiaryNameTV = (EditText) findViewById(R.id.content_diary_name);;
    EditButton = (CircleImageView) findViewById(R.id.content_edit_icon);
    DeleteButton = (CircleImageView) findViewById(R.id.content_delete_icon);

    diariesRef.child(diary_id).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists())
            {
                String name = snapshot.child("name").getValue().toString();
                DiaryNameTV.setText(name);
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });



}

}