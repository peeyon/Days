package project.days.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

//import project.days.DiaryContentActivity;
import project.days.Models.Diaries;
import project.days.R;
import project.days.ViewHolders.DiariesViewHolder;


public class DiaryViewActivity extends AppCompatActivity {

    private TextView AlertText;
    private FirebaseAuth mAuth;
    private DatabaseReference diaryReference,usersReference;
    private ImageView backBtn;
    private String currentUserID,type;
    private RecyclerView recyclerLayout;
    private Button createButton;
    private TextView createText;
    FirebaseRecyclerOptions<Diaries> options;
    FirebaseRecyclerAdapter<Diaries, DiariesViewHolder> adapter;
    public String tt = null;
    String post_key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        gridLayoutManager.setReverseLayout(true);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersReference =  FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        type =  getIntent().getStringExtra("type");
        diaryReference = FirebaseDatabase.getInstance().getReference().child("Diaries");
        if (type.equals("personal")) {
            tt = "Private Diaries";
            diaryReference = usersReference.child("Private Diaries");
        }
        if (type.equals("group")) {
            tt = "Shared Diaries";
            diaryReference = FirebaseDatabase.getInstance().getReference().child("Shared Diaries");
        }
        backBtn = findViewById(R.id.bck_arrow_icon_dmain);
        createButton = findViewById(R.id.create_diary_button);
        createText = findViewById(R.id.create_diary_text);
        recyclerLayout = findViewById(R.id.grid_dmain);
        recyclerLayout.setLayoutManager(gridLayoutManager);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(DiaryViewActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }
        });
        currentUserID = mAuth.getCurrentUser().getUid();



        AlertText = findViewById(R.id.visibility_notice);


        usersReference.child(tt).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                {
                    AlertText.setText("Oops..! We can't find any diaries of you..!");
                    AlertText.setVisibility(View.VISIBLE);
                    createButton.setVisibility(View.VISIBLE);
                    createText.setVisibility(View.VISIBLE);
                    recyclerLayout.setVisibility(View.GONE);
                }
                else
                {

                    AlertText.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        options = new FirebaseRecyclerOptions.Builder<Diaries>()
                .setQuery(diaryReference,Diaries.class).build();

        adapter = new FirebaseRecyclerAdapter<Diaries, DiariesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DiariesViewHolder holder, int position, @NonNull Diaries model) {
                holder.txt.setText(model.getDiary_name());
                // position += 1;
                post_key = getRef(position).getKey();
            }

            @NonNull
            @Override
            public DiariesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_view_layout,parent,false);
                Toast.makeText(DiaryViewActivity.this, post_key, Toast.LENGTH_SHORT).show();
                usersReference.child(tt).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.hasChild(post_key))
                        {
                            view.setVisibility(View.GONE);
                            AlertText.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            AlertText.setVisibility(View.GONE);
                            view.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent dIntent = new Intent(DiaryViewActivity.this, DiaryContentActivity.class);
                        dIntent.putExtra("diary_id",post_key);
                        dIntent.putExtra("type",tt);
                        startActivity(dIntent);
                    }
                });
                return new DiariesViewHolder(view);
            }
        };
        recyclerLayout.setLayoutManager(gridLayoutManager);
        recyclerLayout.setAdapter(adapter);
        adapter.startListening();

        createButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(DiaryViewActivity.this);

                View viewInflated = LayoutInflater.from(DiaryViewActivity.this).inflate(R.layout.create_diary_layout,null,false);
                builder.setView(viewInflated);
                final EditText nameET = viewInflated.findViewById(R.id.crt_dry_name);

                Button crt_btn = viewInflated.findViewById(R.id.crt_dry_btn);
                builder.show();
                crt_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String name = nameET.getText().toString();

                        if (TextUtils.isEmpty(name))
                        {
                            Toast.makeText(DiaryViewActivity.this, "It would be easy if the diary is named", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("name",name);

                            diaryReference.child(name + currentUserID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())
                                    {
                                        if (tt.equals("Private Diaries"))
                                        {
                                            HashMap hmap = new HashMap();
                                            hmap.put("name", name);
                                            usersReference.child(tt).child(name+currentUserID).updateChildren(hmap);
                                            Toast.makeText(DiaryViewActivity.this, "Diary created successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                        else {
                                            HashMap hmap = new HashMap();
                                            hmap.put("diary_id", name + currentUserID);
                                            usersReference.child(tt).child(name + currentUserID).updateChildren(hmap);
                                            Toast.makeText(DiaryViewActivity.this, "Diary created successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
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

    @Override
    protected void onStart() {
        super.onStart();


        if (adapter!=null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!= null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter!=null)
            adapter.startListening();
    }
}
