package tpt.igr203.alita.todolist.BDD.DAO;

import android.arch.persistence.room.*;

import java.util.List;

import tpt.igr203.alita.todolist.BDD.Task;

/**
 * Created by RIQUIER_Melvin on 30/03/2019.
 */

@Dao
public interface TaskDAO {

    @Query("SELECT * FROM Task")
    List<Task> getAllTasks();

    @Query("SELECT * FROM Task WHERE listId = :listId")
    List<Task> getTaskFromList(int listId);

    @Query("SELECT * FROM Task WHERE id = :taskId")
    Task getTask(int taskId);

    @Insert
    long insertTask(Task task);

    @Update
    int updateTask(Task task);

    @Query("DELETE FROM Task WHERE id = :taskId")
    int deleteTask(int taskId);

    @Query("DELETE FROM Task WHERE listId = :listId")
    int deleteTaskFromList(int listId);
}
