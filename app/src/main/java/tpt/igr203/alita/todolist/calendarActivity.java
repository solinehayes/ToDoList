package tpt.igr203.alita.todolist;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * Calendar activity : calendar not implemented, displays a picture
 * Created by Zoé Berenger on 30/03/2019.
 */
public class calendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(true);
    }

    /**
     * Functionalities of the action bar - overrides
     * @param menu : menu displayed in the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_menu, menu);
        final int alpha = 60;

        // When selection clicked : not implemented since no items are displayed
        final MenuItem itemSelection = menu.findItem(R.id.action_selection);
        itemSelection.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // selection dialog
                final Dialog dialog = new Dialog(calendarActivity.this);
                dialog.setContentView(R.layout.selection_dialog);

                // set the selection dialog components - checkboxes
                CheckBox task_checkbox = (CheckBox) dialog.findViewById(R.id.task_checkbox) ;
                task_checkbox.setChecked(homepageActivity.selection[0]);
                CheckBox event_checkbox = (CheckBox) dialog.findViewById(R.id.event_checkbox) ;
                event_checkbox.setChecked(homepageActivity.selection[1]);
                CheckBox memo_checkbox = (CheckBox) dialog.findViewById(R.id.memo_checkbox) ;
                memo_checkbox.setChecked(homepageActivity.selection[2]);
                CheckBox checked_item_checkbox = (CheckBox) dialog.findViewById(R.id.checked_checkbox) ;
                checked_item_checkbox.setChecked(homepageActivity.selection[3]);

                // To change the attributes
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.gravity = Gravity.TOP | Gravity.RIGHT; // position
                lp.y = 100 ;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        });

        // when the todolist button is clicked : returns to homepage
        final MenuItem itemToDo = menu.findItem(R.id.action_todolist);
        itemToDo.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(calendarActivity.this, homepageActivity.class);
                startActivity(intent);
            }
        });

        return true;
    }

    /**
     * when a checkbox is clicked or unclicked -- nothing done in this view since it is not implemented
     * @param view : checkbox clicked
     */
    public void onSelectionCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.task_checkbox:
                homepageActivity.selection[0] = checked;
            case R.id.event_checkbox:
                homepageActivity.selection[1] = checked;
            case R.id.memo_checkbox:
                homepageActivity.selection[2] = checked;
            case R.id.checked_checkbox:
                homepageActivity.selection[3] = checked;
        }
    }
}
