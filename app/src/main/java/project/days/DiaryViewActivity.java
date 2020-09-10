package project.days;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextClock;
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
import com.google.firebase.database.annotations.NotNull;

import org.w3c.dom.Text;

import java.util.HashMap;

import static java.security.AccessController.getContext;

public class DiaryViewActivity extends AppCompatActivity {

    private TextView AlertText;
    private FirebaseAuth mAuth;
    private DatabaseReference diaryReference;
    private ImageView backBtn;
    private String currentUserID,type;
    private GridLayout gridLayout;
    private Button createButton;
    private TextView createText;
    String tt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_view);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        backBtn = (ImageView) findViewById(R.id.bck_arrow_icon_dmain);
        createButton = (Button) findViewById(R.id.create_diary_button);
        createText = (TextView) findViewById(R.id.create_diary_text);
        gridLayout = (GridLayout) findViewById(R.id.grid_dmain);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(DiaryViewActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }
        });
        currentUserID = mAuth.getCurrentUser().getUid();

        type =  getIntent().getStringExtra("type");
        diaryReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
            if (type.equals("personal"))
                tt = "Private Diaries";
            if (type.equals("group"))
                tt = "Shared Diaries";

        AlertText = (TextView) findViewById(R.id.visibility_notice);

        diaryReference.child(tt).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                {
                    AlertText.setText("Oops..! We can't find any diaries of you..!");
                    AlertText.setVisibility(View.VISIBLE);
                    createButton.setVisibility(View.VISIBLE);
                    createText.setVisibility(View.VISIBLE);
                }
                else
                {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        createButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(DiaryViewActivity.this);

                View viewInflated = LayoutInflater.from(DiaryViewActivity.this).inflate(R.layout.create_diary_layout,null,false);
                builder.setView(viewInflated);
                final EditText nameET = (EditText) viewInflated.findViewById(R.id.crt_dry_name);

                Button crt_btn = (Button) viewInflated.findViewById(R.id.crt_dry_btn);
                builder.show();
                crt_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = nameET.getText().toString();

                        if (TextUtils.isEmpty(name))
                        {
                            Toast.makeText(DiaryViewActivity.this, "It would be easy if the diary is named", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            HashMap hashMap = new HashMap();
                            hashMap.put("text","Sample Test");

                            diaryReference.child(tt).child(name).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(DiaryViewActivity.this, "Diary created successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        String msg = task.getException().getMessage();
                                        Toast.makeText(DiaryViewActivity.this, "Error. " + msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                    }
                });
            }
        });

    }
}