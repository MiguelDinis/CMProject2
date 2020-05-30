package com.example.cmprojeto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmprojeto.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;


public class InitUserConfigs extends AppCompatActivity {

    //private Button letsGoBut;
    private String userId;
    private String username;
    private EditText weight;
    private EditText height;
    private EditText calories;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_user);
        //letsGoBut = (Button)findViewById(R.id.letsGoBut);
        userId = getIntent().getStringExtra("id");
        username = getIntent().getStringExtra("username");
        weight = findViewById(R.id.weightEdit);
        height = findViewById(R.id.heightEdit);
        calories = findViewById(R.id.caloriesEdit);
        db = FirebaseFirestore.getInstance();
    }

    public void letsGoClicked(View v){
        User newUser = new User(userId,username, Integer.parseInt(weight.getText().toString()),
                Integer.parseInt(height.getText().toString()),Integer.parseInt(calories.getText().toString()));
        //add new user to the db
        db.collection("Users").document(userId).set(newUser);
        startActivity(new Intent(this,MainActivity.class));
    }

}
