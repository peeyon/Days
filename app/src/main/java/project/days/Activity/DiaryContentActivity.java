package project.days.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import project.days.R;


public class DiaryContentActivity extends AppCompatActivity {

    EditText DiaryNameTV, DiaryContentET;
    CircleImageView EditButton, DeleteButton;
    ImageView BackButton,imageView;
    FirebaseAuth mAuth;
    DatabaseReference diariesRef, usersRef;
    String currentUserID, type, diary_id;
    private Uri filePath;
    Button SaveButton,Photo,upload;
    private StorageReference storageReference;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_diary_content);
    mAuth = FirebaseAuth.getInstance();
    usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    storageReference = FirebaseStorage.getInstance().getReference("image");
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
    DiaryContentET = (EditText) findViewById(R.id.diary_content_text);
    SaveButton = (Button) findViewById(R.id.save_changes_button);
    Photo = (Button) findViewById(R.id.content_add_photos);
    imageView = (ImageView) findViewById(R.id.myImage);
    upload = (Button) findViewById(R.id.content_add_videos);

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

    Photo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            chooseImage();
        }
    });
    upload.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            UploadImage();
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
            HashMap hashMap = new HashMap();
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
        filePath = data.getData();
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
    private void UploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(DiaryContentActivity.this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            StorageReference reference = storageReference.child("image/" + UUID.randomUUID().toString());
            reference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(DiaryContentActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded" + (int) progress + "%");

                }
            });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), 1);
    }


}