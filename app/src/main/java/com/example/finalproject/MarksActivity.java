package com.example.finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MarksActivity extends AppCompatActivity {

    private RecyclerView marksRecyclerView;
    private MarksAdapter marksAdapter;
    private List<Mark> marksList;
    private String marksUrl = "http://10.0.2.2/get_student_marks.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);

        // إعداد RecyclerView
        marksRecyclerView = findViewById(R.id.marksRecyclerView);
        marksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        marksList = new ArrayList<>();
        marksAdapter = new MarksAdapter(marksList);
        marksRecyclerView.setAdapter(marksAdapter);

        // جلب العلامات
        loadStudentMarks();
    }

    private void loadStudentMarks() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int studentId = sharedPreferences.getInt("student_id", -1);

        if (studentId == -1) {
            Toast.makeText(this, "Student ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("student_id", studentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, marksUrl, jsonBody,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            JSONArray marksArray = response.getJSONArray("marks");
                            marksList.clear();

                            for (int i = 0; i < marksArray.length(); i++) {
                                JSONObject markObj = marksArray.getJSONObject(i);

                                Mark mark = new Mark();
                                mark.setMarkId(markObj.getInt("mark_id"));
                                mark.setSubjectName(markObj.optString("subject_name", "Unknown Subject"));
                                mark.setExamName(markObj.getString("exam_name"));
                                mark.setMark(markObj.getDouble("mark"));
                                mark.setSubjectId(markObj.getInt("subject_id"));

                                marksList.add(mark);
                            }

                            marksAdapter.notifyDataSetChanged();

                            if (marksList.isEmpty()) {
                                Toast.makeText(this, "No marks available", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(this, "Failed to load marks", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("MARKS_ERROR", "Error: " + error.toString());
                    Toast.makeText(this, "Network error: " + error.toString(), Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }
}