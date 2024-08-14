package com.codegama.assignmentassistant.activity;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codegama.assignmentassistant.R;
import com.codegama.assignmentassistant.adapter.AssignmentAdapter;
import com.codegama.assignmentassistant.bottomSheetFragment.CreateAssignmentBottomSheetFragment;
import com.codegama.assignmentassistant.bottomSheetFragment.ShowCalendarViewBottomSheet;
import com.codegama.assignmentassistant.broadcastReceiver.AlarmBroadcastReceiver;
import com.codegama.assignmentassistant.database.DatabaseClient;
import com.codegama.assignmentassistant.model.Assignment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements CreateAssignmentBottomSheetFragment.setRefreshListener {

    @BindView(R.id.assignmentRecycler)
    RecyclerView assignmentRecycler;
    @BindView(R.id.addAssignment)
    TextView addAssignment;
    AssignmentAdapter assignmentAdapter;
    List<Assignment> assignments = new ArrayList<>();
    @BindView(R.id.noDataImage)
    ImageView noDataImage;
    @BindView(R.id.calendar)
    ImageView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpAdapter();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ComponentName receiver = new ComponentName(this, AlarmBroadcastReceiver.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Glide.with(getApplicationContext()).load(R.drawable.first_note).into(noDataImage);

        addAssignment.setOnClickListener(view -> {
            CreateAssignmentBottomSheetFragment createAssignmentBottomSheetFragment = new CreateAssignmentBottomSheetFragment();
            createAssignmentBottomSheetFragment.setAssignmentId(0, false, this, MainActivity.this);
            createAssignmentBottomSheetFragment.show(getSupportFragmentManager(), createAssignmentBottomSheetFragment.getTag());
        });

        getSavedAssignments();

        calendar.setOnClickListener(view -> {
            ShowCalendarViewBottomSheet showCalendarViewBottomSheet = new ShowCalendarViewBottomSheet();
            showCalendarViewBottomSheet.show(getSupportFragmentManager(), showCalendarViewBottomSheet.getTag());
        });
    }

    public void setUpAdapter() {
        assignmentAdapter = new AssignmentAdapter(this, assignments, this);
        assignmentRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        assignmentRecycler.setAdapter(assignmentAdapter);
    }

    private void getSavedAssignments() {

        class GetSavedAssignments extends AsyncTask<Void, Void, List<Assignment>> {
            @Override
            protected List<Assignment> doInBackground(Void... voids) {
                assignments = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .dataBaseAction()
                        .getAllAssignmentsList();
                return assignments;
            }

            @Override
            protected void onPostExecute(List<Assignment> assignments) {
                super.onPostExecute(assignments);
                noDataImage.setVisibility(assignments.isEmpty() ? View.VISIBLE : View.GONE);
                setUpAdapter();
            }
        }

        GetSavedAssignments savedAssignments = new GetSavedAssignments();
        savedAssignments.execute();
    }

    @Override
    public void refresh() {
        getSavedAssignments();
    }
}


