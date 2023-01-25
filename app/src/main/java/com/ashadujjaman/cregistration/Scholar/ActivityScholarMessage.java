package com.ashadujjaman.cregistration.Scholar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ActivityScholarMessage extends AppCompatActivity {
    Toolbar toolbar;
    TextView title, emptyText;
    RecyclerView recyclerView;
    ImageView submit;
    EditText messageEditText;
    LinearLayoutManager manager;

    String adminId= "XJ9X8EPV76SZ6cbo6jlP38enZTz1", userId;
    ArrayList<ClassChatMessage> messageList;
    AdapterChatMessage adapter;

    FirebaseDatabase database, userDatabase;
    FirebaseAuth mAuth;
    DatabaseReference supportRef, requestRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scholar_message);
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
//                String date = java.text.DateFormat.getDateTimeInstance().format(new Date());

            ClassChatMessage newMsg = new ClassChatMessage(message, "User", userId, "Admin", userId);
            Log.e("FireBase:", supportRef.toString());
            supportRef.child(String.valueOf(timeInMills)).setValue(newMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
//                            manager.setStackFromEnd(true);
//                            manager.setReverseLayout(false);
//                            recyclerView.setLayoutManager(manager);
                        recyclerView.scrollToPosition(messageList.size() - 1);
                        messageEditText.setText("");
                    }else{
                        Toast.makeText(ActivityScholarMessage.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ActivityScholarMessage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void Initialize() {
        toolbar = findViewById(R.id.scholarToolbar);
        title = findViewById(R.id.scholarToolbarTitle);
        emptyText = findViewById(R.id.scholarEmptyText);
        recyclerView = findViewById(R.id.scholarRecyclerView);
        messageEditText = findViewById(R.id.scholarEditText);
        submit = findViewById(R.id.scholarSubmitBtn);

        manager = new LinearLayoutManager(ActivityScholarMessage.this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        title.setText("Adk Scholar User Side");

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        database = FirebaseDatabase.getInstance("https://complain-registration-a1df2-default-rtdb.firebaseio.com/");

        supportRef = database.getReference("Scholar").child(userId);
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
                }else{
                    emptyText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActivityScholarMessage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        adapter = new AdapterChatMessage(ActivityScholarMessage.this, messageList);
        recyclerView.setAdapter(adapter);
//        recyclerView.scrollToPosition(messageList.size() - 1);
    }

}