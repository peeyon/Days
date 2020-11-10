package project.days.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import project.days.Models.Friends;
import project.days.R;
import project.days.ViewHolders.FriendsViewHolder;

public class AddFriendsActivity extends AppCompatActivity {
    private EditText searchET;
    private CircleImageView searchButton;
    private FirebaseAuth mAuth;
    private DatabaseReference friendsRef;
    private RecyclerView recyclerView;
    private FirebaseRecyclerOptions<Friends> options;
    FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter;
    String person_ref, currentUserID,name,nickname,gender,dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Users");
        searchET = (EditText) findViewById(R.id.add_friends_et);
        searchButton = (CircleImageView) findViewById(R.id.add_friends_search_btn);
        recyclerView = (RecyclerView) findViewById(R.id.add_friends_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String searchWord = searchET.getText().toString();
                if (TextUtils.isEmpty(searchWord))
                {
                    Toast.makeText(AddFriendsActivity.this, "Please provide some keywords to search for", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(AddFriendsActivity.this, "Searching", Toast.LENGTH_SHORT).show();
                    Query query = friendsRef.orderByChild("Name").startAt(searchWord).endAt(searchWord + "\uf8ff");

                    options = new FirebaseRecyclerOptions.Builder<Friends>()
                            .setQuery(query,Friends.class).build();

                    firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull Friends model) {
                            person_ref = getRef(position).getKey();
                            holder.setName(model.getName());
                            name = model.getName();
                            gender = model.getGender();
                            dob = model.getDOB();
                            nickname = model.getNickname();
                        }


                        @NonNull
                        @Override
                        public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_view_layout,parent,false);
                            final ImageView addButton = (ImageView) view.findViewById(R.id.add_him_button);
                            addButton.setOnClickListener(new View.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                @Override
                                public void onClick(View v) {
                                    final HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("Name",name);
                                    hashMap.put("Gender",gender);
                                    hashMap.put("Nickname",nickname);
                                    hashMap.put("DOB",dob);
                                    friendsRef.child(person_ref).child("Friends").child(currentUserID)
                                            .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful())
                                            {
                                                friendsRef.child(currentUserID).child("Friends").child(person_ref)
                                                        .updateChildren(hashMap);
                                                addButton.setImageResource(R.drawable.sent_icon);
                                                addButton.setImageTintList(getApplicationContext().getResources().getColorStateList(R.color.green));
                                            }
                                            else
                                            {
                                                String msg = task.getException().getMessage();
                                                Toast.makeText(AddFriendsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                }
                            });

                            return new FriendsViewHolder(view);
                        }
                    };
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    linearLayoutManager.setReverseLayout(true);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(firebaseRecyclerAdapter);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    firebaseRecyclerAdapter.startListening();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        if (firebaseRecyclerAdapter!=null)
            firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter!= null)
            firebaseRecyclerAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firebaseRecyclerAdapter!=null)
            firebaseRecyclerAdapter.startListening();
    }
}