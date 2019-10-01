package tpt.igr203.alita.todolist.BDD.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;

import java.util.List;

import tpt.igr203.alita.todolist.BDD.ToDoList;

/**
 * Created by RIQUIER_Melvin on 28/03/2019.
 * DAO d'une TODOList. Gère toutes les requêtes qu'on pourrait effectuer sur les TODOList à savoir :
 * _ Récupérer toutes les TODOList
 */

@Dao
public interface ToDoListDAO {

    @Query("SELECT * FROM ToDoList")
    List<ToDoList> getAllLists();

    @Query("SELECT * FROM ToDoList WHERE id = :listId")
    ToDoList getList(int listId);

    @Insert
    long insertList(ToDoList l);

    @Update
    int updateList(ToDoList l);

    @Query("DELETE FROM ToDoList WHERE id = :listId")
    int deleteList(int listId);

}
