package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StudentActivity extends AppCompatActivity {
    Button btnShowAssignments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Button shoButton= findViewById(R.id.btnShowMarks);
        shoButton.setOnClickListener(view ->{
            Intent i =new Intent(this,MarksActivity.class);
            startActivity(i);

        });
        btnShowAssignments = findViewById(R.id.btnShowAssignments);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int studentClassId = prefs.getInt("classID", -1); // -1 إذا ما لقي إشي

        btnShowAssignments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (studentClassId != -1) {
                    Intent intent = new Intent(StudentActivity.this, ViewAssignmentsActivity.class);
                    intent.putExtra("classID", studentClassId);
                    startActivity(intent);
                } else {
                    Toast.makeText(StudentActivity.this, "class_id مش موجود في SharedPreferences", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.logoutButton).setOnClickListener(v -> LogoutHelper.logout(this));


        Button btnShowSchedule =findViewById(R.id.btnShowSchedule);
        btnShowSchedule.setOnClickListener(view->{
            Intent ii=new Intent(this, StudentSchedule.class);
            startActivity(ii);
        });




    }
}