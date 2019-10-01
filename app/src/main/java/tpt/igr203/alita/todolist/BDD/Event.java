package tpt.igr203.alita.todolist.BDD;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by RIQUIER_Melvin on 30/03/2019.
 */

@Entity(foreignKeys = @ForeignKey(entity = ToDoList.class, parentColumns = "id", childColumns = "listId"))
public class Event {

    @PrimaryKey(autoGenerate = true)
    private int id; //Clef primaire

    private String name;

    private int listId; //Clef étrangère

    private String notes;

    private String begining; //Format "AAAA/MM/JJ/HH/MM"

    private String ending; //Format "AAAA/MM/JJ/HH/MM"

    private int importance;

    private boolean showInCalendar;

    public Event(int listId) {
        this.name = "";
        this.listId = listId;
        this.notes = "";
        this.begining = "";
        this.ending = "";
        this.importance = 0;
        this.showInCalendar = false;
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

    public String getBegining() {
        return begining;
    }

    public String getEnding() {
        return ending;
    }

    public int getImportance() {
        return importance;
    }

    public boolean isShowInCalendar() {
        return showInCalendar;
    }

    //SETTERS


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setBegining(String begining) {
        this.begining = begining;
    }

    public void setEnding(String ending) {
        this.ending = ending;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public void setShowInCalendar(boolean showInCalendar) {
        this.showInCalendar = showInCalendar;
    }


    public static int[] stringToDate(String date){
        /*Prend en argument une date sous la forme "AAAA/MM/JJ/HH/MM" et renvoie
        * un tableau d'entier qui contient l'année le mois le jour l'heure et les minutes*
        */
        int[] result = new int[5];
        result[0] = Integer.parseInt(getYear(date));
        result[1] = Integer.parseInt(getMonth(date));
        result[2] = Integer.parseInt(getDay(date));
        result[3] = Integer.parseInt(getHour(date));
        result[4] = Integer.parseInt(getMinute(date));
        return result;
    }

    public static String dateToString(int[] date){
        String result = "";
        for (int i = 0; i<5; i++){
            String ajout = String.valueOf(date[i]);
            if(ajout.length() == 1){
                result += "0" + ajout;
            } else {
                result += ajout;
            }
            result += "/";
        }
        result = result.substring(0, 16);
        return result;
    }

    public static String getYear(String deadline){
        return deadline.substring(0, 4);
    }

    public static String getMonth(String deadline){
        return deadline.substring(5, 7);
    }

    public static String getDay(String deadline){
        return deadline.substring(8, 10);
    }

    public static String getHour(String deadline){
        return deadline.substring(11, 13);
    }

    public static String getMinute(String deadline){
        return deadline.substring(14, 16);
    }

}