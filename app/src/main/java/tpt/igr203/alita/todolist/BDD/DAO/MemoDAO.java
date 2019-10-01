package tpt.igr203.alita.todolist.BDD.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import tpt.igr203.alita.todolist.BDD.Memo;

/**
 * Created by RIQUIER_Melvin on 30/03/2019.
 */

@Dao
public interface MemoDAO {

    @Query("SELECT * FROM Memo")
    List<Memo> getAllMemo();

    @Query("SELECT * FROM Memo WHERE listId = :listId")
    List<Memo> getMemoFromList(int listId);

    @Query("SELECT * FROM Memo WHERE id = :memoId")
    Memo getMemo(int memoId);

    @Insert
    long insertMemo(Memo memo);

    @Update
    int updateMemo(Memo memo);

    @Query("DELETE FROM Memo WHERE id = :memoId")
    int deleteMemo(int memoId);

    @Query("DELETE FROM Memo WHERE listId = :listId")
    int deleteMemoFromList(int listId);
}