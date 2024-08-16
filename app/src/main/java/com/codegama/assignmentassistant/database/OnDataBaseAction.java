package com.codegama.assignmentassistant.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.codegama.assignmentassistant.model.Assignment;

import java.util.List;

@Dao
public interface OnDataBaseAction {

    @Query("SELECT * FROM assignments")
    List<Assignment> getAllAssignmentsList();

    @Query("DELETE FROM assignments")
    void truncateTheList();

    @Insert
    void insertDataIntoAssignmentList(Assignment assignment);

    @Query("DELETE FROM assignments WHERE assignmentId = :assignmentId")
    void deleteAssignmentFromId(int assignmentId);

    @Query("SELECT * FROM assignments WHERE assignmentId = :assignmentId")
    Assignment selectDataFromAnId(int assignmentId);

    @Query("UPDATE assignments SET assignmentTitle = :assignmentTitle, assignmentDescrption = :assignmentDescription, date = :assignmentDueDate, " +
            "lastAlarm = :assignmentTime, event = :assignmentEvent WHERE assignmentId = :assignmentId")
    void updateAnExistingRow(int assignmentId, String assignmentTitle, String assignmentDescription , String assignmentDueDate, String assignmentTime,
                            String assignmentEvent);

}


