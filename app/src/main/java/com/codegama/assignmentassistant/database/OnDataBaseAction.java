package com.codegama.assignmentassistant.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.codegama.assignmentassistant.model.Assignment;

import java.util.List;

@Dao
public interface OnDataBaseAction {

    @Query("SELECT * FROM Assignment")
    List<Assignment> getAllAssignmentsList();

    @Query("DELETE FROM Assignment")
    void truncateTheList();

    @Insert
    void insertDataIntoAssignmentList(Assignment assignment);

    @Query("DELETE FROM Assignment WHERE assignmentId = :assignmentId")
    void deleteAssignmentFromId(int assignmentId);

    @Query("SELECT * FROM Assignment WHERE assignmentId = :assignmentId")
    Assignment selectDataFromAnId(int assignmentId);

    @Query("UPDATE Assignment SET assignmentTitle = :assignmentTitle, assignmentDescription = :assignmentDescription, date = :assignmentDate, " +
            "lastAlarm = :assignmentTime, event = :assignmentEvent WHERE assignmentId = :assignmentId")
    void updateAnExistingRow(int assignmentId, String assignmentTitle, String assignmentDescription , String assignmentDate, String assignmentTime,
                            String assignmentEvent);

}
