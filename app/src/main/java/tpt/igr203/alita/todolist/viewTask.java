package tpt.igr203.alita.todolist;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Calendar;

import tpt.igr203.alita.todolist.BDD.DBHandler;
import tpt.igr203.alita.todolist.BDD.Event;
import tpt.igr203.alita.todolist.BDD.Task;
import tpt.igr203.alita.todolist.BDD.ToDoList;

public class viewTask extends AppCompatActivity {
    // Variables
    private int m_taskId;
    private Task m_task;
    private ToDoList m_list;

    private EditText note;
    private Button addNoteButton;
    private Spinner dateRappel;
    private Button addRapButton;
    private SeekBar imp;
    private Button addImpButton;
    private EditText name;
    private DatePicker deadline;
    private Button addDateButton;
    private CheckBox showCalendar;
    private CheckBox finished;
    private EditText timeDeadline;
    private Button remNoteButton;
    private Button remRapButton;
    private Button remDateButton;
    private Button remImpButton;
    private LinearLayout layoutTime;

    private boolean m_taskJustCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        //on récupère l'id de la liste
        m_taskId = getIntent().getExtras().getInt("itemId", 1);
        m_taskJustCreated = getIntent().getExtras().getBoolean("justCreated", false);
        loadTask(m_taskId);
    }

    /**
     * Loads the task
     * @param m_taskId : task id
     */
    private void loadTask(final int m_taskId) {
        //ouverture de la BDD
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        // loads the task and its list
        m_task = db.taskDAO().getTask(m_taskId);
        m_list = db.listDAO().getList(m_task.getListId());

        // Displays it
        taskLoaded();
    }

    /**
     * Returns the id of a resource
     * @param resourceName : name of the resource
     * @param c : class in which it is stored
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
     * OnClick : sets a task as finished
     * @param view : checkbox clicked
     */
    public void checkboxClicked(View view){
        m_task.setFinished(!m_task.isFinished());
    }

    /**
     * Displays and initialize the interface of the task
     */
    private void taskLoaded(){
        // XML Widgets
        note= findViewById(R.id.noteTask);
        addNoteButton = findViewById(R.id.addNoteTask);
        dateRappel = findViewById(R.id.spinnerReminder);
        addRapButton = findViewById(R.id.addRapTask);
        imp = findViewById(R.id.impTask);
        addImpButton = findViewById(R.id.addImpTask);
        name = findViewById(R.id.nameTask);
        deadline = findViewById(R.id.dateTask);
        addDateButton = findViewById(R.id.addDateTask);
        showCalendar = findViewById(R.id.showCalendarTask);
        finished = findViewById(R.id.finished);
        timeDeadline = findViewById(R.id.timeTask);
        remDateButton = findViewById(R.id.remDateTask);
        remImpButton = findViewById(R.id.remImpTask);
        remRapButton = findViewById(R.id.remRapTask);
        remNoteButton = findViewById(R.id.remNoteTask);
        layoutTime = findViewById(R.id.layoutHour);

        //Gestion de la couleur en fonction de la couleur de la liste
        String colorList = m_list.getColor();
        int id = getId("degrade" + colorList, R.drawable.class);
        View view = this.getWindow().getDecorView();
        view.setBackgroundResource(id);

        // Notes written in the task
        if (!m_task.getNotes().equals("")) {
            addNoteButton.setVisibility(View.GONE);
            note.setText(m_task.getNotes());
            note.setVisibility(View.VISIBLE);
            remNoteButton.setVisibility(View.VISIBLE);
        }

        // DEADLINE if the task has one
        if (m_task.hasDeadline()) {
            String date = m_task.getDeadline();
            int[] dateDeadline = Event.stringToDate(date);
            deadline.updateDate(dateDeadline[0], dateDeadline[1] - 1, dateDeadline[2]);
            timeDeadline.setText(Event.getHour(date) + ":" + Event.getMinute(date));
            addDateButton.setVisibility(View.GONE);
            deadline.setVisibility(View.VISIBLE);
            remDateButton.setVisibility(View.VISIBLE);
            layoutTime.setVisibility(View.VISIBLE);
        }

        // Importance of the task
        if(m_task.getImportance() != 0){
            addImpButton.setVisibility(View.GONE);
            imp.setProgress(m_task.getImportance());
            imp.setVisibility(View.VISIBLE);
            remImpButton.setVisibility(View.VISIBLE);
        }

        // Name
        name.setText(m_task.getName());

        // Checkboxes to show it in calendar or that the task is finished
        showCalendar.setChecked(m_task.isShowInCalendar());
        finished.setChecked(m_task.isFinished());
    }

    /**
     * Deletes a task
     * @param view : view to be deleted
     */
    public void deleteTask(View view){
        // Opens the database
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        // deletes the task
        db.taskDAO().deleteTask(m_taskId);

        // returns to listViewActivity
        Intent listViewActivity = new Intent(viewTask.this, listViewActivity.class);
        listViewActivity.putExtra("identifier", m_task.getListId());
        startActivity(listViewActivity);
    }

    /**
     * When the back button of the phone is pressed
     */
    @Override
    public void onBackPressed()
    {
        back(getWindow().getDecorView().getRootView());
    }

    /**
     * Cancel modifying or creating the task
     * @param sender : button clicked
     */
    public void back(View sender) {
        if (m_taskJustCreated){
            deleteTask(sender);
        }else {
            Intent listViewActivity = new Intent(viewTask.this, listViewActivity.class);
            listViewActivity.putExtra("identifier", m_task.getListId());
            startActivity(listViewActivity);
        }
    }

    /**
     * Saves the task in the database
     * @param sender : button clicked
     */
    public void save(View sender) {
        // Check if the user didn't put nonsens into the hours edit text
        if (m_task.hasDeadline()){
            if (!(timeDeadline.getText().toString().matches("\\d{2}:\\d{2}"))){
                Toast.makeText(this, "Please enter an hour in the format HH:MM",
                        Toast.LENGTH_LONG).show();
                return ;
            }

            String[] hours = timeDeadline.getText().toString().split(":");
            int hour = Integer.valueOf(hours[0]);
            int minute = Integer.valueOf(hours[1]);

            if (hour<0 || hour > 23 || minute < 0 || minute > 60){
                Toast.makeText(this, "Please enter an hour between 00:00 and 23:59",
                        Toast.LENGTH_LONG).show();
                return ;
            }
        }

        // Opens the database
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        // Saves the state of the task
        m_task.setFinished(finished.isChecked());
        // Importance
        if (imp.isEnabled()) {
            m_task.setImportance(imp.getProgress());
        } else {
            m_task.setImportance(0);
        }

        // Name
        m_task.setName(name.getText().toString());
        // Notes
        m_task.setNotes(note.getText().toString());
        // Show in calendar
        m_task.setShowInCalendar(showCalendar.isChecked());

        // Deadline if it has one
        if (m_task.hasDeadline()) {
            String[] hours = timeDeadline.getText().toString().split(":");

            int[] date = {deadline.getYear(), deadline.getMonth()+1, deadline.getDayOfMonth(),
                    Integer.valueOf(hours[0]), Integer.valueOf(hours[1])};

            m_task.setDeadline(Event.dateToString(date));
        }

        // Updates the task in the database
        db.taskDAO().updateTask(m_task);

        // Returns to listViewActivity
        Intent listViewActivity = new Intent(viewTask.this, listViewActivity.class);
        listViewActivity.putExtra("identifier", m_task.getListId());
        startActivity(listViewActivity);
    }

    /**
     * Adds a note to the task
     * @param view : button clicked
     */
    public void addNote(View view) {
        note.setVisibility(View.VISIBLE);
        addNoteButton.setVisibility(View.GONE);
        remNoteButton.setVisibility(View.VISIBLE);
    }

    /**
     * Adds an alarm to the task
     * @param view : button clicked
     */
    public void addRap(View view) {
        dateRappel.setVisibility(View.VISIBLE);
        addRapButton.setVisibility(view.GONE);
        remRapButton.setVisibility(View.VISIBLE);
    }

    /**
     * OnClick : adds an importance to the task
     * @param view : button clicked
     */
    public void addImp(View view) {
        imp.setVisibility(View.VISIBLE);
        imp.setEnabled(true);
        addImpButton.setVisibility(View.GONE);
        remImpButton.setVisibility(View.VISIBLE);
    }

    /**
     * OnClick : adds a deadline to the task
     * @param view : button clicked
     */
    public void addDate(View view) {
        m_task.setHasDeadline(true);
        //Initialisation à la date d'aujourd'hui :
        Calendar c = Calendar.getInstance();
        int[] date = {c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)};
        m_task.setDeadline(Event.dateToString(date));

        deadline.updateDate(date[0], date[1], date[2]); //Mettre -1 au mois ?
        timeDeadline.setText(Event.getHour(Event.dateToString(date)) + ":" + Event.getMinute(Event.dateToString(date)));
        addDateButton.setVisibility(View.GONE);
        timeDeadline.setVisibility(View.VISIBLE);
        deadline.setVisibility(View.VISIBLE);
        remDateButton.setVisibility(View.VISIBLE);
        layoutTime.setVisibility(View.VISIBLE);
    }

    /**
     * OnClick : Removes the note added to the task
     * @param view: button clicked
     */
    public void remNote(View view) {
        note.setVisibility(View.GONE);
        addNoteButton.setVisibility(View.VISIBLE);
        remNoteButton.setVisibility(View.GONE);
        m_task.setNotes("");
        note.setText("");
    }

    /**
     * OnClick : removes the alarm of the task
     * @param view: button clicked
     */
    public void remRap(View view) {
        dateRappel.setVisibility(View.GONE);
        addRapButton.setVisibility(view.VISIBLE);
        remRapButton.setVisibility(View.GONE);
    }

    /**
     * OnClick : removes the importance of the task
     * @param view : button clicked
     */
    public void remImp(View view) {
        imp.setVisibility(View.GONE);
        addImpButton.setVisibility(View.VISIBLE);
        remImpButton.setVisibility(View.GONE);
        m_task.setImportance(0);
        imp.setEnabled(false);
    }

    /**
     * OnClick : removes the deadline of the task
     * @param view : button clicked
     */
    public void remDate(View view) {
        timeDeadline.setVisibility(View.VISIBLE);
        deadline.setVisibility(View.GONE);
        addDateButton.setVisibility(View.VISIBLE);
        remDateButton.setVisibility(View.GONE);
        layoutTime.setVisibility(View.GONE);
        m_task.setHasDeadline(false);
        m_task.setDeadline("");
    }
}
