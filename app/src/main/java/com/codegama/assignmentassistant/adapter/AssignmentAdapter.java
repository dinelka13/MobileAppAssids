/*package com.codegama.assignmentassistant.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codegama.assignmentassistant.R;
import com.codegama.assignmentassistant.activity.MainActivity;
import com.codegama.assignmentassistant.bottomSheetFragment.CreateAssignmentBottomSheetFragment;
import com.codegama.assignmentassistant.database.DatabaseClient;
import com.codegama.assignmentassistant.model.Assignment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private MainActivity context;
    private LayoutInflater inflater;
    private List<Assignment> assignmentList;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy", Locale.US);
    public SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-M-yyyy", Locale.US);
    Date date = null;
    String outputDateString = null;
    CreateAssignmentBottomSheetFragment.setRefreshListener setRefreshListener;

    public AssignmentAdapter(MainActivity context, List<Assignment> assignmentList, CreateAssignmentBottomSheetFragment.setRefreshListener setRefreshListener) {
        this.context = context;
        this.assignmentList = assignmentList;
        this.setRefreshListener = setRefreshListener;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.item_assignment, viewGroup, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);
        holder.title.setText(assignment.getAssignmentTitle());
        holder.description.setText(assignment.getAssignmentDescrption());
        holder.time.setText(assignment.getLastAlarm());
        holder.status.setText(assignment.isComplete() ? "COMPLETED" : "UPCOMING");
        holder.options.setOnClickListener(view -> showPopUpMenu(view, position));
        //holder.progressBar.setMax(10);
        //holder.progressBar.setProgress(3);
        try {
            date = inputDateFormat.parse(assignment.getDate());
            outputDateString = dateFormat.format(date);

            String[] items1 = outputDateString.split(" ");
            String day = items1[0];
            String dd = items1[1];
            String month = items1[2];

            holder.day.setText(day);
            holder.date.setText(dd);
            holder.month.setText(month);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPopUpMenu(View view, int position) {
        final Assignment assignment = assignmentList.get(position);
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDelete:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                    alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sureToDelete).
                            setPositiveButton(R.string.yes, (dialog, which) -> {
                                deleteAssignmentFromId(assignment.getAssignmentId(), position);
                            })
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    break;
                case R.id.menuUpdate:
                    CreateAssignmentBottomSheetFragment createAssignmentBottomSheetFragment = new CreateAssignmentBottomSheetFragment();
                    createAssignmentBottomSheetFragment.setAssignmentId(assignment.getAssignmentId(), true, context, context);
                    createAssignmentBottomSheetFragment.show(context.getSupportFragmentManager(), createAssignmentBottomSheetFragment.getTag());
                    break;
                case R.id.menuComplete:
                    AlertDialog.Builder completeAlertDialog = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                    completeAlertDialog.setTitle(R.string.confirmation).setMessage(R.string.sureToMarkAsComplete).
                            setPositiveButton(R.string.yes, (dialog, which) -> showCompleteDialog(assignment.getAssignmentId(), position))
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    public void showCompleteDialog(int assignmentId, int position) {
        Dialog dialog = new Dialog(context, R.style.AppTheme);
        dialog.setContentView(R.layout.dialog_completed_theme);
        Button close = dialog.findViewById(R.id.closeButton);
        close.setOnClickListener(view -> {
            deleteAssignmentFromId(assignmentId, position);
            dialog.dismiss();
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }


    private void deleteAssignmentFromId(int assignmentId, int position) {
        class GetSavedAssignments extends AsyncTask<Void, Void, List<Assignment>> {
            @Override
            protected List<Assignment> doInBackground(Void... voids) {
                DatabaseClient.getInstance(context)
                        .getAppDatabase()
                        .dataBaseAction()
                        .deleteAssignmentFromId(assignmentId);

                return assignmentList;
            }

            @Override
            protected void onPostExecute(List<Assignment> assignments) {
                super.onPostExecute(assignments);
                removeAtPosition(position);
                setRefreshListener.refresh();
            }
        }
        GetSavedAssignments savedAssignments = new GetSavedAssignments();
        savedAssignments.execute();
    }

    private void removeAtPosition(int position) {
        assignmentList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, assignmentList.size());
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public class AssignmentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.day)
        TextView day;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.month)
        TextView month;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.options)
        ImageView options;
        @BindView(R.id.time)
        TextView time;
      //  @BindView(R.id.progressBar)
        // ProgressBar progressBar;

        AssignmentViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}*/

package com.codegama.assignmentassistant.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codegama.assignmentassistant.R;
import com.codegama.assignmentassistant.activity.MainActivity;
import com.codegama.assignmentassistant.bottomSheetFragment.CreateAssignmentBottomSheetFragment;
import com.codegama.assignmentassistant.database.DatabaseClient;
import com.codegama.assignmentassistant.model.Assignment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private MainActivity context;
    private LayoutInflater inflater;
    private List<Assignment> assignmentList;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy", Locale.US);
    public SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-M-yyyy", Locale.US);
    Date date = null;
    String outputDateString = null;
    CreateAssignmentBottomSheetFragment.setRefreshListener setRefreshListener;

    public AssignmentAdapter(MainActivity context, List<Assignment> assignmentList, CreateAssignmentBottomSheetFragment.setRefreshListener setRefreshListener) {
        this.context = context;
        this.assignmentList = assignmentList;
        this.setRefreshListener = setRefreshListener;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.item_assignment, viewGroup, false);

        return new AssignmentViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);

        // Set other views
        holder.title.setText(assignment.getAssignmentTitle());
        holder.description.setText(assignment.getAssignmentDescrption());
        holder.time.setText(assignment.getLastAlarm());
        holder.status.setText(assignment.isComplete() ? "COMPLETED" : "UPCOMING");

        // Update ProgressBar
        int totalQuestions = assignment.getTotalQuestions();
        int completedQuestions = assignment.getCompletedQuestions();
        holder.progressBar.setMax(totalQuestions);
        holder.progressBar.setProgress(completedQuestions);

        // Determine color based on progress
        int progressPercentage = (int) (((double) completedQuestions / totalQuestions) * 100);
        int progressColor;

        if (progressPercentage >= 80) {
            progressColor = ContextCompat.getColor(context, R.color.progress_green);
        } else if (progressPercentage >= 50) {
            progressColor = ContextCompat.getColor(context, R.color.progress_amber);
        } else {
            progressColor = ContextCompat.getColor(context, R.color.progress_red);
        }


        holder.progressBar.setProgressTintList(ColorStateList.valueOf(progressColor));

        // Date formatting
        try {
            Date date = inputDateFormat.parse(assignment.getDate());
            String outputDateString = dateFormat.format(date);
            String[] items1 = outputDateString.split(" ");
            String day = items1[0];
            String dd = items1[1];
            String month = items1[2];

            holder.day.setText(day);
            holder.date.setText(dd);
            holder.month.setText(month);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPopUpMenu(View view, int position) {
        final Assignment assignment = assignmentList.get(position);
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDelete:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                    alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sureToDelete)
                            .setPositiveButton(R.string.yes, (dialog, which) -> deleteAssignmentFromId(assignment.getAssignmentId(), position))
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    break;
                case R.id.menuUpdate:
                    CreateAssignmentBottomSheetFragment createAssignmentBottomSheetFragment = new CreateAssignmentBottomSheetFragment();
                    createAssignmentBottomSheetFragment.setAssignmentId(assignment.getAssignmentId(), true, context, context);
                    createAssignmentBottomSheetFragment.show(context.getSupportFragmentManager(), createAssignmentBottomSheetFragment.getTag());
                    break;
                case R.id.menuComplete:
                    AlertDialog.Builder completeAlertDialog = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                    completeAlertDialog.setTitle(R.string.confirmation).setMessage(R.string.sureToMarkAsComplete)
                            .setPositiveButton(R.string.yes, (dialog, which) -> showCompleteDialog(assignment.getAssignmentId(), position))
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    public void showCompleteDialog(int assignmentId, int position) {
        Dialog dialog = new Dialog(context, R.style.AppTheme);
        dialog.setContentView(R.layout.dialog_completed_theme);
        Button close = dialog.findViewById(R.id.closeButton);
        close.setOnClickListener(view -> {
            deleteAssignmentFromId(assignmentId, position);
            dialog.dismiss();
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    private void deleteAssignmentFromId(int assignmentId, int position) {
        class GetSavedAssignments extends AsyncTask<Void, Void, List<Assignment>> {
            @Override
            protected List<Assignment> doInBackground(Void... voids) {
                DatabaseClient.getInstance(context)
                        .getAppDatabase()
                        .dataBaseAction()
                        .deleteAssignmentFromId(assignmentId);

                return assignmentList;
            }

            @Override
            protected void onPostExecute(List<Assignment> assignments) {
                super.onPostExecute(assignments);
                removeAtPosition(position);
                setRefreshListener.refresh();
            }
        }
        GetSavedAssignments savedAssignments = new GetSavedAssignments();
        savedAssignments.execute();
    }

    private void removeAtPosition(int position) {
        assignmentList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, assignmentList.size());
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public class AssignmentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.day)
        TextView day;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.month)
        TextView month;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.options)
        ImageView options;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.progressBar)
        ProgressBar progressBar; // Bind ProgressBar here

        AssignmentViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
