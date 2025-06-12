package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateTeacherSchedule extends AppCompatActivity {
    Spinner teacherSpinner;
    TableLayout tableLayout;
    Button btnSaveSchedule, btnViewSchedule;
    String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
    int maxLectures = 7;
    ArrayList<Integer> teacherIds = new ArrayList<>();
    ArrayList<String> teacherNames = new ArrayList<>();
    int selectedTeacherId = -1;
    EditText[][] cellRefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_teacher_schedule);

        teacherSpinner = findViewById(R.id.teacherSpinner);
        tableLayout = findViewById(R.id.tableLayout);
        btnSaveSchedule = findViewById(R.id.btnSaveSchedule);
        // btnViewSchedule = findViewById(R.id.btnViewTeacherSchedule);

        loadTeachers();
        setupSpinnerListener();
        buildTable();

        btnSaveSchedule.setOnClickListener(v -> {
            try {
                saveSchedule();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

//        btnViewSchedule.setOnClickListener(v -> {
//            if (selectedTeacherId == -1) {
//                Toast.makeText(this, "Please select a teacher first", Toast.LENGTH_SHORT).show();
//            } else {
//                Intent intent = new Intent(this, TeacherScheduleActivity.class);
//                intent.putExtra("teacher_id", selectedTeacherId);
//                startActivity(intent);
//            }
//        });
    }

    private void loadTeachers() {
        String url = "http://10.0.2.2/get_teachers.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            JSONArray data = response.getJSONArray("data");
                            teacherNames.clear();
                            teacherIds.clear();
                            teacherNames.add("Select Teacher");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject t = data.getJSONObject(i);
                                teacherIds.add(t.getInt("teacher_id"));
                                teacherNames.add(t.getString("name"));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_spinner_item,
                                    teacherNames
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            teacherSpinner.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Server connection failed", Toast.LENGTH_SHORT).show();
                });

        queue.add(req);
    }

    private void setupSpinnerListener() {
        teacherSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedTeacherId = -1;
                    buildTable();
                } else {
                    selectedTeacherId = teacherIds.get(position - 1);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void buildTable() {
        tableLayout.removeAllViews();
        cellRefs = new EditText[days.length][maxLectures];

        TableRow header = new TableRow(this);
        header.addView(createHeaderCell("Day \\ Lecture"));
        for (int i = 1; i <= maxLectures; i++) {
            header.addView(createHeaderCell("Lecture " + i));
        }
        tableLayout.addView(header);

        for (int i = 0; i < days.length; i++) {
            TableRow row = new TableRow(this);
            TextView dayCell = new TextView(this);
            dayCell.setText(days[i]);
            dayCell.setPadding(12, 12, 12, 12);
            dayCell.setBackgroundColor(Color.LTGRAY);
            dayCell.setTypeface(null, Typeface.BOLD);
            dayCell.setGravity(Gravity.CENTER);
            row.addView(dayCell);

            for (int j = 0; j < maxLectures; j++) {
                EditText cell = new EditText(this);
                cell.setHint("Class");
                cell.setGravity(Gravity.CENTER);
                cell.setBackgroundResource(android.R.drawable.editbox_background);
                row.addView(cell);
                cellRefs[i][j] = cell;
            }
            tableLayout.addView(row);
        }
    }

    private void saveSchedule() throws UnsupportedEncodingException {
        if (selectedTeacherId == -1) {
            Toast.makeText(this, "Select a teacher first", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONArray scheduleArray = new JSONArray();
        for (int i = 0; i < days.length; i++) {
            for (int j = 0; j < maxLectures; j++) {
                String className = cellRefs[i][j].getText().toString().trim();
                if (!className.isEmpty()) {
                    JSONObject entry = new JSONObject();
                    try {
                        entry.put("teacher_id", selectedTeacherId);
                        entry.put("day", days[i]);
                        entry.put("lecture_number", j + 1);
                        entry.put("class_name", className);
                        scheduleArray.put(entry);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        String url = "http://10.0.2.2/insert_teacher_schedule.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest req = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Schedule saved successfully", Toast.LENGTH_SHORT).show();
                    fetchTeacherSchedule(selectedTeacherId); // show after save
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public byte[] getBody() {
                return scheduleArray.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        queue.add(req);
    }

    private void fetchTeacherSchedule(int teacherId) {
        String url = "http://10.0.2.2/get_teacher_schedule.php?teacher_id=" + teacherId;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            JSONArray data = response.getJSONArray("data");
                            displaySchedule(data);
                        } else {
                            Toast.makeText(this, "No schedule found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error loading schedule", Toast.LENGTH_SHORT).show();
                });

        queue.add(req);
    }

    private void displaySchedule(JSONArray data) throws JSONException {
        buildTable();

        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.getJSONObject(i);
            String day = obj.getString("day");
            int lecture = obj.getInt("lecture_number");
            String className = obj.getString("class_name");

            for (int d = 0; d < days.length; d++) {
                if (days[d].equals(day)) {
                    cellRefs[d][lecture - 1].setText(className);
                    break;
                }
            }
        }
    }

    private TextView createHeaderCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(12, 12, 12, 12);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setBackgroundColor(Color.DKGRAY);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }
}
