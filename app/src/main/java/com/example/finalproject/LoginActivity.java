package com.example.finalproject; // غيّري اسم الباكيج حسب مشروعك

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginButton;

    String loginUrl = "http://10.0.2.2/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(username, password);
        });
    }

    private void loginUser(String username, String password) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, loginUrl, jsonBody,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            String role = response.getString("role");
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            switch (role) {
                                case "student":
                                    Log.d("LOGIN_RESPONSE", response.toString());

                                    // حفظ بيانات الطالب
                                    int studentId = response.getInt("student_id");
                                    String studentName = response.optString("student_name", "");

                                    editor.putInt("student_id", studentId);
                                    editor.putString("student_name", studentName);
                                    editor.putString("user_type", "student");
                                    editor.putInt("classID", response.getInt("classID"));
                                    editor.apply();

                                    Log.d("LOGIN", "Student data saved - ID: " + studentId + ", Name: " + studentName);

                                    startActivity(new Intent(this, StudentActivity.class));
                                    break;

                                case "teacher":
                                    Log.d("LOGIN_RESPONSE", response.toString());

                                    int teacherId = response.getInt("teacher_id");
                                    String teacherName = response.optString("teacher_name", "");
                                    int subjectId = response.optInt("subject_id", -1); // إضافة subject_id

                                    // تخزين teacher_id و subject_id في SharedPreferences
                                    editor.putInt("teacher_id", teacherId);
                                    editor.putString("teacher_name", teacherName);
                                    editor.putInt("teacher_subject_id", subjectId); // حفظ subject_id
                                    editor.putString("user_type", "teacher");
                                    editor.apply();

                                    Log.d("LOGIN", "Teacher data saved - ID: " + teacherId + ", Name: " + teacherName + ", Subject ID: " + subjectId);

                                    startActivity(new Intent(this, TeacherActivity.class));
                                    break;

                                case "registrar":
                                    int registrarId = response.optInt("registrar_id", -1);

                                    editor.putInt("registrar_id", registrarId);
                                    editor.putString("user_type", "registrar");
                                    editor.apply();

                                    startActivity(new Intent(this, RegistrarActivity.class));
                                    break;

                                default:
                                    Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
                            }

                            finish(); // إغلاق صفحة تسجيل الدخول
                        } else {
                            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Network error: " + error.toString(), Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }
}