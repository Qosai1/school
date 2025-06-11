package com.example.finalproject;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAssignmentActivity extends AppCompatActivity {

    EditText titleInput, descriptionInput, dueDateInput;
    Button submitBtn, selectFileBtn, removeFileBtn;
    TextView selectedFileText;
    Spinner classSpinner;
    ArrayAdapter<String> spinnerAdapter;
    List<String> classList = new ArrayList<>();
    Map<String, Integer> classNameToIdMap = new HashMap<>();

    String url = "http://10.0.2.2/add_assignment.php";
    private static final String TAG = "AddAssignment";
    private static final int PERMISSION_REQUEST_CODE = 1;

    private Uri selectedFileUri = null;
    private String selectedFileName = "";
    private byte[] fileBytes = null;

    private ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        selectedFileUri = data.getData();
                        handleSelectedFile();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);

        try {
            initViews();
            setupClickListeners();
            checkPermissions();

            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            int teacherId = sharedPreferences.getInt("teacher_id", -1);

            if (teacherId == -1) {
                Toast.makeText(this, "Teacher ID not found. Please log in again.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // إضافة عنصر افتراضي للـ spinner لتجنب الخطأ
            classList.add("جاري التحميل...");
            updateSpinner();

            fetchTeacherClasses(teacherId);

            setupDatePicker();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "خطأ في تحميل الصفحة: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dueDateInput = findViewById(R.id.dueDateInput);
        classSpinner = findViewById(R.id.classSpinner);
        submitBtn = findViewById(R.id.submitBtn);
        selectFileBtn = findViewById(R.id.selectFileBtn);
        removeFileBtn = findViewById(R.id.removeFileBtn);
        selectedFileText = findViewById(R.id.selectedFileText);
    }

    private void setupClickListeners() {
        submitBtn.setOnClickListener(v -> addAssignment());
        selectFileBtn.setOnClickListener(v -> selectFile());
        removeFileBtn.setOnClickListener(v -> removeSelectedFile());
    }

    private void setupDatePicker() {
        dueDateInput.setInputType(InputType.TYPE_NULL);
        dueDateInput.setFocusable(false);
        dueDateInput.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddAssignmentActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        String date = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                        dueDateInput.setText(date);
                    }, year, month, day);

            datePickerDialog.show();
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // تم إصلاح هذا السطر
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(Intent.createChooser(intent, "اختر ملف"));
    }

    private void handleSelectedFile() {
        if (selectedFileUri != null) {
            try {
                selectedFileName = getFileName(selectedFileUri);
                InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
                if (inputStream != null) {
                    fileBytes = readBytes(inputStream);
                    selectedFileText.setText("تم اختيار: " + selectedFileName +
                            " (" + formatFileSize(fileBytes.length) + ")");
                    removeFileBtn.setVisibility(Button.VISIBLE);

                    Log.d(TAG, "File selected: " + selectedFileName + ", Size: " + fileBytes.length);
                } else {
                    Toast.makeText(this, "لا يمكن قراءة الملف", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error reading file: " + e.getMessage());
                Toast.makeText(this, "خطأ في قراءة الملف", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void removeSelectedFile() {
        selectedFileUri = null;
        selectedFileName = "";
        fileBytes = null;
        selectedFileText.setText("لم يتم اختيار ملف");
        removeFileBtn.setVisibility(Button.GONE);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting file name: " + e.getMessage());
            }
        }
        if (result == null && uri.getPath() != null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result != null ? result : "unknown_file";
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        inputStream.close();
        return byteBuffer.toByteArray();
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        else if (size < 1024 * 1024) return (size / 1024) + " KB";
        else return (size / (1024 * 1024)) + " MB";
    }

    private void clearFields() {
        titleInput.setText("");
        descriptionInput.setText("");
        dueDateInput.setText("");
        removeSelectedFile();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "تم منح الإذن", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "الإذن مطلوب لاختيار الملفات", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchTeacherClasses(int teacherId) {
        String classUrl = "http://10.0.2.2/get_teacher_classes.php";

        StringRequest request = new StringRequest(Request.Method.POST, classUrl,
                response -> {
                    Log.d(TAG, "Classes Response: " + response);

                    try {
                        response = response.trim();

                        if (response.isEmpty()) {
                            Log.w(TAG, "Empty response received");
                            showNoClassesMessage();
                            return;
                        }

                        if (response.startsWith("[")) {
                            JSONArray jsonArray = new JSONArray(response);

                            if (jsonArray.length() == 0) {
                                showNoClassesMessage();
                                return;
                            }

                            classList.clear();
                            classNameToIdMap.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                int id = obj.getInt("id");
                                String name = obj.getString("name");
                                classList.add(name);
                                classNameToIdMap.put(name, id);
                            }

                            runOnUiThread(this::updateSpinner);

                        } else if (response.startsWith("{")) {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.has("error")) {
                                String error = jsonObject.getString("error");
                                Log.e(TAG, "Server error: " + error);
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "خطأ: " + error, Toast.LENGTH_SHORT).show();
                                    showNoClassesMessage();
                                });
                            } else {
                                Log.e(TAG, "Unexpected JSON object structure");
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "استجابة غير متوقعة من الخادم", Toast.LENGTH_SHORT).show();
                                    showNoClassesMessage();
                                });
                            }

                        } else {
                            Log.e(TAG, "Invalid response format: " + response);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "استجابة غير صالحة من الخادم", Toast.LENGTH_SHORT).show();
                                showNoClassesMessage();
                            });
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage(), e);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "خطأ في تحليل البيانات: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            showNoClassesMessage();
                        });
                    }
                },
                error -> {
                    Log.e(TAG, "Network Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                        String errorResponse = new String(error.networkResponse.data);
                        Log.e(TAG, "Error Response: " + errorResponse);
                    }
                    runOnUiThread(() -> {
                        Toast.makeText(this, "فشل الاتصال بالخادم", Toast.LENGTH_SHORT).show();
                        showNoClassesMessage();
                    });
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("teacher_id", String.valueOf(teacherId));
                Log.d(TAG, "Sending teacher_id: " + teacherId);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void showNoClassesMessage() {
        classList.clear();
        classList.add("لا توجد صفوف متاحة");
        classNameToIdMap.clear();
        classNameToIdMap.put("لا توجد صفوف متاحة", -1);
        updateSpinner();
    }

    private void updateSpinner() {
        try {
            if (classList.isEmpty()) {
                classList.add("لا توجد صفوف متاحة");
                classNameToIdMap.put("لا توجد صفوف متاحة", -1);
            }

            spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classList);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            classSpinner.setAdapter(spinnerAdapter);

            Log.d(TAG, "Spinner updated with " + classList.size() + " items");
        } catch (Exception e) {
            Log.e(TAG, "Error updating spinner: " + e.getMessage(), e);
        }
    }

    private void addAssignment() {
        try {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String dueDate = dueDateInput.getText().toString().trim();

            if (classList.isEmpty() || classSpinner.getSelectedItem() == null) {
                Toast.makeText(this, "لا توجد صفوف متاحة", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedClassName = classSpinner.getSelectedItem().toString();

            if (selectedClassName.equals("لا توجد صفوف متاحة") || selectedClassName.equals("جاري التحميل...")) {
                Toast.makeText(this, "يرجى اختيار صف صالح", Toast.LENGTH_SHORT).show();
                return;
            }

            Integer classId = classNameToIdMap.get(selectedClassName);
            if (classId == null || classId == -1) {
                Toast.makeText(this, "خطأ في ID الصف المحدد", Toast.LENGTH_SHORT).show();
                return;
            }

            if (title.isEmpty() || description.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            int teacherId = sharedPreferences.getInt("teacher_id", -1);

            if (teacherId == -1) {
                Toast.makeText(this, "خطأ في معرف المدرس. يرجى تسجيل الدخول مرة أخرى", Toast.LENGTH_SHORT).show();
                return;
            }

            sendAssignmentRequest(title, description, dueDate, classId, teacherId);

        } catch (Exception e) {
            Log.e(TAG, "Error in addAssignment: " + e.getMessage(), e);
            Toast.makeText(this, "خطأ في إضافة الواجب: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendAssignmentRequest(String title, String description, String dueDate, int classId, int teacherId) {
        Log.d(TAG, "Sending request to: " + url);
        Log.d(TAG, "Class ID: " + classId + ", Teacher ID: " + teacherId);
        Log.d(TAG, "Has file: " + (fileBytes != null ? "Yes (" + fileBytes.length + " bytes)" : "No"));

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d(TAG, "Assignment Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if ("success".equals(status)) {
                            Toast.makeText(this, "تم إضافة الواجب بنجاح!", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(this, "خطأ: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        Toast.makeText(this, "استجابة غير متوقعة من الخادم", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Network Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Error Response: " + new String(error.networkResponse.data));
                    }
                    Toast.makeText(this, "خطأ في الاتصال بالخادم", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("description", description);
                params.put("due_date", dueDate);
                params.put("classID", String.valueOf(classId));
                params.put("teacher_id", String.valueOf(teacherId));
                params.put("subject_id", "1");

                if (fileBytes != null) {
                    params.put("has_attachment", "1");
                    params.put("file_name", selectedFileName);
                    params.put("file_data", android.util.Base64.encodeToString(fileBytes, android.util.Base64.DEFAULT));
                } else {
                    params.put("has_attachment", "0");
                }

                Log.d(TAG, "Params: " + params.toString());
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}