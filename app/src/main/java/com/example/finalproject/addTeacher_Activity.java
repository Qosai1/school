package com.example.finalproject;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;
import java.util.*;

public class addTeacher_Activity extends AppCompatActivity {

    EditText etName, etEmail, etPhone, etBirthDate, etAddress, etDepartment, etBio;
    Spinner spinnerGender, spinnerSubject;
    Button btnAddTeacher;
    String url = "http://10.0.2.2/add_teacher.php";
    String subjectsUrl = "http://10.0.2.2/get_subjects.php";
    private String selectedDate = "";

    private List<String> subjectNames = new ArrayList<>();
    private List<Integer> subjectIds = new ArrayList<>();
    private ArrayAdapter<String> subjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_teacher_activity);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etBirthDate = findViewById(R.id.etBirthDate);
        etAddress = findViewById(R.id.etAddress);
        etDepartment = findViewById(R.id.etDepartment);
        etBio = findViewById(R.id.etBio);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        btnAddTeacher = findViewById(R.id.btnAddTeacher);

        setupInputValidation();
        setupGenderSpinner();
        setupSubjectSpinner();
        setupDatePicker();
        loadSubjects();

        btnAddTeacher.setOnClickListener(v -> {
            if (validateFields()) {
                addTeacher();
            }
        });
    }

    private void setupInputValidation() {
        etName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        etPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        etPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        etDepartment.setInputType(InputType.TYPE_CLASS_TEXT);

        etBio.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        etBio.setMaxLines(4);
    }

    private void setupGenderSpinner() {
        String[] genderOptions = {"Select Gender", "Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, genderOptions) {
            @Override
            public boolean isEnabled(int position) {
                // تعطيل الخيار الأول (النص التوضيحي)
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(android.graphics.Color.GRAY);
                } else {
                    tv.setTextColor(android.graphics.Color.BLACK);
                }
                return view;
            }
        };

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
        spinnerGender.setSelection(0);
    }

    private void setupSubjectSpinner() {
        subjectNames.clear();
        subjectIds.clear();
        subjectNames.add("Choose subject");
        subjectIds.add(0);

        subjectAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, subjectNames) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(android.graphics.Color.GRAY);
                } else {
                    tv.setTextColor(android.graphics.Color.BLACK);
                }
                return view;
            }
        };

        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(subjectAdapter);
        spinnerSubject.setSelection(0);
    }

    private void loadSubjects() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                subjectsUrl,
                null,
                response -> {
                    try {
                        subjectNames.clear();
                        subjectIds.clear();
                        subjectNames.add("Choose subject");
                        subjectIds.add(0);

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject subject = response.getJSONObject(i);
                            int subjectId = subject.getInt("subject_id");
                            String subjectName = subject.getString("subject_name");

                            subjectIds.add(subjectId);
                            subjectNames.add(subjectName);
                        }

                        subjectAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Toast.makeText(addTeacher_Activity.this,
                                "خطأ في تحميل المواد: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String errorMsg = "خطأ في تحميل المواد";
                    if (error.networkResponse != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject errorResponse = new JSONObject(responseBody);
                            errorMsg = errorResponse.getString("error");
                        } catch (Exception e) {
                            errorMsg = "خطأ في الاتصال بالسيرفر";
                        }
                    }
                    Toast.makeText(addTeacher_Activity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    private void setupDatePicker() {
        etBirthDate.setFocusable(false);
        etBirthDate.setClickable(true);
        etBirthDate.setHint("date of birth");

        etBirthDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);

                    String displayDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    etBirthDate.setText(displayDate);
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -100);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private boolean validateFields() {
        boolean isValid = true;

        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError("Please fill the name field");
            isValid = false;
        } else if (!name.matches("^[a-zA-Zأ-ي\\s]+$")) {
            etName.setError("Name must contain only characters ");
            isValid = false;
        }

        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            etEmail.setError("Please fill the email field");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("the email is incorrect");
            isValid = false;
        }

        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            etPhone.setError("Please fill the phone number field");
            isValid = false;
        } else if (phone.length() != 10) {
            etPhone.setError("phone number must be only 10 digits");
            isValid = false;
        } else if (!phone.matches("^[0-9]+$")) {
            etPhone.setError("phone number must contain only characters");
            isValid = false;
        }

        if (selectedDate.isEmpty()) {
            etBirthDate.setError("Please fill the date of birth field");
            isValid = false;
        }

        if (spinnerGender.getSelectedItemPosition() == 0) {
            ((TextView) spinnerGender.getSelectedView()).setError("Please select the Gender");
            isValid = false;
        }

        if (etAddress.getText().toString().trim().isEmpty()) {
            etAddress.setError("Please fill the address field");
            isValid = false;
        }

        if (etDepartment.getText().toString().trim().isEmpty()) {
            etDepartment.setError("Please fill the department field");
            isValid = false;
        }

        if (spinnerSubject.getSelectedItemPosition() == 0) {
            ((TextView) spinnerSubject.getSelectedView()).setError("Please fill the subject field");
            isValid = false;
        }

        return isValid;
    }

    private void addTeacher() {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", etName.getText().toString().trim());
            jsonBody.put("email", etEmail.getText().toString().trim());
            jsonBody.put("phone", etPhone.getText().toString().trim());
            jsonBody.put("birth_date", selectedDate);


            String genderText = spinnerGender.getSelectedItem().toString();
            String gender = genderText.equals("male") ? "male" : "female";
            jsonBody.put("gender", gender);

            jsonBody.put("address", etAddress.getText().toString().trim());
            jsonBody.put("department", etDepartment.getText().toString().trim());
            jsonBody.put("bio", etBio.getText().toString().trim());


            int selectedSubjectIndex = spinnerSubject.getSelectedItemPosition();
            int subjectId = subjectIds.get(selectedSubjectIndex);
            jsonBody.put("subject_id", subjectId);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        try {
                            if (response.getBoolean("success")) {
                                showLoginInfo(response);
                                clearFields();
                            } else {
                                Toast.makeText(addTeacher_Activity.this,
                                        "خطأ: " + response.getString("error"),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(addTeacher_Activity.this,
                                    "خطأ في تحليل الاستجابة: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> {
                        String errorMsg = "خطأ في الشبكة";
                        if (error.networkResponse != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                JSONObject errorResponse = new JSONObject(responseBody);
                                errorMsg = errorResponse.getString("error");
                                if (errorResponse.has("details")) {
                                    errorMsg += "\nالتفاصيل: " + errorResponse.getString("details");
                                }
                            } catch (Exception e) {
                                errorMsg = "خطأ: " + error.toString();
                            }
                        }
                        Toast.makeText(addTeacher_Activity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            RequestQueue queue = Volley.newRequestQueue(addTeacher_Activity.this);
            queue.add(jsonRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(addTeacher_Activity.this, "خطأ JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoginInfo(JSONObject response) {
        try {
            String teacherName = etName.getText().toString().trim();
            String username = response.getString("username");
            String password = response.getString("password");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("user account created successfully!");
            builder.setMessage("teacher added successfully: " + teacherName + "\n\n" +
                    "log in info.:\n" +
                    "username: " + username + "\n" +
                    "password: " + password + "\n\n" );

            builder.setPositiveButton("Done", (dialog, which) -> dialog.dismiss());
            builder.setNegativeButton("copy user name", (dialog, which) -> {
                android.content.ClipboardManager clipboard =
                        (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Username", username);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "تم نسخ اسم المستخدم", Toast.LENGTH_SHORT).show();
            });

            builder.show();

        } catch (JSONException e) {
            Toast.makeText(this, "تم إضافة المعلم بنجاح!", Toast.LENGTH_LONG).show();
        }
    }

    private void clearFields() {
        etName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        etBirthDate.setText("");
        etAddress.setText("");
        etDepartment.setText("");
        etBio.setText("");
        spinnerGender.setSelection(0);
        spinnerSubject.setSelection(0);
        selectedDate = "";
    }
}