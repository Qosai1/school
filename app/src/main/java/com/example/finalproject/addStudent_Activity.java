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
import java.util.Calendar;
import java.util.Locale;

public class addStudent_Activity extends AppCompatActivity {

    EditText etName, etEmail, etPhone, etBirthDate, etAddress, classid;
    Spinner spinnerGender;
    Button btnAddStudent;
    String url = "http://10.0.2.2/add_studnt.php";
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_studnet_activity);

        // ربط الحقول
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etBirthDate = findViewById(R.id.etBirthDate);
        etAddress = findViewById(R.id.etAddress);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        classid = findViewById(R.id.classid);

        setupInputValidation();
        setupGenderSpinner();
        setupDatePicker();

        btnAddStudent.setOnClickListener(v -> {
            if (validateFields()) {
                addStudent();
            }
        });
    }

    private void setupInputValidation() {

        etName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);


        etPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        etPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});


        classid.setInputType(InputType.TYPE_CLASS_NUMBER);
        classid.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});


        etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    private void setupGenderSpinner() {

        String[] genderOptions = {"Select Gender", "Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, genderOptions) {
            @Override
            public boolean isEnabled(int position) {

                return position != 0;
            }


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

    private void setupDatePicker() {

        etBirthDate.setFocusable(false);
        etBirthDate.setClickable(true);
        etBirthDate.setHint("اختر تاريخ الميلاد");

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
            etName.setError("الرجاء إدخال الاسم");
            isValid = false;
        } else if (!name.matches("^[a-zA-Zأ-ي\\s]+$")) {
            etName.setError("الاسم يجب أن يحتوي على أحرف فقط");
            isValid = false;
        }


        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            etEmail.setError("الرجاء إدخال البريد الإلكتروني");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("البريد الإلكتروني غير صحيح");
            isValid = false;
        }


        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            etPhone.setError("الرجاء إدخال رقم الهاتف");
            isValid = false;
        } else if (phone.length() != 10) {
            etPhone.setError("رقم الهاتف يجب أن يكون 10 خانات");
            isValid = false;
        } else if (!phone.matches("^[0-9]+$")) {
            etPhone.setError("رقم الهاتف يجب أن يحتوي على أرقام فقط");
            isValid = false;
        }

        if (selectedDate.isEmpty()) {
            etBirthDate.setError("الرجاء اختيار تاريخ الميلاد");
            isValid = false;
        }


        if (spinnerGender.getSelectedItemPosition() == 0) {
            ((TextView) spinnerGender.getSelectedView()).setError("الرجاء اختيار الجنس");
            isValid = false;
        }


        if (etAddress.getText().toString().trim().isEmpty()) {
            etAddress.setError("الرجاء إدخال العنوان");
            isValid = false;
        }

        String classIdStr = classid.getText().toString().trim();
        if (classIdStr.isEmpty()) {
            classid.setError("الرجاء إدخال رقم الصف");
            isValid = false;
        } else {
            try {
                int classIdInt = Integer.parseInt(classIdStr);
                if (classIdInt < 1 || classIdInt > 12) {
                    classid.setError("رقم الصف يجب أن يكون بين 1 و 12");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                classid.setError("الرجاء إدخال رقم صحيح للصف");
                isValid = false;
            }
        }

        return isValid;
    }

    private void addStudent() {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", etName.getText().toString().trim());
            jsonBody.put("email", etEmail.getText().toString().trim());
            jsonBody.put("phone", etPhone.getText().toString().trim());
            jsonBody.put("birth_date", selectedDate);
            jsonBody.put("gender", spinnerGender.getSelectedItem().toString().toLowerCase());
            jsonBody.put("address", etAddress.getText().toString().trim());
            jsonBody.put("classID", Integer.parseInt(classid.getText().toString().trim()));

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
                                Toast.makeText(addStudent_Activity.this,
                                        "خطأ: " + response.getString("error"),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(addStudent_Activity.this,
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
                        Toast.makeText(addStudent_Activity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            RequestQueue queue = Volley.newRequestQueue(addStudent_Activity.this);
            queue.add(jsonRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(addStudent_Activity.this, "خطأ JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoginInfo(JSONObject response) {
        try {
            String studentName = etName.getText().toString().trim();
            String username = response.getString("username");
            String password = response.getString("password");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("تم إنشاء الحساب بنجاح!");
            builder.setMessage("تم إضافة الطالب: " + studentName + "\n\n" +
                    "معلومات تسجيل الدخول:\n" +
                    "اسم المستخدم: " + username + "\n" +
                    "كلمة المرور: " + password + "\n\n" +
                    "يرجى حفظ هذه المعلومات وإعطاؤها للطالب.");

            builder.setPositiveButton("حسناً", (dialog, which) -> dialog.dismiss());
            builder.setNegativeButton("نسخ اسم المستخدم", (dialog, which) -> {
                android.content.ClipboardManager clipboard =
                        (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Username", username);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "تم نسخ اسم المستخدم", Toast.LENGTH_SHORT).show();
            });

            builder.show();

        } catch (JSONException e) {
            Toast.makeText(this, "تم إضافة الطالب بنجاح!", Toast.LENGTH_LONG).show();
        }
    }

    private void clearFields() {
        etName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        etBirthDate.setText("");
        etAddress.setText("");
        classid.setText("");
        spinnerGender.setSelection(0);
        selectedDate = "";
    }
}