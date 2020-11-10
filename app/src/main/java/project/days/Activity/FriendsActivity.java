package project.days.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import project.days.Models.Friends;
import project.days.R;
import project.days.ViewHolders.FriendsViewHolder;

public class FriendsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference FriendsRef, UsersRef;
    private RecyclerView FriendsView;
    String currentUserID, person_ref;
    private TextView FriendsAlert;
    private Button AddFriendsButton;
    FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Friends> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid().toString();
        FriendsAlert = (TextView) findViewById(R.id.no_friends_alert);
        FriendsView = (RecyclerView) findViewById(R.id.friends_recycler_view);
        AddFriendsButton = (Button) findViewById(R.id.add_friends_button);
        FriendsView.setAdapter(firebaseRecyclerAdapter);


        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendsRef = UsersRef.child(currentUserID).child("Friends");

        FriendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                {
                   FriendsAlert.setVisibility(View.VISIBLE);
                }
                else
                {
                    FriendsAlert.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        AddFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fIntent = new Intent(FriendsActivity.this, AddFriendsActivity.class);
                startActivity(fIntent);
            }
        });

        options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(FriendsRef,Friends.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull Friends model) {
                person_ref = getRef(position).getKey();
                holder.setName(model.getName());
            }


            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_view_layout,parent,false);
                final ImageView addButton = (ImageView) view.findViewById(R.id.add_him_button);
                addButton.setImageResource(R.drawable.delete_ic);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       FriendsRef.child(person_ref).removeValue()
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful())
                                       {
                                           UsersRef.child(person_ref).child("Friends").child(currentUserID)
                                                   .removeValue();
                                           Toast.makeText(FriendsActivity.this, "Removed successfully.", Toast.LENGTH_SHORT).show();
                                       }

                                   }
                               });
                    }
                });

                return new FriendsViewHolder(view);
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        FriendsView.setHasFixedSize(true);
        FriendsView.setLayoutManager(linearLayoutManager);
        FriendsView.setAdapter(firebaseRecyclerAdapter);
        FriendsView.setVisibility(View.VISIBLE);
        firebaseRecyclerAdapter.startListening();


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