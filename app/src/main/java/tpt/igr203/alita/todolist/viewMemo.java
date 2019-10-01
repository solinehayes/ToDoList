package tpt.igr203.alita.todolist;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Field;

import tpt.igr203.alita.todolist.BDD.DBHandler;
import tpt.igr203.alita.todolist.BDD.Memo;
import tpt.igr203.alita.todolist.BDD.ToDoList;

public class viewMemo extends AppCompatActivity {
    // Variables
    private int m_memoId;
    private Memo m_memo;
    private ToDoList m_list;
    private EditText memo;
    private EditText memoName;

    private boolean m_memoJustCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_memo);

        //on récupère l'id du mémo
        m_memoId = getIntent().getExtras().getInt("itemId", 1);
        m_memoJustCreated = getIntent().getExtras().getBoolean("justCreated", false);
        loadMemo();
    }

    /**
     * When the back button of the phone is pressed
     */
    @Override
    public void onBackPressed() {
        back(getWindow().getDecorView().getRootView());
    }

    /**
     * Loads the memo from the database
     */
    private void loadMemo() {
        //ouverture de la BDD
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        // loads the memo and its list
        m_memo = db.memoDAO().getMemo(m_memoId);
        m_list = db.listDAO().getList(m_memo.getListId());
        memoLoaded();
    }

    /**
     * Returns the id of a resource
     *
     * @param resourceName : name of the resource
     * @param c            : class in which it is stored
     * @return identifier (int)
     */
    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }

    /**
     * Displays and initialize the interface of the memo
     */
    private void memoLoaded() {
        // XML Widgets
        memoName = findViewById(R.id.memoName);
        memo = findViewById(R.id.memo);
        memo.setText(m_memo.getMemo());
        memoName.setText(m_memo.getName());

        //Gestion de la couleur en fonction de la couleur de la liste
        String colorList = m_list.getColor();
        int id = getId("degrade" + colorList, R.drawable.class);
        View view = this.getWindow().getDecorView();
        view.setBackgroundResource(id);
    }

    /**
     * OnClick : deletes the memo
     *
     * @param sender : button clicked
     */
    public void deleteMemo(View sender) {
        // Opening of the database
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        //Deletes the memo
        db.memoDAO().deleteMemo(m_memoId);

        // Returns to listViewActivity
        Intent listViewActivity = new Intent(viewMemo.this, listViewActivity.class);
        listViewActivity.putExtra("identifier", m_memo.getListId());
        startActivity(listViewActivity);
    }

    /**
     * OnClick : goes back to listViewActivity
     *
     * @param sender: button clicked
     */
    public void back(View sender) {
        if (m_memoJustCreated) {
            deleteMemo(sender);
        } else {
            Intent listViewActivity = new Intent(viewMemo.this, listViewActivity.class);
            listViewActivity.putExtra("identifier", m_memo.getListId());
            startActivity(listViewActivity);
        }
    }

    /**
     * OnClick : Saves the memo in the database
     *
     * @param sender: button clicked
     */
    public void save(View sender) {

        // Opening of the database
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        // Saves informations and updates the memo
        m_memo.setName(memoName.getText().toString());
        m_memo.setMemo(memo.getText().toString());
        db.memoDAO().updateMemo(m_memo);

        // Returns back to listViewActivity
        Intent listViewActivity = new Intent(viewMemo.this, listViewActivity.class);
        listViewActivity.putExtra("identifier", m_memo.getListId());
        startActivity(listViewActivity);
    }
}
