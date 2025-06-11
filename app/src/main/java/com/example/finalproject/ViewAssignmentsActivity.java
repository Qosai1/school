package com.example.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ViewAssignmentsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ViewAssignmentsAdapter adapter;
    ArrayList<Assignment> assignments = new ArrayList<>();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    int student_id;
    int classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);

        recyclerView = findViewById(R.id.recyclerViewAssignments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // استرجاع student_id و classID من SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        student_id = prefs.getInt("student_id", -1);
        classId = prefs.getInt("classID", -1);

        if (student_id == -1 || classId == -1) {
            Toast.makeText(this, "خطأ في تحميل بيانات الطالب", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Pass 'this' activity to the adapter
        adapter = new ViewAssignmentsAdapter(assignments, student_id, this);
        recyclerView.setAdapter(adapter);

        fetchAssignments(classId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ViewAssignmentsAdapter.getFilePickerRequestCode() && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedFileUri = data.getData();
                adapter.handleFileSelection(selectedFileUri);
            } else {
                Toast.makeText(this, "لم يتم اختيار ملف", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchAssignments(int classId) {
        String url = "http://10.0.2.2/get_student_assignments.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        assignments.clear(); // Clear existing data

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Assignment assignment = new Assignment();
                            assignment.assignment_id = obj.getInt("assignment_id");
                            assignment.title = obj.getString("title");
                            assignment.description = obj.getString("description");

                            try {
                                assignment.due_date = format.parse(obj.getString("due_date"));
                            } catch (Exception e) {
                                assignment.due_date = null;
                            }

                            assignment.attachment = obj.optString("attachment", null);
                            assignment.subject_name = obj.getString("subject_name");
                            assignments.add(assignment);
                        }
                        adapter.notifyDataSetChanged();

                        if (assignments.isEmpty()) {
                            Toast.makeText(this, "لا توجد واجبات", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "فشل في تحليل البيانات: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "فشل في تحميل البيانات: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("classID", String.valueOf(classId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);

    }

}