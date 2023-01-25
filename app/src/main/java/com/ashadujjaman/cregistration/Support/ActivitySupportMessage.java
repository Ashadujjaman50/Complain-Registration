package com.ashadujjaman.cregistration.Support;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashadujjaman.cregistration.Chatting.ClassChatMessage;
import com.ashadujjaman.cregistration.R;
import com.ashadujjaman.cregistration.adapter.AdapterChatMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivitySupportMessage extends AppCompatActivity {
    Toolbar toolbar;
    TextView title, emptyText;
    RecyclerView recyclerView;
    ImageView submit;
    EditText messageEditText;
    LinearLayoutManager manager;

    String adminId, userId;
    ArrayList<ClassChatMessage> messageList;
    AdapterChatMessage adapter;

    FirebaseDatabase database, userDatabase;
    FirebaseAuth mAuth;
    DatabaseReference supportRef, requestRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_message);

        //init views
        Initialize();

        submit.setEnabled(false);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                submit.setEnabled(!(s.length() == 0));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submit.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            long timeInMills = System.currentTimeMillis();
            //String date = java.text.DateFormat.getDateTimeInstance().format(new Date());

            ClassChatMessage newMsg = new ClassChatMessage(message, "User", userId, "Admin", userId);
            Log.e("FireBase:", supportRef.toString());
            supportRef.child(String.valueOf(timeInMills)).setValue(newMsg).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
//                            manager.setStackFromEnd(true);
//                            manager.setReverseLayout(false);
//                            recyclerView.setLayoutManager(manager);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                    messageEditText.setText("");
                }else{
                    Toast.makeText(ActivitySupportMessage.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ActivitySupportMessage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void Initialize() {
        toolbar = findViewById(R.id.supportToolbar);
        title = findViewById(R.id.supportToolbarTitle);
        emptyText = findViewById(R.id.supportEmptyText);
        recyclerView = findViewById(R.id.supportRecyclerView);
        messageEditText = findViewById(R.id.supportEditText);
        submit = findViewById(R.id.supportSubmitBtn);

        manager = new LinearLayoutManager(ActivitySupportMessage.this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        title.setText("Live Chat");

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        database = FirebaseDatabase.getInstance("https://complain-registration-a1df2-default-rtdb.firebaseio.com/");

        supportRef = database.getReference("SupportUser").child(userId);
        messageList = new ArrayList<>();
        supportRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for(DataSnapshot snap : snapshot.getChildren()){
                    ClassChatMessage msg = snap.getValue(ClassChatMessage.class);
                    messageList.add(msg);
                }
                if(messageList.size() != 0){
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                else{
                    emptyText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActivitySupportMessage.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        adapter = new AdapterChatMessage(ActivitySupportMessage.this, messageList);
        recyclerView.setAdapter(adapter);
//        recyclerView.scrollToPosition(messageList.size() - 1);
    }
}