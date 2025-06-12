package com.example.finalproject;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
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

import java.util.HashMap;

public class TeacherScheduleActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
    private final int maxLectures = 7;
    private int teacherId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        tableLayout = findViewById(R.id.tableLayout);


        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        teacherId = prefs.getInt("teacher_id", -1);

        if (teacherId != -1) {
            fetchTeacherSchedule(teacherId);
        } else {
            initEmptyTable();
            Toast.makeText(this, "No teacher logged in", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchTeacherSchedule(int teacherId) {
        String url = "http://10.0.2.2/API/get_teacher_schedule.php?teacher_id=" + teacherId;
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

    private TableRow createDayRow(String day, HashMap<Integer, String> lectures) {
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
        tv.setTypeface(null, Typeface.BOLD);
        tv.setBackgroundColor(Color.DKGRAY);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }
}