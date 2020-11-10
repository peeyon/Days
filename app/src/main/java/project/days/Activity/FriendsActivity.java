package project.days.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import project.days.R;

public class FriendsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference FriendsRef;
    private RecyclerView FriendsView;
    String currentUserID;
    private TextView FriendsAlert;
    private Button AddFriendsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid().toString();
        FriendsAlert = (TextView) findViewById(R.id.no_friends_alert);
        FriendsView = (RecyclerView) findViewById(R.id.friends_recycler_view);
        AddFriendsButton = (Button) findViewById(R.id.add_friends_button);

        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Friends");

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
                    showFriends();
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

    }

    private void showFriends() {

    }
}