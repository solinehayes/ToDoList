package tpt.igr203.alita.todolist.BDD;

import android.arch.persistence.room.*;

import java.util.List;

/**
 * Created by RIQUIER_Melvin on 28/03/2019.
 */

@Entity
public class ToDoList {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;

    private String color;

    public ToDoList(){
        this.name = "";
        this.color = "blue";

    }

    //GETTERS
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor(){
        return color;
    }

    //SETTERS
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color){
        this.color = color;
    }

}
