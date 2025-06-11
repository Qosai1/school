package com.example.finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMarksActivity extends AppCompatActivity {

    EditText examNameInput;
    Button submitBtn;
    Spinner classSpinner;
    RecyclerView studentsRecyclerView;

    ArrayAdapter<String> spinnerAdapter;
    StudentsMarksAdapter studentsAdapter;

    List<String> classList = new ArrayList<>();
    List<StudentMarkItem> studentsList = new ArrayList<>();
    Map<String, Integer> classNameToIdMap = new HashMap<>();

    private int teacherId = -1;
    private int teacherSubjectId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marks);

        initViews();
        setupClickListeners();


        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        teacherId = prefs.getInt("teacher_id", -1);
        teacherSubjectId = prefs.getInt("teacher_subject_id", -1);

        if (teacherId == -1 || teacherSubjectId == -1) {
            Toast.makeText(this, "Teacher data not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        fetchTeacherClasses(teacherId);
    }

    private void initViews() {
        examNameInput = findViewById(R.id.examNameInput);
        classSpinner = findViewById(R.id.classSpinner);
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView);
        submitBtn = findViewById(R.id.submitBtn);

        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentsAdapter = new StudentsMarksAdapter(studentsList);
        studentsRecyclerView.setAdapter(studentsAdapter);
    }

    private void setupClickListeners() {
        submitBtn.setOnClickListener(v -> submitMarks());
    }

    private void fetchTeacherClasses(int teacherId) {
        String url = "http://10.0.2.2/get_teacher_classes.php";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response.trim());
                        classList.clear();
                        classNameToIdMap.clear();

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            classList.add(o.getString("name"));
                            classNameToIdMap.put(o.getString("name"), o.getInt("id"));
                        }

                        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classList);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        classSpinner.setAdapter(spinnerAdapter);

                        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                                String name = classList.get(pos);
                                int cid = classNameToIdMap.get(name);
                                fetchStudentsByClass(cid);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing classes", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Server error classes", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("teacher_id", String.valueOf(teacherId));
                return p;
            }
        };
        Volley.newRequestQueue(this).add(req);
    }

    private void fetchStudentsByClass(int classId) {
        String url = "http://10.0.2.2/get_students_by_class.php";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        studentsList.clear();

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject student = arr.getJSONObject(i);
                            StudentMarkItem item = new StudentMarkItem(
                                    student.getInt("student_id"),
                                    student.getString("name")
                            );
                            studentsList.add(item);
                        }

                        studentsAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing students", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Server error students", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> p = new HashMap<>();
                p.put("classID", String.valueOf(classId));
                return p;
            }
        };
        Volley.newRequestQueue(this).add(req);
    }

    private void submitMarks() {
        String examName = examNameInput.getText().toString().trim();

        if (examName.isEmpty()) {
            Toast.makeText(this, "Please enter exam name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (studentsList.isEmpty()) {
            Toast.makeText(this, "No students selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // التحقق من أن جميع العلامات تم إدخالها
        for (StudentMarkItem student : studentsList) {
            if (student.getMark().isEmpty()) {
                Toast.makeText(this, "Please enter marks for all students", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        submitMarksToServer(examName);
    }

    private void submitMarksToServer(String examName) {
        String url = "http://10.0.2.2/add_marks.php";

        try {
            JSONArray marksArray = new JSONArray();
            for (StudentMarkItem student : studentsList) {
                JSONObject markObj = new JSONObject();
                markObj.put("student_id", student.getStudentId());
                markObj.put("mark", student.getMark());
                marksArray.put(markObj);
            }

            StringRequest req = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject o = new JSONObject(response);
                            if ("success".equals(o.getString("status"))) {
                                Toast.makeText(this, "Marks added successfully!", Toast.LENGTH_SHORT).show();
                                examNameInput.setText("");
                                // إعادة تعيين العلامات
                                for (StudentMarkItem student : studentsList) {
                                    student.setMark("");
                                }
                                studentsAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(this, "Error: " + o.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Invalid server response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Server error adding marks", Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> p = new HashMap<>();
                    p.put("exam_name", examName);
                    p.put("subject_id", String.valueOf(teacherSubjectId)); // استخدام subject_id الخاص بالمعلم
                    p.put("marks_data", marksArray.toString());
                    return p;
                }
            };
            Volley.newRequestQueue(this).add(req);

        } catch (Exception e) {
            Toast.makeText(this, "Error preparing data", Toast.LENGTH_SHORT).show();
        }
    }
}