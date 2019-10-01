package tpt.igr203.alita.todolist;

import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import tpt.igr203.alita.todolist.BDD.*;

public class globalViewActivity extends AppCompatActivity {
    // Identifier used for creating the views
    private int lastId = 1;

    // One list for each type of elements
    private List<Task> m_task = new ArrayList<>();
    private List<Event> m_event = new ArrayList<>();
    private List<Memo> m_memo = new ArrayList<>();

    // Lists storing colors for each element
    private List<String> m_color_task= new ArrayList<>();
    private List<String> m_color_event= new ArrayList<>();
    private List<String> m_color_memo=new ArrayList<>();

    // Lists containing layouts id for each element : used for sorting and displays
    private List<Integer> taskLayout = new ArrayList<>();
    private List<Integer> eventLayout = new ArrayList<>();
    private List<Integer> memoLayout = new ArrayList<>();
    private List<Integer> done = new ArrayList<>();

    // List of all ToDoLists
    private List<ToDoList> m_lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_view);

        // Sets the action bar for this activity
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Sets the sort to default
        homepageActivity.sort = new boolean[]{false, false, false, false};

        // Swipes to get back to homepageActivity
        getWindow().getDecorView().getRootView().setOnTouchListener(new SwipeListener(this) {
            public void onSwipeLeft() {
                Intent intent = new Intent(globalViewActivity.this, homepageActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            public void onSwipeRight() {
                Intent intent = new Intent(globalViewActivity.this, homepageActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        loadAllItems();
    }

    /**
     * When the back button of the phone is pressed
     */
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this,homepageActivity.class);
        startActivity(intent);
    }

    /**
     * Opens the database to load all items in all lists - saves them in this class' private variables
     */
    private void loadAllItems() {
        //Ouverture de la BDD
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        //On récupère l'ensemble des éléments, seulement une partie sera affichée, selon la barre d'outil
        //Récupération de l'ensemble des taches
        m_task = db.taskDAO().getAllTasks();
        //Récupération de l'ensemble des event
        m_event = db.eventDAO().getAllEvent();
        //Récupération de l'ensemble des mémos
        m_memo = db.memoDAO().getAllMemo();
        //Récupération de l'ensemble des listes
        m_lists = db.listDAO().getAllLists();

        // Initialization of the lists grid in the selection tool
        homepageActivity.list_Selection = new ArrayList<>();

        // Initialization of the color lists
        for(final Task task: m_task){
            String color=db.listDAO().getList(task.getListId()).getColor();
            m_color_task.add(color);
        }
        for (final Event event :m_event){
            String color=db.listDAO().getList(event.getListId()).getColor();
            m_color_event.add(color);
        }
        for (final Memo memo :m_memo){
            String color=db.listDAO().getList(memo.getListId()).getColor();
            m_color_memo.add(color);
        }
        for (ToDoList toDo : m_lists) {
            homepageActivity.list_Selection.add(true);
        }

        // display all selected items
        displayTasks(m_task, homepageActivity.selection[0]);
        displayEvents(m_event, homepageActivity.selection[1]);
        displayMemos(m_memo, homepageActivity.selection[2]);
        displayItems(done, homepageActivity.selection[3]);
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
     * Creates a layout per task and displays it if dispTask = true
     * @param m_task : list of task
     * @param dispTask : boolean for displaying the task
     */
    public void displayTasks(List<Task> m_task, boolean dispTask){
        RelativeLayout relativeLayout = findViewById(R.id.listView);
        int i=0;

        // for each element in the list of tasks
        for(final Task task : m_task){
            // creates a new layout for the task
            final RelativeLayout layout = new RelativeLayout(this);
            layout.setId(lastId);

            // Parameters
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            p.addRule(RelativeLayout.BELOW, lastId - 1);
            layout.setLayoutParams(p);

            // Checkboxes for when it is finished
            final CheckBox checkbox = new CheckBox(this);
            checkbox.setId(View.generateViewId());
            int checkId = checkbox.getId();
            RelativeLayout.LayoutParams c = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            checkbox.setLayoutParams(c);
            checkbox.setScaleX(1.5f);
            checkbox.setScaleY(1.5f);
            checkbox.setTextColor(Color.BLACK);
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkboxClicked(view, task, layout);
                }
            });
            layout.addView(checkbox);

            // Checks the checkbox if finished
            if (task.isFinished()){
                checkbox.setChecked(true);
            }

            // Name of the task
            final TextView name = new TextView(this);
            name.setId(View.generateViewId());
            name.setTextColor(Color.BLACK);
            int nameId = name.getId();
            RelativeLayout.LayoutParams n = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            n.addRule(RelativeLayout.RIGHT_OF, checkId);
            name.setLayoutParams(n);
            name.setText(task.getName());
            name.setTypeface(Typeface.create("Montserrat", Typeface.NORMAL));
            name.setTextSize(18);
            layout.addView(name);

            // Deadline elements
            String day = "01";
            String month = "01";
            String year = "2019";
            String hour = "00";
            String minutes = "00";

            final TextView deadline = new TextView(this);
            // Deadline
            if(task.hasDeadline()) {
                String date = task.getDeadline();

                day = Event.getDay(date);
                month = Event.getMonth(date);
                year = Event.getYear(date);
                hour = Event.getHour(date);
                minutes = Event.getMinute(date);

                RelativeLayout.LayoutParams d = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                d.addRule(RelativeLayout.BELOW, nameId);
                d.addRule(RelativeLayout.RIGHT_OF, checkId);
                deadline.setLayoutParams(d);
                deadline.setText(day + "/" + month + "/" + year + " " + "at" + " " + hour + ":" + minutes);
                deadline.setTypeface(Typeface.create("Montserrat", Typeface.NORMAL));
                deadline.setTextSize(16);
            }

            // Id and onClick listener
            final int taskId = task.getId();
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectTask(taskId);
                }
            });

            // Elements to be kept in the layout for sorting
            layout.setTag(R.id.importance, task.getImportance());
            layout.setTag(R.id.parentId, task.getListId());
            String string;
            if(task.hasDeadline()) {
                string = year + "/" + month + "/" + day + " " + hour + ":" + minutes;
            } else {
                string = "5000/00/00 00:00";
            }
            layout.setTag(R.id.deadline, string);

            //Different colors depending on the importance 
            layout.setBackgroundColor(getResources().getColor(getColorIdByName(m_color_task.get(i))));
            if (task.getImportance()==0) {layout.getBackground().setAlpha(20);}
            else {layout.getBackground().setAlpha((int) (task.getImportance()*25.5));}
            layout.setTag(R.id.layoutColor, m_color_task.get(i));

            // adds the layout
            layout.requestLayout();
            relativeLayout.addView(layout);

            boolean isPassed = false;

            if (task.hasDeadline()){
                String date = task.getDeadline();
                int[] dateTab = Event.stringToDate(date);

                Calendar cal = Calendar.getInstance();
                cal.set(dateTab[0], dateTab[1]-1, dateTab[2], dateTab[3], dateTab[4]);
                isPassed = cal.before(Calendar.getInstance());
            }
            if (isPassed && !task.isFinished()){
                deadline.setTextColor(Color.RED);
            } else {
                deadline.setTextColor(Color.BLACK);
            }

            if (task.isFinished()) {
                done.add(lastId);
                layout.setVisibility(RelativeLayout.GONE);
            } else {
                // deadline not yet reached, the layout is displayed if tasks are displayed
                taskLayout.add(lastId);

                if (dispTask) {
                    layout.setVisibility(RelativeLayout.VISIBLE);
                } else {
                    layout.setVisibility(RelativeLayout.GONE);
                }
            }

            layout.addView(deadline);

            lastId += 1;
            i++;
        }
    }

    /**
     * /**
     * Creates a layout per event and displays it if dispEvent = true
     * @param m_event : list of event
     * @param dispEvent : boolean for displaying the event
     */
    public void displayEvents(List<Event> m_event, boolean dispEvent) {
        RelativeLayout relativeLayout = findViewById(R.id.listView);
        int i=0;

        // for each element in the list of events
        for(Event event : m_event){
            // Beginning date
            String beginning = event.getBegining();

            String dayB = Event.getDay(beginning);
            String monthB = Event.getMonth(beginning);
            String yearB = Event.getYear(beginning);
            String hourB = Event.getHour(beginning);
            String minutesB = Event.getMinute(beginning);

            // Ending date
            String ending = event.getEnding();
            int[] endingTab = Event.stringToDate(ending);

            String day = Event.getDay(ending);
            String month = Event.getMonth(ending);
            String year = Event.getYear(ending);
            String hour = Event.getHour(ending);
            String minutes = Event.getMinute(ending);

            // creates a new layout for the event
            final RelativeLayout layout = new RelativeLayout(this);
            layout.setId(lastId);

            // Parameters
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            p.addRule(RelativeLayout.BELOW, lastId - 1);
            layout.setLayoutParams(p);

            // Name
            final TextView name = new TextView(this);
            name.setId(View.generateViewId());
            int nameId = name.getId();
            RelativeLayout.LayoutParams n = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            n.setMargins(100, 0, 0, 0);
            name.setLayoutParams(n);
            name.setText(event.getName());
            name.setTypeface(Typeface.create("Montserrat", Typeface.NORMAL));
            name.setTextSize(18);
            name.setTextColor(Color.BLACK);
            layout.addView(name);

            // Displays the dates
            final TextView deadline = new TextView(this);
            RelativeLayout.LayoutParams d = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            d.addRule(RelativeLayout.BELOW, nameId);
            d.setMargins(100, 0, 0, 0);
            deadline.setLayoutParams(d);

            String deadlineDisplay = "";
            if (beginning.equals(ending)){
                deadlineDisplay = dayB + "/" + monthB + "/" + yearB + " " + "at" + " " + hourB + ":" + minutesB;
            } else {
                deadlineDisplay = dayB + "/" + monthB + "/" + yearB + " " + "at" + " " + hourB + ":" + minutesB
                        + " " + "to" + " " + day + "/" + month + "/" + year + " " + "at" + " " + hour + ":" + minutes;
            }
            deadline.setText(deadlineDisplay);

            deadline.setTypeface(Typeface.create("Montserrat", Typeface.NORMAL));
            deadline.setTextSize(16);
            deadline.setTextColor(Color.BLACK);
            layout.addView(deadline);

            // Id and onClick listener
            final int eventId = event.getId();
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectEvent(eventId);
                }
            });

            // Elements to be kept in the layout for sorting
            layout.setTag(R.id.importance, event.getImportance());
            layout.setTag(R.id.parentId, event.getListId());
            String string = yearB + "/" + monthB + "/" + dayB + " " + hourB + ":" + minutesB;
            layout.setTag(R.id.deadline, string);

            //Different colors depending on the importance 
            layout.setBackgroundColor(getResources().getColor(getColorIdByName(m_color_event.get(i))));
            if (event.getImportance()==0) {layout.getBackground().setAlpha(20);}
            else {layout.getBackground().setAlpha((int) (event.getImportance()*25.5));}
            layout.setTag(R.id.layoutColor, m_color_event.get(i));

            // Adds the view to the parent layout
            layout.requestLayout();
            relativeLayout.addView(layout);

            //Check if deadline is passed
            String date = event.getEnding();
            int[] dateTab = Event.stringToDate(date);

            Calendar cal = Calendar.getInstance();
            cal.set(dateTab[0], dateTab[1]-1, dateTab[2], dateTab[3], dateTab[4]);
            boolean isPassed = cal.before(Calendar.getInstance());

            if (isPassed) {
                //If so, passed into the done list
                done.add(lastId);
                layout.setVisibility(RelativeLayout.GONE);
            } else {
                // not yet passed : displayed if events are displayed
                eventLayout.add(lastId);

                if (dispEvent) {
                    layout.setVisibility(RelativeLayout.VISIBLE);
                } else {
                    layout.setVisibility(RelativeLayout.GONE);
                }
            }

            lastId += 1;
            i++;
        }
    }

    /**
     * Creates a layout per memo and displays it if dispMemo = true
     * @param m_memo : list of memos
     * @param dispMemo : boolean for displaying the memos
     */
    public void displayMemos(List<Memo> m_memo, boolean dispMemo) {
        RelativeLayout relativeLayout = findViewById(R.id.listView);
        int i=0;

        // for each memo in the list of memos
        for(Memo memo : m_memo){
            // new layout for the memo
            final RelativeLayout layout = new RelativeLayout(this);
            layout.setId(lastId);

            // Parameters
            layout.setBackgroundColor(getResources().getColor(getColorIdByName(m_color_memo.get(i))));
            layout.getBackground().setAlpha(20);
            layout.setTag(R.id.layoutColor, m_color_memo.get(i));

            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            p.addRule(RelativeLayout.BELOW, lastId - 1);
            p.setMargins(100, 0, 0, 0);
            layout.setLayoutParams(p);

            // Name
            final TextView text = new TextView(this);
            text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            text.setText(memo.getName());
            text.setTextColor(Color.BLACK);
            text.setTypeface(Typeface.create("Montserrat", Typeface.NORMAL));
            text.setTextSize(18);

            layout.addView(text);

            // Id and OnClick listener
            final int memoId = memo.getId();
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectMemo((int) memoId);
                }
            });

            // Elements to be kept in the layout for sorting
            layout.setTag(R.id.importance, 0);
            layout.setTag(R.id.parentId, memo.getListId());
            String string = "5000/00/00 00:00";
            layout.setTag(R.id.deadline, string);

            // adds the layout to the parent layout
            layout.requestLayout();
            relativeLayout.addView(layout);

            // displayed if memos are displayed
            if (dispMemo) {
                layout.setVisibility(RelativeLayout.VISIBLE);
            } else {
                layout.setVisibility(RelativeLayout.GONE);
            }

            memoLayout.add(lastId);
            lastId += 1;
            i++;
        }
    }

    /**
     * OnClick : selects and displays a task
     * @param id : id of the task
     */
    public void selectTask(int id) {
        Intent intent = new Intent(globalViewActivity.this, viewTask.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }

    /**
     * OnClick : selects and displays an event
     * @param id : id of the event
     */
    public void selectEvent(int id) {
        Intent intent = new Intent(globalViewActivity.this, viewEvent.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }

    /**
     * OnClick : selects and displays a memo
     * @param id : id of the memo
     */
    public void selectMemo(int id) {
        Intent intent = new Intent(globalViewActivity.this, viewMemo.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }

    /**
     *  Actions to do when a task is finished or unfinished
     * @param view : checkbox checked
     * @param task : task finished
     * @param layout : layout of the finished task
     */
    public void checkboxClicked(View view, Task task, RelativeLayout layout){
        boolean checked = ((CheckBox) view).isChecked(); // finished or not
        task.setFinished(!task.isFinished());

        if (checked){ // if the task is finished, changes the display and the list in which the layout is
            done.add(layout.getId());
            if (homepageActivity.selection[3]) {
                layout.setVisibility(RelativeLayout.VISIBLE);
            } else {
                layout.setVisibility(RelativeLayout.GONE);
            }

            int index = taskLayout.indexOf(layout.getId());
            if (index != -1) {
                taskLayout.remove(index);
            }
        } else { // if the task is unfinished, changes the display and the list in which the layout is
            taskLayout.add(layout.getId());
            if (homepageActivity.selection[0]) {
                layout.setVisibility(RelativeLayout.VISIBLE);
            } else {
                layout.setVisibility(RelativeLayout.GONE);
            }

            int index = done.indexOf(layout.getId());
            if (index != -1){
                done.remove(index);
            }
        }

        // Updates the task in the database
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        db.taskDAO().updateTask(task);

    }

    /**
     * Functionalities of the action bar
     * @param menu : menu displayed in the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.basicmenu, menu);
        final int alpha = 60;

        // Selection button clicked
        final MenuItem itemSelection = menu.findItem(R.id.action_selection);
        itemSelection.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // selection dialog
                final Dialog dialog = new Dialog(globalViewActivity.this);
                dialog.setContentView(R.layout.global_view_selection_dialog);

                // set the selection dialog components - checkboxes & lists
                CheckBox task_checkbox = (CheckBox) dialog.findViewById(R.id.task_checkbox);
                task_checkbox.setChecked(homepageActivity.selection[0]);
                CheckBox event_checkbox = (CheckBox) dialog.findViewById(R.id.event_checkbox);
                event_checkbox.setChecked(homepageActivity.selection[1]);
                CheckBox memo_checkbox = (CheckBox) dialog.findViewById(R.id.memo_checkbox);
                memo_checkbox.setChecked(homepageActivity.selection[2]);
                CheckBox checked_item_checkbox = (CheckBox) dialog.findViewById(R.id.checked_checkbox);
                checked_item_checkbox.setChecked(homepageActivity.selection[3]);

                // OnClick listener for the list buttons
                View.OnClickListener listButtonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = view.getId();

                        for (int i = 0; i<m_lists.size(); i++){
                            int index = m_lists.get(i).getId();

                            if (id == index){ //finds the list clicked
                                if (homepageActivity.list_Selection.get(i)){ // if list displayed
                                    homepageActivity.list_Selection.set(i, false);
                                    view.getBackground().setAlpha(alpha);
                                    displayList(id, false);
                                } else { // if list not yet diplayed : displays it
                                    homepageActivity.list_Selection.set(i, true);
                                    view.getBackground().setAlpha(255);
                                    displayList(id, true);
                                }
                            }
                        }
                    }
                };

                // Grid in which all ToDolists are displayed
                GridLayout gridLayout = (GridLayout) dialog.findViewById(R.id.list_list);
                for (ToDoList toDo : m_lists) {
                    // new button for the ToDoList
                    final Button button = new Button(globalViewActivity.this);
                    final int id = toDo.getId();
                    button.setId(id);

                    // Parameters
                    button.setWidth(250);
                    button.setHeight(150);
                    button.setText(toDo.getName());
                    button.setBackground(getResources().getDrawable(R.drawable.button));
                    button.setTypeface(Typeface.create("Montserrat", Typeface.NORMAL));

                    // Color of the button
                    String col = toDo.getColor();
                    int color = getResources().getColor(getColorIdByName(col));
                    button.setBackgroundColor(color);
                    button.setTag(R.id.layoutColor, col);

                    button.setOnClickListener(listButtonListener);

                    button.requestLayout();
                    gridLayout.addView(button);
                }

                // To change the attributes
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.gravity = Gravity.TOP | Gravity.RIGHT; // position
                lp.y = 100;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        });

        // Calendar button clicked
        final MenuItem itemCalendar = menu.findItem(R.id.action_calendar);
        itemCalendar.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(globalViewActivity.this, calendarActivity.class);
                startActivity(intent);
            }
        });

        // Sort button clicked
        final MenuItem itemSort = menu.findItem(R.id.action_sort);
        itemSort.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // selection dialog
                final Dialog dialog = new Dialog(globalViewActivity.this);
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
                                    // sorts by color only
                                    view.getBackground().setAlpha(255);
                                    homepageActivity.sort[0] = true;
                                    setLayoutOrder("color");
                                    homepageActivity.sort[1] = false;
                                    homepageActivity.sort[2] = false;
                                    homepageActivity.sort[3] = false;
                                    byImportance.getBackground().setAlpha(alpha);
                                    byLast.getBackground().setAlpha(alpha);
                                    byDeadline.getBackground().setAlpha(alpha);
                                }
                                break;
                            case R.id.importance_sort:
                                if (homepageActivity.sort[1]) {
                                    view.getBackground().setAlpha(alpha);
                                    homepageActivity.sort[1] = false;
                                    setLayoutOrder("default");
                                } else {
                                    // sorts by importance only
                                    view.getBackground().setAlpha(255);
                                    homepageActivity.sort[1] = true;
                                    setLayoutOrder("importance");
                                    homepageActivity.sort[0] = false;
                                    homepageActivity.sort[2] = false;
                                    homepageActivity.sort[3] = false;
                                    byColor.getBackground().setAlpha(alpha);
                                    byLast.getBackground().setAlpha(alpha);
                                    byDeadline.getBackground().setAlpha(alpha);
                                }
                                break;
                            case R.id.deadline_sort:
                                if (homepageActivity.sort[2]) {
                                    view.getBackground().setAlpha(alpha);
                                    homepageActivity.sort[2] = false;
                                    setLayoutOrder("default");
                                } else {
                                    // sorts by deadline only
                                    view.getBackground().setAlpha(255);
                                    homepageActivity.sort[2] = true;
                                    setLayoutOrder("deadline");
                                    homepageActivity.sort[0] = false;
                                    homepageActivity.sort[1] = false;
                                    homepageActivity.sort[3] = false;
                                    byImportance.getBackground().setAlpha(alpha);
                                    byLast.getBackground().setAlpha(alpha);
                                    byColor.getBackground().setAlpha(alpha);
                                }
                                break;
                            case R.id.last_sort:
                                if (homepageActivity.sort[3]) {
                                    view.getBackground().setAlpha(alpha);
                                    homepageActivity.sort[3] = false;
                                    setLayoutOrder("default");
                                } else {
                                    // not implemented : default sort
                                    view.getBackground().setAlpha(255);
                                    homepageActivity.sort[3] = true;
                                    setLayoutOrder("default");
                                    homepageActivity.sort[0] = false;
                                    homepageActivity.sort[1] = false;
                                    homepageActivity.sort[2] = false;
                                    byImportance.getBackground().setAlpha(alpha);
                                    byColor.getBackground().setAlpha(alpha);
                                    byDeadline.getBackground().setAlpha(alpha);
                                }
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
     * when a selection checkbox is clicked or unclicked
     * @param view : checkbox clicked
     */
    public void onSelectionCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked and displays/hide following items
        switch (view.getId()) {
            case R.id.task_checkbox:
                homepageActivity.selection[0] = checked;
                displayItems(taskLayout, checked);
                break;
            case R.id.event_checkbox:
                homepageActivity.selection[1] = checked;
                displayItems(eventLayout, checked);
                break;
            case R.id.memo_checkbox:
                homepageActivity.selection[2] = checked;
                displayItems(memoLayout, checked);
                break;
            case R.id.checked_checkbox:
                homepageActivity.selection[3] = checked;
                displayItems(done, checked);
                break;
        }
    }

    /**
     * Displays the items of the list if boolean = True
     * @param list : list to be displayed or hidden
     * @param display : display or hide
     */
    public void displayItems(List<Integer> list, boolean display) {
        if (list.size() == 0) return;
        RelativeLayout relativeLayout = findViewById(R.id.listView);

        // for each layout in the activity
        for (int i = 0; i < relativeLayout.getChildCount(); i++) {
            View child = relativeLayout.getChildAt(i);

            // if the layout is in the concerned list
            for (Integer item : list) {
                if (child.getId() == item) {
                    if (display) {
                        child.setVisibility(RelativeLayout.VISIBLE);
                    } else {
                        child.setVisibility(RelativeLayout.GONE);
                    }
                }
            }
        }
    }

    /**
     * Method called to reorder the items displayed by layoutOrder
     * @param layoutOrder : "default" or "color" depending on how items are to be sorted
     */
    public void setLayoutOrder(String layoutOrder) {
        // Layout in which all lists are displayed
        RelativeLayout relativeLayout = findViewById(R.id.listView);
        List<ArrayList> orderList = new ArrayList<>();

        // get all items in the parent layout
        for(int i = 0; i < relativeLayout.getChildCount(); i++) {
            View child = relativeLayout.getChildAt(i);
            ArrayList element = new ArrayList<>();
            element.add(child.getId()); //child's id
            element.add(child.getTag(R.id.importance)); //child's importance
            element.add(child); // child
            element.add(child.getTag(R.id.deadline)); // child's deadline
            element.add(child.getTag(R.id.layoutColor)); // child's color

            orderList.add(element);
        }

        // this is the default list
        List<ArrayList> defaultList = new ArrayList<>(orderList);

        // Displays items
        if (layoutOrder == "importance") {
            // Sorting
            Collections.sort(orderList, new Comparator<ArrayList>() {
                @Override
                public int compare(ArrayList e2, ArrayList e1)
                {
                    Integer first = (int) e1.get(1);
                    Integer second = (int) e2.get(1);
                    return  second.compareTo(first);
                }
            });
            Collections.reverse(orderList);

            // Display according to the new order
            View first = (View) orderList.get(0).get(2);
            RelativeLayout.LayoutParams p1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            first.setLayoutParams(p1);

            for (int j = 1; j < orderList.size(); j++){
                View view = (View) orderList.get(j).get(2);
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p.addRule(RelativeLayout.BELOW, (int) orderList.get(j-1).get(0));
                view.setLayoutParams(p);
            }
        } else if (layoutOrder == "default"){
            // Display according to the default order
            View first = (View) defaultList.get(0).get(2);
            RelativeLayout.LayoutParams p1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            first.setLayoutParams(p1);

            for (int j = 1; j < defaultList.size(); j++){
                View view = (View) defaultList.get(j).get(2);
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p.addRule(RelativeLayout.BELOW, (int) defaultList.get(j-1).get(0));
                view.setLayoutParams(p);
            }
        } else if (layoutOrder == "deadline"){
            // Sorting
            Collections.sort(orderList, new Comparator<ArrayList>() {
                @Override
                public int compare(ArrayList e2, ArrayList e1)
                {
                    String first = (String) e1.get(3);
                    String second = (String) e2.get(3);

                    String pattern = "yyyy/MM/dd HH:mm";
                    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                    Date one = new Date();
                    Date two = new Date();
                    try {
                        one = dateFormat.parse(first);
                        two = dateFormat.parse(second);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return  two.compareTo(one);
                }
            });

            // Display according to the new order
            View first = (View) orderList.get(0).get(2);
            RelativeLayout.LayoutParams p1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            first.setLayoutParams(p1);

            for (int j = 1; j < orderList.size(); j++){
                View view = (View) orderList.get(j).get(2);
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p.addRule(RelativeLayout.BELOW, (int) orderList.get(j-1).get(0));
                view.setLayoutParams(p);
            }
        } else if (layoutOrder == "color") {
            // Sorting
            Collections.sort(orderList, new Comparator<ArrayList>() {
                @Override
                public int compare(ArrayList e2, ArrayList e1)
                {
                    String first = (String) e1.get(4);
                    String second = (String) e2.get(4);
                    return  second.compareTo(first);
                }
            });
            Collections.reverse(orderList);

            // Display according to the new order
            View first = (View) orderList.get(0).get(2);
            RelativeLayout.LayoutParams p1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            first.setLayoutParams(p1);

            for (int j = 1; j < orderList.size(); j++){
                View view = (View) orderList.get(j).get(2);
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p.addRule(RelativeLayout.BELOW, (int) orderList.get(j-1).get(0));
                view.setLayoutParams(p);
            }
        }
    }

    /**
     * Displays the list or hides it according to bool
     * @param id : list id
     * @param bool : displays or hides
     */
    public void displayList(int id, boolean bool){
        RelativeLayout relativeLayout = findViewById(R.id.listView);

        // for each children in the parent layout
        for (int i = 0; i < relativeLayout.getChildCount(); i++) {
            View child = relativeLayout.getChildAt(i);
            int parentId = (int) child.getTag(R.id.parentId);

            // checks if the layout is from the concerned list
            if (parentId == id){
                if (bool) {
                    child.setVisibility(RelativeLayout.VISIBLE);
                } else {
                    child.setVisibility(RelativeLayout.GONE);
                }
            }
        }
    }
}
