package tpt.igr203.alita.todolist.BDD;

import android.arch.persistence.db.*;
import android.arch.persistence.room.*;
import android.content.*;
import android.support.annotation.*;

import tpt.igr203.alita.todolist.BDD.DAO.*;

/** Classe qui gère la base de donnée en elle même.
 *
 */

@Database(entities = {ToDoList.class, Task.class, Memo.class, Event.class}, version = 1, exportSchema = false)
public abstract class DBHandler extends RoomDatabase {

    // --- SINGLETON ---
    private static volatile DBHandler INSTANCE;

    // --- DAO ---
    public abstract ToDoListDAO listDAO();
    public abstract TaskDAO taskDAO();
    public abstract EventDAO eventDAO();
    public abstract MemoDAO memoDAO();

    // --- INSTANCE ---
    public static DBHandler getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DBHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DBHandler.class, "mainDatabase.db")
                            .addCallback(prepopulateDatabase())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static Callback prepopulateDatabase(){
        return new Callback() {

            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }
        };
    }
}