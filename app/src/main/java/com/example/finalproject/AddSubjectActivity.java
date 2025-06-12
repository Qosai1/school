package com.example.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AddSubjectActivity extends AppCompatActivity {

    private EditText subjectIdEditText, subjectNameEditText;
    private Button addSubjectButton;


    private static final String URL = "http://10.0.2.2/API/add_subject.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        // Initialize UI components
        subjectIdEditText = findViewById(R.id.subjectIdEditText);
        subjectNameEditText = findViewById(R.id.subjectNameEditText);
        addSubjectButton = findViewById(R.id.addSubjectButton);

        addSubjectButton.setOnClickListener(v -> addSubject());
    }

    private void addSubject() {
        String subject_id = subjectIdEditText.getText().toString().trim();
        String subject_name = subjectNameEditText.getText().toString().trim();

        // Validate input
        if (subject_id.isEmpty() || subject_name.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create POST request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Toast.makeText(AddSubjectActivity.this, response, Toast.LENGTH_LONG).show();

                    // Clear fields if successful
                    if (response.contains("successfully")) {
                        subjectIdEditText.setText("");
                        subjectNameEditText.setText("");
                    }
                },
                error -> {
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Network error occurred";
                    Toast.makeText(AddSubjectActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("subject_id", subject_id);
                params.put("subject_name", subject_name);
                return params;
            }
        };

        // Add request to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }
}