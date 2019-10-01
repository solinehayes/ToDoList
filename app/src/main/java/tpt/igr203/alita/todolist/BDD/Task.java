package tpt.igr203.alita.todolist.BDD;

import android.arch.persistence.room.*;

/**
 * Created by RIQUIER_Melvin on 28/03/2019.
 */

@Entity(foreignKeys = @ForeignKey(entity = ToDoList.class, parentColumns = "id", childColumns = "listId"))
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int id; //Clef primaire

    private String name;

    private int listId; //Clef étrangère

    private String notes;

    private String deadline; //Format "AAAA/MM/JJ/HH/MM"

    private int importance;

    private boolean showInCalendar;

    private boolean finished;

    private boolean hasDeadline;

    public Task(int listId) {
        this.name = "";
        this.listId = listId;
        this.notes = "";
        this.deadline = "";
        this.importance = 0;
        this.showInCalendar = false;
        this.finished = false;
        this.hasDeadline = false;
    }

    //GETTERS
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getListId() {
        return listId;
    }

    public String getNotes() {
        return notes;
    }

    public String getDeadline() {
        return deadline;
    }

    public boolean isShowInCalendar() {
        return showInCalendar;
    }

    public int getImportance() {
        return importance;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean hasDeadline() { return hasDeadline; }

    //SETTERS
    public void setId(int id) { this.id = id; }

    public void setName(String name) {
        this.name = name;
    }

    public void setListId(int listID) {
        this.listId = listID;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public void setShowInCalendar(boolean showInCalendar) {
        this.showInCalendar = showInCalendar;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setHasDeadline(boolean hasDeadline) { this.hasDeadline = hasDeadline; }
}
