package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherActivity extends AppCompatActivity {

    Button addAssignmentBtn;
    Button btnAddMarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        Button  btnAddAssignment = findViewById(R.id.btnAddAssignment);
        btnAddAssignment.setOnClickListener(view -> {
            Intent intent = new Intent(TeacherActivity.this, AddAssignmentActivity.class);
            startActivity(intent);
        });
        Button  btnAddMarks = findViewById(R.id.btnAddMarks);
        btnAddMarks.setOnClickListener(view -> {
            Intent intent = new Intent(TeacherActivity.this, AddMarksActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.logoutButton).setOnClickListener(v -> LogoutHelper.logout(this));

        Button btnshowscedule = findViewById(R.id.Schedule);
        btnshowscedule.setOnClickListener(view->{

            Intent i =new Intent(this,TeacherScheduleActivity.class);
            startActivity(i);
        });

        Button btnshowsubmissions = findViewById(R.id.btnShowSubmissions);
        btnshowsubmissions.setOnClickListener(view->{

            Intent i =new Intent(this,ShowSubmissionsActivity.class);
            startActivity(i);
        });



    }
}