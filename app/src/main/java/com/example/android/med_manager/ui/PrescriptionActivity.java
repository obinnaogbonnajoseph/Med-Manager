package com.example.android.med_manager.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.med_manager.R;

import java.util.ArrayList;
import java.util.Calendar;

import static android.R.attr.button;
import static android.R.attr.description;
import static android.R.attr.min;
import static android.R.attr.startYear;
import static android.os.Build.VERSION_CODES.M;

public class PrescriptionActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String PRESCRIPTION_LOG = PrescriptionActivity.class.getSimpleName();
    private Spinner mFreqSpinner;
    // Value of the freq selected. This will be defined in the db
    private int mFreq = 0;
    // Checks if prescription has changed
    private boolean mPrescriptionChanged = false;
    private ArrayList<Integer> mWeekDays = new ArrayList<>();
    // Views
    private TextView mTime, mTime1, mTime2, mStartDate, mEndDate, mStartYear, mEndYear;
    private EditText mDescription;
    private TextInputEditText mMedName;
    // On touch listener
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPrescriptionChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);
        // set up the toolbar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            // Make sure the back arrow is displayed
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Let's assume for now it's a new entry, so we invalidate options menu
        invalidateOptionsMenu();
        // Set up the views
        mTime = findViewById(R.id.text_med_time);
        mTime1 = findViewById(R.id.text_med_time_1);
        mTime2 = findViewById(R.id.text_med_time_2);
        mStartDate = findViewById(R.id.text_start_date);
        mEndDate = findViewById(R.id.text_end_date);
        mStartYear = findViewById(R.id.text_start_year);
        mEndYear = findViewById(R.id.text_end_year);
        mDescription = findViewById(R.id.text_note);
        mMedName = findViewById(R.id.text_med_name);
        mFreqSpinner = findViewById(R.id.dialog_spinner);
        Button sWeekDay = findViewById(R.id.btn_select_weekdays);
        // Set up the on click for the views
        sWeekDay.setOnClickListener(this);
        mStartDate.setOnClickListener(this);
        mEndDate.setOnClickListener(this);
        mTime.setOnClickListener(this);
        mTime1.setOnClickListener(this);
        mTime2.setOnClickListener(this);

        // Setup OnTouchListeners on necessary input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mMedName.setOnTouchListener(mTouchListener);
        mDescription.setOnTouchListener(mTouchListener);
        mFreqSpinner.setOnTouchListener(mTouchListener);
        mTime.setOnTouchListener(mTouchListener);
        mTime1.setOnTouchListener(mTouchListener);
        mTime2.setOnTouchListener(mTouchListener);
        sWeekDay.setOnTouchListener(mTouchListener);
        mStartDate.setOnTouchListener(mTouchListener);
        mEndDate.setOnTouchListener(mTouchListener);

        // Set up the spinner
        setUpSpinner();
        // Set up an editor listener for text note and med name
        mDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    // Capture the text here and display it
                    mDescription.setText(mDescription.getText().toString());
                    // Hide the soft keyboard
                    mDescription.clearFocus();
                    View view = PrescriptionActivity.this.getCurrentFocus();
                    if(view!= null) {
                        InputMethodManager manager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(view.getWindowToken(),0);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_prescription_activity, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new entry, hide the "Delete" menu item.
        // Let's assume for now it is always new, till we add the database
        MenuItem menuItem = menu.findItem(R.id.action_delete);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling the back button
        if(!mPrescriptionChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButton =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Discard" button, close the current activity
                        finish();
                    }
                };

        DialogInterface.OnClickListener keepEditButton =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Keep editing" button, so dismiss the dialog
                        // and continue editing
                        if(dialog != null) {
                            dialog.dismiss();
                        }
                    }
                };
        showConfirmationDialog(getString(R.string.dialog_unsaved_changes_msg),discardButton,
                keepEditButton, getString(R.string.discard), getString(R.string.keep_editing));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId){
            // Respond to a click on the "Up" arrow button in the app
            case(android.R.id.home):
                // if no changes to prescription, continue with navigating up to previous
                // activity
                if(!mPrescriptionChanged) {
                    onBackPressed();
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButton =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked "Discard" button, closes the current activity
                                finish();
                            }
                        };
                DialogInterface.OnClickListener keepEditButton =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked "Keep editing" button, so dismiss the dialog
                                // and continue editing
                                if(dialog != null) {
                                    dialog.dismiss();
                                }
                            }
                        };
                showConfirmationDialog(getString(R.string.dialog_unsaved_changes_msg),discardButton,
                        keepEditButton, getString(R.string.discard), getString(R.string.keep_editing));
                return true;
            case R.id.action_save:
                // Save to database
                savePrescription();
                // Exit the activity
                finish();
                return true;
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                DialogInterface.OnClickListener deleteButton = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the "Delete" button, hence entry is deleted
                        deletePrescription();
                    }
                };

                DialogInterface.OnClickListener cancelButton = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the "Cancel" button, so dismiss the dialog
                        // and continue editing the pet.
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                };
                showConfirmationDialog(getString(R.string.dialog_delete_msg),
                        deleteButton,cancelButton, getString(R.string.delete),
                        getString(R.string.cancel));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        Calendar calendar = Calendar.getInstance();
        switch(viewId) {
            case R.id.btn_select_weekdays:
                // Dialog accept button
                DialogInterface.OnClickListener acceptSelectionButton = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss dialog
                        if(dialog != null) dialog.dismiss();
                        Log.d(PRESCRIPTION_LOG,"Contents of weekdays" + mWeekDays);
                    }
                };
                // Dialog cancel button
                DialogInterface.OnClickListener rejectChangesButton = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dialog != null) {
                            dialog.dismiss();
                        }
                    }
                };
                AlertDialog.Builder listBuilder = new AlertDialog.Builder(PrescriptionActivity.this);
                listBuilder.setTitle("WeekDays")
                        .setMultiChoiceItems(R.array.week_days, null, new DialogInterface.
                                OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                // Perform an action if an option is selected
                                if(isChecked) {
                                    mWeekDays.add(which);
                                } else if(mWeekDays.contains(which)) {
                                    mWeekDays.remove(which);
                                }
                            }
                        })
                        .setPositiveButton("Done",acceptSelectionButton)
                        .setNegativeButton("Cancel",rejectChangesButton)
                        .show();
                break;
            case R.id.text_start_date:
                new DatePickerDialog(PrescriptionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String startDate = String.valueOf(dayOfMonth) + " " + setMonth(month);
                        String startYear = String.valueOf(year);
                        mStartYear.setText(startYear);
                        mStartDate.setText(startDate);
                    }
                },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.
                        get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.text_end_date:
                new DatePickerDialog(PrescriptionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String endDate = String.valueOf(dayOfMonth) + " " + setMonth(month);
                        String endYear = String.valueOf(year);
                        mEndYear.setText(endYear);
                        mEndDate.setText(endDate);
                    }
                },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.
                        get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.text_med_time:
                setReminder(mTime);
                break;
            case R.id.text_med_time_1:
                setReminder(mTime1);
                break;
            case R.id.text_med_time_2:
                setReminder(mTime2);
                break;
        }
    }

    private void setUpSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter freqSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_freq_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        freqSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mFreqSpinner.setAdapter(freqSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mFreqSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.freq_once))) {
                        mFreq = 0;
                        // Set the necessary timers to be visible
                        mTime1.setVisibility(View.GONE);
                        mTime2.setVisibility(View.GONE);

                    } else if (selection.equals(getString(R.string.freq_twice))) {
                        mFreq = 1;
                        // Set the necessary timers to be visible
                        mTime1.setVisibility(View.VISIBLE);
                        mTime2.setVisibility(View.GONE);
                    } else if (selection.equals(getString(R.string.freq_thrice))) {
                        mFreq = 2;
                        // Set the necessary timers to be visible
                        mTime1.setVisibility(View.VISIBLE);
                        mTime2.setVisibility(View.VISIBLE);
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mFreq = 20;
            }
        });
    }

    private void showConfirmationDialog(String dialogMessage,
                                        DialogInterface.OnClickListener positiveButton,
                                        DialogInterface.OnClickListener negativeButton,
                                        String positiveMsg, String negativeMsg) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(dialogMessage)
                .setPositiveButton(positiveMsg, positiveButton)
                .setNegativeButton(negativeMsg, negativeButton).create().show();
    }

    private void savePrescription() {
        // Remove focus from all edit texts and close the soft keyboard
        View view = PrescriptionActivity.this.getCurrentFocus();
        if(view!= null) {
            InputMethodManager manager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
        // Add entry to database
        Toast.makeText(this,"Prescription added",Toast.LENGTH_SHORT).show();
    }


    private void deletePrescription() {
        //Delete the entry
        // Exit the activity
        finish();
    }

    private void setReminder(final TextView timeSet) {
        // Get the current date, minute and hour
        Calendar currentTime = Calendar.getInstance();
        int minute = currentTime.get(Calendar.MINUTE);
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        // Initialize the time picker
        TimePickerDialog timePicker;
        timePicker = new TimePickerDialog(PrescriptionActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String aMpM = "AM";
                int currentHour = hourOfDay;
                if(hourOfDay > 12) {
                    currentHour = hourOfDay - 12;
                    aMpM = "PM";
                } else if(hourOfDay == 12) aMpM = "PM";
                // Set up the time to display properly
                String hourString = String.valueOf(currentHour);
                String minuteString = String.valueOf(minute);
                if(minute < 10) {
                    minuteString = "0" + String.valueOf(minute);
                } else if(currentHour < 10) {
                    hourString = "0" + String.valueOf(currentHour);
                }
                String selectedTime = hourString + ":" + minuteString
                        + " " + aMpM;
                timeSet.setText(selectedTime);
            }
        },hour,minute, DateFormat.is24HourFormat(PrescriptionActivity.this));
        timePicker.setTitle("Select time");
        timePicker.show();
    }

    private String setMonth(int month) {
        switch(month) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                return "Invalid month";
        }
    }
}
