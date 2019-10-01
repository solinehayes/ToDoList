package tpt.igr203.alita.todolist;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Calendar;

import tpt.igr203.alita.todolist.BDD.DBHandler;
import tpt.igr203.alita.todolist.BDD.Event;
import tpt.igr203.alita.todolist.BDD.ToDoList;

public class viewEvent extends AppCompatActivity {
    // Variables
    private int m_eventId;
    private Event m_event;
    private ToDoList m_list;

    // Design variables
    private EditText note;
    private Button addNoteButton;
    private Spinner dateRappel;
    private Button addRapButton;
    private SeekBar imp;
    private Button addImpButton;
    private EditText name;
    private EditText dateBegin;
    private EditText hourBegin;
    private EditText dateEnd;
    private EditText hourEnd;
    private CheckBox showCalendar;
    private Button remRapButton;
    private Button remImpButton;
    private Button remNoteButton;

    private boolean m_eventJustCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        //on récupère l'id de l'event
        m_eventId = getIntent().getExtras().getInt("itemId", 1);
        m_eventJustCreated = getIntent().getExtras().getBoolean("justCreated", false);
        loadEvent();
    }

    /**
     * Loads the event
     */
    private void loadEvent() {
        //ouverture de la BDD
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        // Loads the event and its list
        m_event = db.eventDAO().getEvent(m_eventId);
        m_list = db.listDAO().getList(m_event.getListId());

        //On met les 2 deadline à la date d'aujourd'hui :
        //Initialisation à la date d'aujourd'hui :
        Calendar c = Calendar.getInstance();
        int[] date = {c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)};

        if (m_event.getBegining().equals("")) {
            m_event.setBegining(Event.dateToString(date));
            m_event.setEnding(Event.dateToString(date));
        }

        db.eventDAO().updateEvent(m_event);

        // displays it
        eventLoaded();
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
     * When the back button of the phone is pressed
     */
    @Override
    public void onBackPressed() {
        back(getWindow().getDecorView().getRootView());
    }

    /**
     * Displays and initialize the interface of the event
     */
    private void eventLoaded() {
        //Récuperation des widget XML
        note = findViewById(R.id.noteEvent);
        addNoteButton = findViewById(R.id.addNoteEvent);
        dateRappel = findViewById(R.id.spinnerReminder);
        addRapButton = findViewById(R.id.addRapEvent);
        imp = findViewById(R.id.impEvent);
        addImpButton = findViewById(R.id.addImpEvent);
        name = findViewById(R.id.nameEvent);
        dateBegin = findViewById(R.id.dateBegin);
        hourBegin = findViewById(R.id.hourBegin);
        dateEnd = findViewById(R.id.dateEnd);
        hourEnd = findViewById(R.id.hourEnd);
        showCalendar = findViewById(R.id.showCalendar);
        remRapButton = findViewById(R.id.remRapEvent);
        remNoteButton = findViewById(R.id.remNoteEvent);
        remImpButton = findViewById(R.id.remImpEvent);

        // Hide the following elements
        remRapButton.setVisibility(View.GONE);
        remNoteButton.setVisibility(View.GONE);
        remImpButton.setVisibility(View.GONE);

        //Gestion de la couleur en fonction de la couleur de la liste
        String colorList = m_list.getColor();
        int id = getId("degrade" + colorList, R.drawable.class);
        View view = this.getWindow().getDecorView();
        view.setBackgroundResource(id);

        //
        if (!m_event.getNotes().equals(new String(""))) {
            addNoteButton.setVisibility(View.GONE);
            remNoteButton.setVisibility(View.VISIBLE);
            note.setText(m_event.getNotes());
            note.setVisibility(View.VISIBLE);
        }

        // Gestion des dates
        int[] datesBegin = Event.stringToDate(m_event.getBegining());
        int[] datesEnd = Event.stringToDate(m_event.getEnding());

        // Beginning date
        String month = "/" + datesBegin[1];
        String day = "" + datesBegin[2];
        String hour = "" + datesBegin[3];
        String minutes = ":" + datesBegin[4];


        if (datesBegin[2] <= 9) {
            day = "0" + datesBegin[2];
        }
        if (datesBegin[1] <= 9) {
            month = "/0" + datesBegin[1];
        }
        if (datesBegin[3] <= 9) {
            hour = "0" + datesBegin[3];
        }
        if (datesBegin[4] <= 9) {
            minutes = ":0" + datesBegin[4];
        }

        // Displays the beginning time
        String text = day + month + "/" + datesBegin[0];
        dateBegin.setText(text);
        text = hour + minutes;
        hourBegin.setText(text);

        // Ending date
        String monthE = "/" + datesEnd[1];
        String dayE = "" + datesEnd[2];
        String hourE = "" + datesEnd[3];
        String minutesE = ":" + datesEnd[4];

        if (datesEnd[2] <= 9) {
            dayE = "0" + datesEnd[2];
        }
        if (datesEnd[1] <= 9) {
            monthE = "/0" + datesEnd[1];
        }
        if (datesEnd[3] <= 9) {
            hourE = "0" + datesEnd[3];
        }
        if (datesEnd[4] <= 9) {
            minutesE = ":0" + datesEnd[4];
        }

        // Displays the ending time
        text = dayE + monthE + "/" + datesEnd[0];
        dateEnd.setText(text);
        text = hourE + minutesE;
        hourEnd.setText(text);

        // Importance of the event
        if (m_event.getImportance() != 0) {
            addImpButton.setVisibility(View.GONE);
            remImpButton.setVisibility(View.VISIBLE);
            imp.setProgress(m_event.getImportance());
            imp.setVisibility(View.VISIBLE);
        }

        // Name of the event
        name.setText(m_event.getName());

        // checkbox to show it in calendar or not
        showCalendar.setChecked(m_event.isShowInCalendar());
    }

    /**
     * Deletes an event
     *
     * @param view : view to be deleted
     */
    public void deleteEvent(View view) {
        // Opens the database
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        // Deletes the event
        db.eventDAO().deleteEvent(m_eventId);
        Intent listViewActivity = new Intent(viewEvent.this, listViewActivity.class);
        listViewActivity.putExtra("identifier", m_event.getListId());
        startActivity(listViewActivity);
    }

    /**
     * Cancel modifying or creating the event
     *
     * @param sender : button clicked
     */
    public void back(View sender) {
        if (m_eventJustCreated) {
            deleteEvent(sender);
        } else {
            Intent listViewActivity = new Intent(viewEvent.this, listViewActivity.class);
            listViewActivity.putExtra("identifier", m_event.getListId());
            startActivity(listViewActivity);
        }
    }

    /**
     * Saves the event in the database
     *
     * @param sender : button clicked
     */
    public void save(View sender) {
        // Check if the user didn't put nonsens into edit text
        if (!(dateBegin.getText().toString().matches("\\d{2}/\\d{2}/\\d{4}"))) {
            Toast.makeText(this, "Please enter a beginning date in the format DD/MM/YYYY",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (!(dateEnd.getText().toString().matches("\\d{2}/\\d{2}/\\d{4}"))) {
            Toast.makeText(this, "Please enter a ending date in the format DD/MM/YYYY",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (!(hourBegin.getText().toString().matches("\\d{2}:\\d{2}"))) {
            Toast.makeText(this, "Please enter a beginning hour in the format HH:MM",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (!(hourEnd.getText().toString().matches("\\d{2}:\\d{2}"))) {
            Toast.makeText(this, "Please enter a ending hour in the format HH:MM",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Opens the database
        DBHandler db = Room.databaseBuilder(getApplicationContext(),
                DBHandler.class, "mainDatabase.db").allowMainThreadQueries().build();

        // Event name
        m_event.setName(name.getText().toString());
        // Importance
        if (imp.isEnabled()) {
            m_event.setImportance(imp.getProgress());
        } else {
            m_event.setImportance(0);
        }
        // Notes
        m_event.setNotes(note.getText().toString());
        // Show in Calendar
        m_event.setShowInCalendar(showCalendar.isChecked());

        // Dates of the event
        String[] datesBegin = dateBegin.getText().toString().split("/");
        String[] datesEnd = dateEnd.getText().toString().split("/");
        String[] hoursBegin = hourBegin.getText().toString().split(":");
        String[] hoursEnd = hourEnd.getText().toString().split(":");

        m_event.setBegining(datesBegin[2] + "/" + datesBegin[1] + "/" + datesBegin[0] + "/" + hoursBegin[0] + "/" + hoursBegin[1]);
        m_event.setEnding(datesEnd[2] + "/" + datesEnd[1] + "/" + datesEnd[0] + "/" + hoursEnd[0] + "/" + hoursEnd[1]);

        // updates the event in the database
        db.eventDAO().updateEvent(m_event);

        // Returns to listViewActivity
        Intent listViewActivity = new Intent(viewEvent.this, listViewActivity.class);
        listViewActivity.putExtra("identifier", m_event.getListId());
        startActivity(listViewActivity);
    }

    /**
     * Adds a note to the event
     *
     * @param view : button clicked
     */
    public void addNote(View view) {
        note.setVisibility(View.VISIBLE);
        addNoteButton.setVisibility(View.GONE);
        remNoteButton.setVisibility(View.VISIBLE);
    }

    /**
     * Adds an alarm to the event
     *
     * @param view : button clicked
     */
    public void addRap(View view) {
        dateRappel.setVisibility(View.VISIBLE);
        addRapButton.setVisibility(view.GONE);
        remRapButton.setVisibility(View.VISIBLE);
    }

    /**
     * OnClick : adds an importance to the event
     *
     * @param view : button clicked
     */
    public void addImp(View view) {
        imp.setVisibility(View.VISIBLE);
        imp.setEnabled(true);
        addImpButton.setVisibility(View.GONE);
        remImpButton.setVisibility(View.VISIBLE);
    }

    /**
     * OnClick : Removes the note added to the event
     *
     * @param view: button clicked
     */
    public void remNote(View view) {
        note.setVisibility(View.GONE);
        addNoteButton.setVisibility(View.VISIBLE);
        remNoteButton.setVisibility(View.GONE);
        m_event.setNotes("");
        note.setText("");
    }

    /**
     * OnClick : removes the alarm of the event
     *
     * @param view: button clicked
     */
    public void remRap(View view) {
        dateRappel.setVisibility(View.GONE);
        addRapButton.setVisibility(view.VISIBLE);
        remRapButton.setVisibility(View.GONE);
    }

    /**
     * OnClick : removes the importance of the event
     *
     * @param view : button clicked
     */
    public void remImp(View view) {
        imp.setVisibility(View.GONE);
        addImpButton.setVisibility(View.VISIBLE);
        remImpButton.setVisibility(View.GONE);
        m_event.setImportance(0);
        imp.setEnabled(false);
    }
}
