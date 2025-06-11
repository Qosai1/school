package com.example.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder> {

    private List<Subject> subjectsList;
    private OnSubjectClickListener onSubjectClickListener;

    public interface OnSubjectClickListener {
        void onSubjectClick(Subject subject);
    }

    public SubjectsAdapter(List<Subject> subjectsList, OnSubjectClickListener listener) {
        this.subjectsList = subjectsList;
        this.onSubjectClickListener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjectsList.get(position);
        holder.subjectNameTextView.setText(subject.getSubjectName());



        holder.itemView.setOnClickListener(v -> {
            if (onSubjectClickListener != null) {
                onSubjectClickListener.onSubjectClick(subject);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectsList.size();
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView subjectNameTextView;
        TextView subjectDescriptionTextView;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectNameTextView = itemView.findViewById(R.id.subjectNameTextView);
        }
    }
}