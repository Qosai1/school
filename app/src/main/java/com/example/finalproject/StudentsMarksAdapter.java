package com.example.finalproject;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentsMarksAdapter extends RecyclerView.Adapter<StudentsMarksAdapter.ViewHolder> {
    private List<StudentMarkItem> studentsList;

    public StudentsMarksAdapter(List<StudentMarkItem> studentsList) {
        this.studentsList = studentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_mark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentMarkItem student = studentsList.get(position);
        holder.bind(student, position);
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentNameText;
        EditText markInput;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            studentNameText = itemView.findViewById(R.id.studentNameText);
            markInput = itemView.findViewById(R.id.markInput);
        }

        public void bind(StudentMarkItem student, int position) {
            studentNameText.setText(student.getStudentName());
            markInput.setText(student.getMark());

            markInput.setTag(null);

            markInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (markInput.getTag() == null) {
                        markInput.setTag("updating");
                        student.setMark(s.toString());
                        markInput.setTag(null);
                    }
                }
            });
        }
    }
}