package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    private Context context;
    private List<Assignment> assignmentList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public AssignmentAdapter(Context context, List<Assignment> assignmentList) {
        this.context = context;
        this.assignmentList = assignmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_assignment2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);

        holder.title.setText(assignment.title != null ? assignment.title : "No Title");
        holder.description.setText(assignment.description != null ? assignment.description : "No Description");

        // Format date properly
        if (assignment.due_date != null) {
            holder.dueDate.setText("Due: " + dateFormat.format(assignment.due_date));
        } else {
            holder.dueDate.setText("No Due Date");
        }

        holder.btnShowWhoSubmitted.setOnClickListener(v -> {
            Intent intent = new Intent(context, AssignmentSubmissionsActivity.class);
            intent.putExtra("assignment_id", assignment.assignment_id);
            intent.putExtra("assignment_title", assignment.title);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList != null ? assignmentList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, dueDate;
        Button btnShowWhoSubmitted;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.assignmentTitle);
            description = itemView.findViewById(R.id.assignmentDescription);
            dueDate = itemView.findViewById(R.id.assignmentDueDate);
            btnShowWhoSubmitted = itemView.findViewById(R.id.btnShowWhoSubmitted);
        }
    }
}