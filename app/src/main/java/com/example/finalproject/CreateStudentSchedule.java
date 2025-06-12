package com.example.finalproject;

import android.content.Intent;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class CreateStudentSchedule extends AppCompatActivity {

    TableLayout tableLayout;
    Button btnSaveSchedule;
    Spinner classSpinner;

    String[] classList = {"Select Class", "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5", "Grade 6", "Grade 7", "Grade 8", "Grade 9","Grade 10","Grade 11", "Grade 12"};
    String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
    int lectureCount = 7;
    EditText[][] cellRefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_student_schedule);

        tableLayout = findViewById(R.id.tableLayout);
        btnSaveSchedule = findViewById(R.id.btnSaveSchedule);
        classSpinner = findViewById(R.id.classSpinner);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(adapter);

        classSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        buildTable();

        btnSaveSchedule.setOnClickListener(v -> saveSchedule());
    }

    private void buildTable() {
        tableLayout.removeAllViews();
        cellRefs = new EditText[days.length][lectureCount];

        TableRow header = new TableRow(this);
        header.addView(createHeaderCell("Day \\ Lecture"));
        for (int i = 1; i <= lectureCount; i++) {
            header.addView(createHeaderCell("Lecture " + i));
        }
        tableLayout.addView(header);

        for (int i = 0; i < days.length; i++) {
            TableRow row = new TableRow(this);
            TextView dayCell = new TextView(this);
            dayCell.setText(days[i]);
            dayCell.setPadding(12, 12, 12, 12);
            dayCell.setBackgroundColor(Color.LTGRAY);
            dayCell.setTextColor(Color.BLACK);
            dayCell.setTypeface(null, Typeface.BOLD);
            row.addView(dayCell);

            for (int j = 0; j < lectureCount; j++) {
                EditText cell = new EditText(this);
                cell.setHint("Subject");
                cell.setEms(6);
                cell.setTextColor(Color.BLACK);
                cell.setTextSize(15);
                cell.setTypeface(null, Typeface.BOLD);
                cell.setGravity(Gravity.CENTER);
                cell.setBackgroundResource(android.R.drawable.editbox_background);
                row.addView(cell);
                cellRefs[i][j] = cell;
            }
            tableLayout.addView(row);
        }
    }

    private void saveSchedule() {
        String selectedClass = classSpinner.getSelectedItem().toString();

        if (selectedClass.equals("Select Class")) {
            Toast.makeText(this, "Please select a class first", Toast.LENGTH_SHORT).show();
            return;
        }

        String checkUrl = "http://10.0.2.2/check_schedule.php?class_name=" + selectedClass;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest checkRequest = new StringRequest(
                Request.Method.GET,
                checkUrl,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("exists")) {
                            Toast.makeText(this, "Schedule already exists for this class", Toast.LENGTH_SHORT).show();
                        } else {
                            submitSchedule(selectedClass, queue);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Check failed", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Server error while checking", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );

        queue.add(checkRequest);
    }

    private void submitSchedule(String selectedClass, RequestQueue queue) {
        JSONArray scheduleArray = new JSONArray();

        for (int i = 0; i < days.length; i++) {
            JSONArray subjectsArray = new JSONArray();

            for (int j = 0; j < lectureCount; j++) {
                String subject = cellRefs[i][j].getText().toString().trim();
                if (!subject.isEmpty()) {
                    JSONObject subjectObj = new JSONObject();
                    try {
                        subjectObj.put("subject_name", subject);
                        subjectsArray.put(subjectObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (subjectsArray.length() > 0) {
                JSONObject dayObject = new JSONObject();
                try {
                    dayObject.put("class", selectedClass);
                    dayObject.put("day", days[i]);
                    dayObject.put("subjects", subjectsArray);
                    scheduleArray.put(dayObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        String url = "http://10.0.2.2/insert_schedule.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Toast.makeText(this, "Schedule saved successfully", Toast.LENGTH_SHORT).show();
                    classSpinner.setSelection(0);
                    clearTableInputs();
                },
                error -> {
                    Toast.makeText(this, "Failed to save the schedule", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        ) {
            @Override
            public byte[] getBody() {
                return scheduleArray.toString().getBytes(StandardCharsets.UTF_8);
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        queue.add(request);
    }
    private void clearTableInputs() {
        for (int i = 0; i < days.length; i++) {
            for (int j = 0; j < lectureCount; j++) {
                if (cellRefs[i][j] != null) {
                    cellRefs[i][j].setText("");
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