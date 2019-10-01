package tpt.igr203.alita.todolist.BDD.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import tpt.igr203.alita.todolist.BDD.Event;

/**
 * Created by RIQUIER_Melvin on 30/03/2019.
 */

@Dao
public interface EventDAO {

    @Query("SELECT * FROM Event")
    List<Event> getAllEvent();

    @Query("SELECT * FROM Event WHERE listId = :listId")
    List<Event> getEventFromList(int listId);

    @Query("SELECT * FROM Event WHERE id = :eventId")
    Event getEvent(int eventId);

    @Insert
    long insertEvent(Event event);

    @Update
    int updateEvent(Event event);

    @Query("DELETE FROM Event WHERE id = :eventId")
    int deleteEvent(int eventId);

    @Query("DELETE FROM Event WHERE listId = :listId")
    int deleteEventFromList(int listId);
}