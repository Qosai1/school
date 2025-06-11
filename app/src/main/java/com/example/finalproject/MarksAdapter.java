package com.example.finalproject;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MarksAdapter extends RecyclerView.Adapter<MarksAdapter.MarkViewHolder> {

    private List<Mark> marksList;

    public MarksAdapter(List<Mark> marksList) {
        this.marksList = marksList;
    }

    @NonNull
    @Override
    public MarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mark, parent, false);
        return new MarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkViewHolder holder, int position) {
        Mark mark = marksList.get(position);
        holder.bind(mark);
    }

    @Override
    public int getItemCount() {
        return marksList.size();
    }

    public class MarkViewHolder extends RecyclerView.ViewHolder {
        private TextView subjectNameTextView;
        private TextView examNameTextView;
        private TextView markTextView;

        public MarkViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectNameTextView = itemView.findViewById(R.id.subjectNameTextView);
            examNameTextView = itemView.findViewById(R.id.examNameTextView);
            markTextView = itemView.findViewById(R.id.markTextView);
        }

        public void bind(Mark mark) {
            subjectNameTextView.setText(mark.getSubjectName());
            examNameTextView.setText(mark.getExamName());
            markTextView.setText(String.valueOf(mark.getMark()));

            // تلوين العلامة حسب الدرجة
            double markValue = mark.getMark();
            if (markValue >= 90) {
                markTextView.setTextColor(Color.parseColor("#4CAF50")); // أخضر
            } else if (markValue >= 80) {
                markTextView.setTextColor(Color.parseColor("#FF9800")); // برتقالي
            } else if (markValue >= 70) {
                markTextView.setTextColor(Color.parseColor("#FFC107")); // أصفر
            } else if (markValue >= 60) {
                markTextView.setTextColor(Color.parseColor("#FF5722")); // أحمر فاتح
            } else {
                markTextView.setTextColor(Color.parseColor("#F44336")); // أحمر غامق
            }
        }
    }
}