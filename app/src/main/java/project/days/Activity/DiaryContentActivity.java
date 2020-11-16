package project.days.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import project.days.R;


public class DiaryContentActivity extends AppCompatActivity {

    EditText DiaryNameTV, DiaryContentET;
    CircleImageView EditButton, DeleteButton;
    ImageView BackButton,imageView;
    FirebaseAuth mAuth;
    Uri imageUri;
    DatabaseReference diariesRef, usersRef;
    String currentUserID, type, diary_id,imageUrl;
    Task<Uri> durl;
    Button SaveButton,Photo;
    private CardView cardView;
    private StorageReference storageReference, filePath;
    ProgressDialog mDialog;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_diary_content);
    mAuth = FirebaseAuth.getInstance();
    usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    storageReference = FirebaseStorage.getInstance().getReference("image");
    currentUserID = mAuth.getCurrentUser().getUid();
    type = getIntent().getStringExtra("type");
    mDialog = new ProgressDialog(this);
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
    DiaryContentET = (EditText) findViewById(R.id.diary_content_text);
    cardView = (CardView) findViewById(R.id.cview_1);
    SaveButton = (Button) findViewById(R.id.save_changes_button);
    Photo = (Button) findViewById(R.id.content_add_photos);
    imageView = (ImageView) findViewById(R.id.image_view_card_imview);

    diariesRef.child(diary_id).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists())
            {
                String name = snapshot.child("name").getValue().toString();
                String content = null;
                if (snapshot.child("content").exists())
                     content = snapshot.child("content").getValue().toString();
                if (snapshot.child("image").exists()) {
                    cardView.setVisibility(View.VISIBLE);
                    storageReference.child(currentUserID + diary_id + ".jpg").getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageView.setImageURI(uri);
                                }
                            });
                }
                if (content != null)
                    DiaryContentET.setText(content);
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
                HashMap<String, Object> hashMap = new HashMap<>();
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

    Photo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            chooseImage();
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

    SaveButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UploadImage();
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("name",DiaryNameTV.getText().toString());
            hashMap.put("content",DiaryContentET.getText().toString());
            diariesRef.child(diary_id).updateChildren(hashMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(DiaryContentActivity.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }
                    });
        }
    });


}
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getData()!= null)
    {
        cardView.setVisibility(View.VISIBLE);
        imageUri = data.getData();
        imageView.setImageURI(imageUri);
    }
}
    private void UploadImage() {
        mDialog.setTitle("Please wait");
        mDialog.setMessage("We are setting up your profile");
        mDialog.show();
        filePath = storageReference.child(currentUserID + diary_id + ".jpg");
        filePath.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        durl = taskSnapshot.getStorage().getDownloadUrl();
                        diariesRef.child(diary_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists())
                                {
                                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                    hashMap.put("image",durl.toString());
                                    final ProgressDialog mDialog = new ProgressDialog(getApplicationContext());

                                    diariesRef.child(diary_id).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful())
                                            {
                                                mDialog.hide();
                                                Intent mainIntent = new Intent(DiaryContentActivity.this, MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(DiaryContentActivity.this, "Error occured. " + message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), 1);
    }


}