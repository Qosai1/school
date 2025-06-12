package com.example.finalproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;
import java.util.*;

public class AssignmentSubmissionsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView totalCount, assignmentTitle;
    StudentSubmissionAdapter adapter;
    List<StudentSubmission> submissions = new ArrayList<>();
    int assignmentId;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_submissions);

        assignmentId = getIntent().getIntExtra("assignment_id", -1);
        String title = getIntent().getStringExtra("assignment_title");

        recyclerView = findViewById(R.id.recyclerViewSubmissions);
        totalCount = findViewById(R.id.totalSubmissionCount);
        assignmentTitle = findViewById(R.id.assignmentTitle);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentSubmissionAdapter(this, submissions);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        if (title != null) {
            assignmentTitle.setText(title);
        }

        if (assignmentId == -1) {
            Toast.makeText(this, "Assignment ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadSubmissions();
    }

    private void loadSubmissions() {
        String url = "http://10.0.2.2/API/get_assignment_submissions.php?assignment_id=" + assignmentId;
        Log.d("SUBMISSIONS_URL", "Requesting URL: " + url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("SUBMISSIONS_RAW_RESPONSE", "Raw Response: " + response);

                    try {
                        response = response.trim();

                        if (response.startsWith("{")) {
                            JSONObject errorObj = new JSONObject(response);
                            if (errorObj.has("error")) {
                                String errorMsg = errorObj.getString("error");
                                Log.e("SERVER_ERROR", "Server returned error: " + errorMsg);
                                Toast.makeText(this, "Server Error: " + errorMsg, Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        JSONArray jsonArray;
                        if (response.startsWith("[")) {
                            jsonArray = new JSONArray(response);
                        } else {
                            JSONObject responseObj = new JSONObject(response);
                            if (responseObj.has("data")) {
                                jsonArray = responseObj.getJSONArray("data");
                            } else {
                                throw new JSONException("Response is not a valid JSON array or object with data array");
                            }
                        }

                        Log.d("SUBMISSIONS_PARSED", "Parsed JSON Array with " + jsonArray.length() + " items");

                        totalCount.setText("Total Submissions: " + jsonArray.length());
                        submissions.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            Log.d("SUBMISSION_ITEM", "Processing item " + i + ": " + obj.toString());

                            StudentSubmission s = new StudentSubmission();

                            if (obj.has("student_id")) {
                                s.student_id = obj.getInt("student_id");
                            } else {
                                Log.w("MISSING_FIELD", "student_id missing in item " + i);
                                continue; // تخطي هذا العنصر
                            }

                            if (obj.has("student_name")) {
                                s.student_name = obj.getString("student_name");
                            } else {
                                s.student_name = "Unknown Student";
                                Log.w("MISSING_FIELD", "student_name missing in item " + i);
                            }

                            s.attachment = obj.optString("attachment", "");

                            if (obj.has("submitted_at")) {
                                s.submitted_at = obj.getString("submitted_at");
                            } else {
                                s.submitted_at = "Unknown Date";
                                Log.w("MISSING_FIELD", "submitted_at missing in item " + i);
                            }

                            submissions.add(s);
                        }

                        adapter.notifyDataSetChanged();

                        if (submissions.isEmpty()) {
                            Toast.makeText(this, "No submissions found for this assignment", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("SUBMISSIONS_SUCCESS", "Successfully loaded " + submissions.size() + " submissions");
                            Toast.makeText(this, "Loaded " + submissions.size() + " submissions", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Log.e("JSON_PARSE_ERROR", "JSON parsing failed: " + e.getMessage());
                        Log.e("JSON_PARSE_ERROR", "Response that failed to parse: " + response);

                        if (response.isEmpty()) {
                            Toast.makeText(this, "Server returned empty response", Toast.LENGTH_LONG).show();
                        } else if (!response.startsWith("[") && !response.startsWith("{")) {
                            Toast.makeText(this, "Server returned invalid JSON format", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "JSON parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                error -> {
                    Log.e("NETWORK_ERROR", "Request failed: " + error.toString());

                    if (error.networkResponse != null) {
                        Log.e("NETWORK_ERROR", "Status Code: " + error.networkResponse.statusCode);
                        String responseBody = new String(error.networkResponse.data);
                        Log.e("NETWORK_ERROR", "Error Response Body: " + responseBody);

                        Toast.makeText(this, "Network Error (Code: " + error.networkResponse.statusCode + "): " + responseBody, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000, // 15 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
}