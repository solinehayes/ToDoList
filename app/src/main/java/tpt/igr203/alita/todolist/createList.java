package tpt.igr203.alita.todolist;


import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import tpt.igr203.alita.todolist.BDD.DBHandler;
import tpt.igr203.alita.todolist.BDD.ToDoList;

/**
 * Activity qui permet de modifier une liste. On commence par charger les infos actuelles
 * et ensuite on peut les modifier directement. Une fois modifiée on l'enregistre dans le BDD
 */
public class createList extends AppCompatActivity {
    // List id
    private int m_listId;
    // list of ToDoList
    private ToDoList m_todolist;
    // list's color id
    private int currentColorId;

    private float rows = 0;
    private int columns = 0;

    private boolean m_listjustCreated; //indique que l'ont arrive sur cette activity en ayant créé une liste (permet de la supprimer quand on cancel)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        // On récupère l'identifiant de la liste à modifier
        Intent intent = getIntent();
        m_listId = intent.getExtras().getInt("identifier", 1);
        m_listjustCreated = intent.getExtras().getBoolean("justCreated", false);

        loadList();
    }

    /**
     * When the cancel button is pressed
     * @param sender : button
     */
    public void back(View sender) {
        /* Si on vient juste de créer la liste le cancel la supprime */
        if (m_listjustCreated){
            deleteList(sender);
        } else {
            Intent listViewActivity = new Intent(createList.this, homepageActivity.class);
            startActivity(listViewActivity);
        }
    }

    /**
     * Loads the content of the view with id m_listId
     */
    private void loadList() {
        // Ouverture de la BDD
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        // On récupère la liste
        m_todolist = db.listDAO().getList(m_listId);

        // Une fois que tout est chargé, on affiche
        EditText nameList = findViewById(R.id.nameList);
        nameList.setText(m_todolist.getName());
        currentColorId = getColorIdByName(m_todolist.getColor());
        setColor(m_todolist.getColor());
    }

    /**
     * Règle la couleur du texte sur la couleur donnée en string
     * @param color : color name
     */
    private void setColor(String color){
        EditText nameList= findViewById(R.id.nameList);
        nameList.setTextColor(getResources().getColor(getColorIdByName(color)));
        nameList.setHintTextColor(getResources().getColor(getColorIdByName(color)));
    }

    /**
     * Returns the identifier of the color of a list with its name
     * @param colorName : name of the color
     * @return color identifier (int)
     */
    private int getColorIdByName(String colorName) {
        return getResources().getIdentifier(colorName, "color", getPackageName());
    }

    /**
     * Changes the color of the text from the view clicked
     * @param view : view with color clicked
     */
    public void changeColor(View view) {
        currentColorId = view.getId();
        EditText nameList= findViewById(R.id.nameList);
        switch(currentColorId) {
            case R.id.blue : //Mettre la couleur de la liste sur bleu
                nameList.setTextColor(getResources().getColor(R.color.blue));
                nameList.setHintTextColor(getResources().getColor(R.color.blue));
                break;
            case R.id.pink : //Mettre la couleur de la liste sur pink
                nameList.setTextColor(getResources().getColor(R.color.pink));
                nameList.setHintTextColor(getResources().getColor(R.color.pink));
                break;
            case R.id.dark_blue : //Mettre la couleur de la liste sur bleu foncé
                nameList.setTextColor(getResources().getColor(R.color.dark_blue));
                nameList.setHintTextColor(getResources().getColor(R.color.dark_blue));
                break;
            case R.id.green : //Mettre la couleur de la liste sur green
                nameList.setTextColor(getResources().getColor(R.color.green));
                nameList.setHintTextColor(getResources().getColor(R.color.green));
                break;
            case R.id.red : //Mettre la couleur de la liste sur rouge
                nameList.setTextColor(getResources().getColor(R.color.red));
                nameList.setHintTextColor(getResources().getColor(R.color.red));
                break;
            case R.id.purple : //Mettre la couleur de la liste sur violet
                nameList.setTextColor(getResources().getColor(R.color.purple));
                nameList.setHintTextColor(getResources().getColor(R.color.purple));
                break;
            case R.id.orange : //Mettre la couleur de la liste sur orange
                nameList.setTextColor(getResources().getColor(R.color.orange));
                nameList.setHintTextColor(getResources().getColor(R.color.orange));
                break;
            case R.id.yellow : //Mettre la couleur de la liste sur yellow
                nameList.setTextColor(getResources().getColor(R.color.yellow));
                nameList.setHintTextColor(getResources().getColor(R.color.yellow));
                break;
        }
    }

    /**
     * Save the list created
     * @param sender : save button clicked
     */
    public void save(View sender) {
        //ouverture de la BDD
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        //Maj du nom
        EditText nameList = findViewById(R.id.nameList);
        m_todolist.setName(nameList.getText().toString());

        //Maj de la couleur
        m_todolist.setColor(getResources().getResourceEntryName(currentColorId));

        //Sauvegarde
        db.listDAO().updateList(m_todolist);

        Intent listViewActivity = new Intent(createList.this, listViewActivity.class);
        listViewActivity.putExtra("identifier", m_listId);
        startActivity(listViewActivity);
    }

    /**
     * Deletes a list
     * @param view : delete button clicked
     */
    public void deleteList(View view){
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        //avant de supprimer la liste on supprime tous les items dedans
        db.taskDAO().deleteTaskFromList(m_listId);
        db.eventDAO().deleteEventFromList(m_listId);
        db.memoDAO().deleteMemoFromList(m_listId);

        db.listDAO().deleteList(m_listId);

        // goes back to homepage
        Intent intent = new Intent(createList.this, homepageActivity.class);
        startActivity(intent);
    }
}
