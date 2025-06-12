package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrarActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button addStudentButton = findViewById(R.id.addStudentButton);

        addStudentButton.setOnClickListener(view -> {
            Intent intent = new Intent(RegistrarActivity.this, addStudent_Activity.class);
            startActivity(intent);
        });
        Button addteacher=findViewById(R.id.addteacher);
        addteacher.setOnClickListener(view->{
            Intent i = new Intent(this, addTeacher_Activity.class);
            startActivity(i);
        });
        Button btnOpenAddSubject = findViewById(R.id.btnOpenAddSubject);
        btnOpenAddSubject.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddSubjectActivity.class);
            startActivity(intent);
        });
        Button btnOpenAdvertisements = findViewById(R.id.btnOpenAdvertisements);
        btnOpenAdvertisements.setOnClickListener(view -> {
            Intent intent = new Intent(this, EnhancedAdsActivity.class);
            startActivity(intent);

        });
        Button createstudent= findViewById(R.id.studentScheduleButton);
        createstudent.setOnClickListener(view->{
            Intent intentt=new Intent(this,CreateStudentSchedule.class);
            startActivity(intentt);
        });
        Button createteacher= findViewById(R.id.classScheduleButton);
        createteacher.setOnClickListener(view->{
            Intent intentt=new Intent(this,CreateTeacherSchedule.class);
            startActivity(intentt);
        });
        Button logout= findViewById(R.id.logoutButton);
        logout.setOnClickListener(view->{
            Intent intentt=new Intent(this, LogoutHelper.class);
            startActivity(intentt);
        });
    }
}