package com.example.sugertime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Chats_list_screen extends AppCompatActivity {

    private RecyclerView chats_LAY_chatList;

    private UserListAdapter userListAdapter;
    private DatabaseReference reference;

    private String username;
    private List<Chat_list> usersChatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_list_screen);

        findView();
        initRecycleView();

        usersChatList = new ArrayList<>();

        username = getIntent().getStringExtra("user");


        // Read from DB and show chat list with all people
        reference = FirebaseDatabase.getInstance().getReference("ChatList/").child(username);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersChatList.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Chat_list chatList = snap.getValue(Chat_list.class);
                    usersChatList.add(chatList);
                }

                // Show with recycle view list of users
                userListAdapter = new UserListAdapter(getApplicationContext(), usersChatList, username);
                chats_LAY_chatList.setAdapter(userListAdapter);
                userListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initRecycleView() {
        chats_LAY_chatList.setHasFixedSize(true);
        chats_LAY_chatList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void findView() {
        chats_LAY_chatList = findViewById(R.id.chats_LAY_chatList);
    }

}