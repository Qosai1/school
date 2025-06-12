package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ShowSubmissionsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    AssignmentAdapter adapter;
    List<Assignment> assignmentList = new ArrayList<>();
    int teacher_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_submissions);

        recyclerView = findViewById(R.id.recyclerViewAssignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AssignmentAdapter(this, assignmentList);
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        teacher_id = prefs.getInt("teacher_id", -1);

        Log.d("TEACHER_ID", "Teacher ID: " + teacher_id);

        if (teacher_id == -1) {
            Toast.makeText(this, "Teacher ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAssignments();
    }

    private void loadAssignments() {
        String url = "http://10.0.2.2/API/get_teacher_assignments.php?teacher_id=" + teacher_id;

        Log.d("REQUEST_URL", url);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("RESPONSE", response.toString());
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Assignment assignment = new Assignment();
                            assignment.assignment_id = obj.getInt("assignment_id");
                            assignment.title = obj.getString("title");
                            assignment.description = obj.getString("description");

                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                assignment.due_date = dateFormat.parse(obj.getString("due_date"));
                            } catch (ParseException e) {
                                assignment.due_date = new Date();
                            }

                            assignment.attachment = obj.optString("attachment", "");
                            assignment.subject_name = obj.optString("subject_name", "");
                            assignmentList.add(assignment);
                        }
                        adapter.notifyDataSetChanged();

                        if (assignmentList.isEmpty()) {
                            Toast.makeText(this, "No assignments found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("JSON_ERROR", e.getMessage());
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NETWORK_ERROR", error.toString());
                    Toast.makeText(this, "Failed to load assignments: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}