package tpt.igr203.alita.todolist;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tpt.igr203.alita.todolist.BDD.*;

import android.arch.persistence.room.*;
import android.widget.Button;
import android.widget.GridLayout;
import android.view.WindowManager;
import android.widget.Toast;

public class homepageActivity extends AppCompatActivity {
    // List of all ToDoList to be displayed
    private List<ToDoList> m_lists;

    // Parameters of the action bar
    static boolean[] selection = new boolean[]{true, true, true, false};
    static ArrayList<Boolean> list_Selection = new ArrayList<Boolean>(){};
    static boolean[] sort = new boolean[]{false, false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        loadLists();

        // Swipes
        getWindow().getDecorView().getRootView().setOnTouchListener(new SwipeListener(this) {
            public void onSwipeLeft() {
                Intent intent = new Intent(homepageActivity.this, globalViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            public void onSwipeRight() {
                Intent intent = new Intent(homepageActivity.this, globalViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    /**
     * OnClick : loads createList to create a list
     * @param sender : button clicked
     */
    public void createList(View sender) {
        //Creation d'une nouvelle liste avec paramètres par défaut
        ToDoList newList = new ToDoList();

        //Ouverture de la BDD
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();
        //Insertion de la nouvelle liste dans la BDD
        int id = (int) db.listDAO().insertList(newList);

        Intent intent = new Intent(homepageActivity.this, createList.class);
        //On passe l'identifiant de la nouvelle liste à l'activité qui permet de modifier une liste
        intent.putExtra("identifier", id);
        intent.putExtra("justCreated", true); //indique qu'on arrive sur cette activité en ayant juste créé la liste
        startActivity(intent);
    }

    /**
     * Selects a list and displays it
     * @param id : list identifier
     */
    public void selectList(int id) {
        Intent intent = new Intent(homepageActivity.this, listViewActivity.class);
        intent.putExtra("identifier", id);
        startActivity(intent);
    }

    /**
     * Load the lists
     */
    private void loadLists() {
        // Opens the database
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        //Récupération de l'ensemble des listes
        m_lists = db.listDAO().getAllLists();

        // Displays the list in a gridLayout
        GridLayout gridLayout = findViewById(R.id.grid);
        for (ToDoList toDo : m_lists) {
            // Creates a button for the list
            final Button button = new Button(this);
            final int id = toDo.getId();
            button.setId(id);

            // Parameters of the button
            button.setWidth(400);
            button.setHeight(220);
            button.setText(toDo.getName());
            button.setBackground(getResources().getDrawable(R.drawable.button));
            button.setTypeface(Typeface.create("Montserrat", Typeface.NORMAL));

            String col = toDo.getColor();
            int color = getResources().getColor(getColorIdByName(col));
            button.setBackgroundColor(color);
            button.setTag(R.id.layoutColor, col);

            // OnClick listener
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectList(id);
                }
            });

            // adds the button to the grid
            button.requestLayout();
            gridLayout.addView(button);
        }
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
     * Functionalities of the action bar
     * @param menu : menu displayed in the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        final int alpha = 60;

        // Calendar button clicked
        final MenuItem itemCalendar = menu.findItem(R.id.action_calendar);
        itemCalendar.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homepageActivity.this, calendarActivity.class);
                startActivity(intent);
            }
        });

        // Sort button clicked
        final MenuItem itemSort = menu.findItem(R.id.action_sort);
        itemSort.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // selection dialog
                final Dialog dialog = new Dialog(homepageActivity.this);
                dialog.setContentView(R.layout.sort_dialog);

                // set the selection dialog components - checkboxes
                final Button byColor = (Button) dialog.findViewById(R.id.color_sort);
                if (!homepageActivity.sort[0]) { byColor.getBackground().setAlpha(alpha);}
                final Button byImportance = (Button) dialog.findViewById(R.id.importance_sort);
                if (!homepageActivity.sort[1]) { byImportance.getBackground().setAlpha(alpha);}
                final Button byDeadline = (Button) dialog.findViewById(R.id.deadline_sort);
                if (!homepageActivity.sort[2]) { byDeadline.getBackground().setAlpha(alpha);}
                final Button byLast = (Button) dialog.findViewById(R.id.last_sort);
                if (!homepageActivity.sort[3]) { byLast.getBackground().setAlpha(alpha);}

                // on click for the sort buttons
                View.OnClickListener listButtonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // id of the button clicked
                        int id = view.getId();

                        // Depending on the id, modify how the items are sorted
                        switch (id) {
                            case R.id.color_sort:
                                if (homepageActivity.sort[0]) {
                                    view.getBackground().setAlpha(alpha);
                                    homepageActivity.sort[0] = false;
                                    setLayoutOrder("default");
                                } else {
                                    // sorts by color
                                    view.getBackground().setAlpha(255);
                                    homepageActivity.sort[0] = true;
                                    setLayoutOrder("color");
                                }
                                break;
                            case R.id.importance_sort:
                                // nothing done
                                view.getBackground().setAlpha(alpha);
                                homepageActivity.sort[1] = false;
                                break;
                            case R.id.deadline_sort:
                                // nothing done
                                view.getBackground().setAlpha(alpha);
                                homepageActivity.sort[2] = false;
                                break;
                            case R.id.last_sort:
                                // nothing done
                                view.getBackground().setAlpha(alpha);
                                homepageActivity.sort[3] = false;
                                break;
                        }
                    }
                };

                byColor.setOnClickListener(listButtonListener);
                byImportance.setOnClickListener(listButtonListener);
                byDeadline.setOnClickListener(listButtonListener);
                byLast.setOnClickListener(listButtonListener);

                // To change the attributes
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.gravity = Gravity.TOP | Gravity.RIGHT; // position
                lp.y = 100;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        });

        return true;
    }

    // if settings was implemented
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
            /* DO EDIT */
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method called to reorder the items displayed by layoutOrder
     * @param layoutOrder : "default" or "color" depending on how items are to be sorted
     */
    public void setLayoutOrder(String layoutOrder) {
        // Grid in which all lists are displayed
        GridLayout gridLayout = findViewById(R.id.grid);
        List<ArrayList> orderList = new ArrayList<>();

        // keeps the add button as the first element
        View addButton = gridLayout.getChildAt(0);

        // get all items in the grid
        for(int i = 1; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            ArrayList element = new ArrayList<>();
            element.add(child.getId()); // child's id
            element.add(child); // child
            element.add(child.getTag(R.id.layoutColor)); // child color

            orderList.add(element);
        }

        // this is the default list
        List<ArrayList> defaultList = new ArrayList<>(orderList);

        // Displays items
        if (layoutOrder == "color") {
            // Sorting
            Collections.sort(orderList, new Comparator<ArrayList>() {
                @Override
                public int compare(ArrayList e2, ArrayList e1)
                {
                    String first = (String) e1.get(2);
                    String second = (String) e2.get(2);
                    return  second.compareTo(first);
                }
            });

            // Display according to the new order
            gridLayout.removeAllViews();
            gridLayout.addView(addButton);

            for (int j = 0; j < orderList.size(); j++){
                View view = (View) orderList.get(j).get(1);
                gridLayout.addView(view);
            }
        } else if (layoutOrder == "default"){
            // Display according to the default order
            gridLayout.removeAllViews();
            gridLayout.addView(addButton);

            for (int j = 0; j < defaultList.size(); j++){
                View view = (View) defaultList.get(j).get(1);
                gridLayout.addView(view);
            }
        }
    }
}


