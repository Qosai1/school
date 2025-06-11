package com.example.finalproject;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateTeacherSchedule extends AppCompatActivity {

    Spinner teacherSpinner;
    TableLayout tableLayout;

    String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
    int maxLectures = 7;
    ArrayList<Integer> teacherIds = new ArrayList<>();
    ArrayList<String> teacherNames = new ArrayList<>();
    int selectedTeacherId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        tableLayout = findViewById(R.id.tableLayout);
        loadTeachers();
        setupSpinnerListener();
        initEmptyTable();
    }
    private void loadTeachers() {
        String url = "http://10.0.2.2/get_teachers.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET, url, null,
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
                            adapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item
                            );
                            teacherSpinner.setAdapter(adapter);

                        } else {
                            Toast.makeText(this,
                                    "Failed to load teachers",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this,
                                "Parsing error",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this,
                            "Server connection failed",
                            Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(req);
    }
    private void setupSpinnerListener() {
        teacherSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view,
                                               int position,
                                               long id) {
                        if (position == 0) {
                            selectedTeacherId = -1;
                            initEmptyTable();
                        } else {
                            selectedTeacherId = teacherIds.get(position - 1);
                            fetchTeacherSchedule(selectedTeacherId);
                        }
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                });
    }
    private void fetchTeacherSchedule(int teacherId) {
        String url = "http://10.0.2.2/get_teacher_schedule.php"
                + "?teacher_id=" + teacherId;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            JSONArray data = response.getJSONArray("data");
                            if (data.length() == 0) {
                                initEmptyTable();
                                Toast.makeText(this, "No schedule found", Toast.LENGTH_SHORT).show();
                            } else {
                                populateTable(data);
                            }
                        } else {
                            initEmptyTable();
                            Toast.makeText(this, "Failed to fetch schedule", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        initEmptyTable();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    initEmptyTable();
                    Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(req);
    }

    private void initEmptyTable() {
        tableLayout.removeAllViews();
        createTableHeader();
        for (String day : days) {
            tableLayout.addView(createDayRow(day, new HashMap<>()));
        }
    }

    private void populateTable(JSONArray data) throws JSONException {
        HashMap<String, HashMap<Integer, String>> map = new HashMap<>();
        for (String d : days) map.put(d, new HashMap<>());

        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.getJSONObject(i);
            String day  = obj.getString("day");
            int lec     = obj.getInt("lecture_number");
            String cls  = obj.getString("class_name");
            if (map.containsKey(day)) {
                map.get(day).put(lec, cls);
            }
        }

        tableLayout.removeAllViews();
        createTableHeader();
        for (String day : days) {
            tableLayout.addView(createDayRow(day, map.get(day)));
        }
    }
    private void createTableHeader() {
        TableRow header = new TableRow(this);
        header.addView(createHeaderCell("Day \\ Lecture"));
        for (int i = 1; i <= maxLectures; i++) {
            header.addView(createHeaderCell("Lecture " + i));
        }
        tableLayout.addView(header);
    }
    private TableRow createDayRow(String day,
                                  HashMap<Integer, String> lectures) {
        TableRow row = new TableRow(this);
        TextView tv = new TextView(this);
        tv.setText(day);
        tv.setPadding(12,12,12,12);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setBackgroundColor(Color.LTGRAY);
        tv.setGravity(Gravity.CENTER);
        row.addView(tv);

        for (int i = 1; i <= maxLectures; i++) {
            TextView cell = new TextView(this);
            cell.setText(lectures.getOrDefault(i, ""));
            cell.setPadding(12,12,12,12);
            cell.setGravity(Gravity.CENTER);
            cell.setBackgroundResource(android.R.drawable.editbox_background);
            row.addView(cell);
        }
        return row;
    }
    private TextView createHeaderCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(12,12,12,12);
        tv.setTypeface(null,Typeface.BOLD);
        tv.setBackgroundColor(Color.DKGRAY);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }
}