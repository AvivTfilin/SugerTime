package com.example.sugertime;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    Context context;
    String username;
    List<Chat_list> nameList;


    public UserListAdapter(Context context, List<Chat_list> nameList, String username) {
        this.context = context;
        this.nameList = nameList;
        this.username = username;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlist_item, parent, false);
        return new UserListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.userList_LBL_name.setText(nameList.get(position).id);

        readLastMessage(nameList.get(position).getId(), holder.userList_LBL_lastMessage);

        holder.userList_LAY_userList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Chat_screen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("sendTo", nameList.get(position).id);
                intent.putExtra("sender", username);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userList_LBL_name;
        private TextView userList_LBL_lastMessage;
        private RelativeLayout userList_LAY_userList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userList_LBL_name = itemView.findViewById(R.id.userList_LBL_name);
            userList_LBL_lastMessage = itemView.findViewById(R.id.userList_LBL_lastMessage);
            userList_LAY_userList = itemView.findViewById(R.id.userList_LAY_userList);
        }
    }

    private void readLastMessage(final String user, final TextView lastMessage) {
        DatabaseReference reference;

        reference = FirebaseDatabase.getInstance().getReference("Chats/");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot snap : snapshot.getChildren()) {
                        Message message = snap.getValue(Message.class);

                        if (message.getReceiver().equals(username) && message.getSender().equals(user)
                                || message.getReceiver().equals(user) && message.getSender().equals(username)) {
                                lastMessage.setText(message.getMessage());
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
