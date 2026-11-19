package com.example.cuoiki.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuoiki.R;
import com.example.cuoiki.model.Goal;

import java.util.ArrayList;
import java.util.Locale;

public class GoalAdapter
        extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    public interface OnGoalActionListener {

        void onEdit(Goal goal);

        void onAddAmount(Goal goal);

        void onDelete(Goal goal);
    }

    private final ArrayList<Goal> goalList;
    private final OnGoalActionListener listener;

    public GoalAdapter(
            ArrayList<Goal> goalList,
            OnGoalActionListener listener
    ) {
        this.goalList = goalList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_goal,
                        parent,
                        false
                );

        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull GoalViewHolder holder,
            int position
    ) {

        Goal goal = goalList.get(position);

        holder.txtGoalTitle.setText(
                goal.getTitle()
        );

        holder.txtGoalAmount.setText(
                String.format(
                        Locale.getDefault(),
                        "%,.0f / %,.0f VNĐ",
                        goal.getCurrentAmount(),
                        goal.getTargetAmount()
                )
        );

        holder.txtGoalDeadline.setText(
                "Hạn: " + goal.getDeadline()
        );

        int progress = 0;

        if (goal.getTargetAmount() > 0) {

            progress = (int) (
                    goal.getCurrentAmount()
                            * 100
                            / goal.getTargetAmount()
            );
        }

        // Không cho vượt quá 100%
        progress = Math.max(
                0,
                Math.min(progress, 100)
        );

        holder.progressGoal.setProgress(progress);

        holder.txtProgressPercent.setText(
                progress + "%"
        );

        holder.btnEditGoal.setOnClickListener(v ->
                listener.onEdit(goal)
        );

        holder.btnAddGoalAmount.setOnClickListener(v ->
                listener.onAddAmount(goal)
        );

        holder.btnDeleteGoal.setOnClickListener(v ->
                listener.onDelete(goal)
        );
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    public static class GoalViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtGoalTitle;
        TextView txtGoalAmount;
        TextView txtGoalDeadline;
        TextView txtProgressPercent;

        ProgressBar progressGoal;

        ImageButton btnEditGoal;
        ImageButton btnAddGoalAmount;
        ImageButton btnDeleteGoal;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);

            txtGoalTitle =
                    itemView.findViewById(R.id.txtGoalTitle);

            txtGoalAmount =
                    itemView.findViewById(R.id.txtGoalAmount);

            txtGoalDeadline =
                    itemView.findViewById(R.id.txtGoalDeadline);

            txtProgressPercent =
                    itemView.findViewById(R.id.txtProgressPercent);

            progressGoal =
                    itemView.findViewById(R.id.progressGoal);

            btnEditGoal =
                    itemView.findViewById(R.id.btnEditGoal);

            btnAddGoalAmount =
                    itemView.findViewById(R.id.btnAddGoalAmount);

            btnDeleteGoal =
                    itemView.findViewById(R.id.btnDeleteGoal);
        }
    }
}