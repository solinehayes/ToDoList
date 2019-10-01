package tpt.igr203.alita.todolist.BDD;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by RIQUIER_Melvin on 30/03/2019.
 */


@Entity(foreignKeys = @ForeignKey(entity = ToDoList.class, parentColumns = "id", childColumns = "listId"))
public class Memo {

    @PrimaryKey(autoGenerate = true)
    private int id; //Clef primaire

    private String name;

    private int listId; //Clef étrangère

    private String memo;

    public Memo(int listId) {
        this.name = "";
        this.listId = listId;
        this.memo = "";
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

    public String getMemo() {
        return memo;
    }

    //SETTERS


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setListId(int listID) {
        this.listId = listID;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
