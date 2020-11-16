package project.days.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

public class ProfileSelectActivity extends AppCompatActivity {

    private CircleImageView imageView;
    private TextView resText;
    private ImageView tickImage;
    public Uri imageUri;
    private Task<Uri> downloadUrl;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private StorageReference storageReference,filePath;
    private Button goodtogoButton;
    String currentUserID;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_select);
        imageView = findViewById(R.id.profile_image_select);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.pr_img));
        resText = findViewById(R.id.txtt1);
        tickImage = findViewById(R.id.tick_icon);
        goodtogoButton = findViewById(R.id.good_to_go_button);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        storageReference = FirebaseStorage.getInstance().getReference("Profile Images");
        mDialog = new ProgressDialog(this);



        goodtogoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri != null)
                    uploadPicture();
                else
                    Toast.makeText(ProfileSelectActivity.this, "We would love to see you :)", Toast.LENGTH_SHORT).show();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });
    }
    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,10);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== 10 && resultCode==RESULT_OK && data!= null && data.getData()!=null)
        {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            resText.setVisibility(View.VISIBLE);
            tickImage.setVisibility(View.VISIBLE);
            goodtogoButton.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.green));
        }
    }
    private void uploadPicture() {

        mDialog.setTitle("Please wait");
        mDialog.setMessage("We are setting up your profile");
        mDialog.show();
        filePath = storageReference.child(currentUserID + ".jpg");
        filePath.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                        usersRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists())
                                {
                                    HashMap hashMap = new HashMap();
                                    hashMap.put("profile_image",downloadUrl.toString());
                                    final ProgressDialog mDialog = new ProgressDialog(getApplicationContext());

                                    usersRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful())
                                            {
                                                mDialog.hide();
                                                Intent mainIntent = new Intent(ProfileSelectActivity.this, MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(ProfileSelectActivity.this, "Error occured. " + message, Toast.LENGTH_SHORT).show();
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
}