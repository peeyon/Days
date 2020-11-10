package project.days.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import project.days.R;


public class DiaryContentActivity extends AppCompatActivity {

    EditText DiaryNameTV;
    CircleImageView EditButton, DeleteButton;
    ImageView BackButton;
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
    if (type.equals("Private Diaries")) {
        diariesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Private Diaries");
    }
    else {
        diariesRef = FirebaseDatabase.getInstance().getReference().child("Shared Diaries");
    }
    DiaryNameTV = (EditText) findViewById(R.id.content_diary_name);;
    EditButton = (CircleImageView) findViewById(R.id.content_edit_icon);
    BackButton = (ImageView) findViewById(R.id.content_view_back_icon);
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

    EditButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DiaryNameTV.setEnabled(true);
            DiaryNameTV.requestFocus();
        }
    });

    DiaryNameTV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus)
            {
                HashMap hashMap = new HashMap();
                hashMap.put("name",DiaryNameTV.getText().toString());
                diariesRef.child(diary_id).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(DiaryContentActivity.this, "Renamed successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String msg = task.getException().getMessage();
                            Toast.makeText(DiaryContentActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }
    });

    BackButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    });

    DeleteButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            diariesRef.child(diary_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(DiaryContentActivity.this, "Diary deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
            });
        }
    });

}

}