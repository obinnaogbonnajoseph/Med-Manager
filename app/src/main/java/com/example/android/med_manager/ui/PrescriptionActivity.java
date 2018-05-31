package com.example.android.med_manager.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
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

import com.example.android.med_manager.R;
import com.example.android.med_manager.data.PrescriptionInfo;
import com.example.android.med_manager.model.MedViewModel;


import java.util.Calendar;
import java.util.Date;


import static com.example.android.med_manager.ui.HomePageActivity.PRESCRIPTION;

public class PrescriptionActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String PRESCRIPTION_LOG = PrescriptionActivity.class.getSimpleName();
    // Value of the freq selected. This will be defined in the db
    private int mFreq;
    // Checks if prescription has changed
    private boolean mPrescriptionChanged = false;
    // On touch listener
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPrescriptionChanged = true;
            return false;
        }
    };

    public static String[] sMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
            "Aug", "Sep", "Oct", "Nov", "Dec"};

    // Create a view model instance
    private MedViewModel mViewModel;
    // Current prescription
    PrescriptionInfo currentMed;

    // Create a binding instance
    // Field variables to be written to the database
    private long mStartDateToDatabase;
    private long mEndDateToDatabase;
    private String mDescriptionTextToDatabase;
    private int[] mDaysSelectedToDatabase = new int[7];
    private String tRemind, tRemind1, tRemind2;
    // Define the views
    TextInputEditText mMedName;
    Spinner mSpinner;
    TextView mTimer,mTimer1,mTimer2,mStartDate,mStartYear,mEndDate,mEndYear;
    Button mSelectWeekdayButton;
    EditText mDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_prescription);
        // Set up the views
        mMedName = findViewById(R.id.text_med_name);
        mSpinner = findViewById(R.id.dialog_spinner);
        mTimer = findViewById(R.id.text_med_time);
        mTimer1 = findViewById(R.id.text_med_time_1);
        mTimer2 = findViewById(R.id.text_med_time_2);
        mSelectWeekdayButton = findViewById(R.id.btn_select_weekdays);
        mStartDate = findViewById(R.id.text_start_date);
        mStartYear = findViewById(R.id.text_start_year);
        mEndDate = findViewById(R.id.text_end_date);
        mEndYear = findViewById(R.id.text_end_year);
        mDescription = findViewById(R.id.text_note);
        // set up the toolbar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            // Make sure the back arrow is displayed
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Instantiate the view model
        mViewModel = ViewModelProviders.of(this).get(MedViewModel.class);
        // This action should be performed when there is an available intent
        Intent intent = getIntent();
        if(intent.hasExtra(PRESCRIPTION)) {
            Bundle bundle = intent.getBundleExtra(PRESCRIPTION);
            currentMed = bundle.getParcelable(PRESCRIPTION);
            // this means that this is a new entry
            setTitle(getString(R.string.prescription_activity_edit_med));
            // invalidate the options menu, so the "Delete" menu option is unavailable
            invalidateOptionsMenu();
            setUpViews();
        }
        setTitle(getString(R.string.title_activity_prescription));
        // Set up the on click for the views
        setUpClickListener();
        // Setup OnTouchListeners on necessary input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        setUpTouchListener();
        // Set up the spinner
        setUpSpinner();
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
        if(currentMed == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
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
                finish();
                return true;
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                DialogInterface.OnClickListener deleteButton = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the "Delete" button, hence entry is deleted
                        deletePrescription();
                        finish();
                    }
                };

                DialogInterface.OnClickListener cancelButton = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the "Cancel" button, so dismiss the dialog
                        // and continue editing the pet.
                        if (dialog != null) {
                            dialog.dismiss();
                            finish();
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
                        // By now the selected items should have been added to the database.
                        // Dismiss dialog
                        if(dialog != null) dialog.dismiss();
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
                // Create a new boolean array
                boolean[] checkedItems = new boolean[7];
                // Check to see if this is an existing data or not
                if(currentMed != null) {
                    // Convert integer to boolean. if 1, return true, else, return false
                    checkedItems[0] = intToBool(currentMed.getDaySunday());
                    checkedItems[1] = intToBool(currentMed.getDayMonday());
                    checkedItems[2] = intToBool(currentMed.getDayTuesday());
                    checkedItems[3] = intToBool(currentMed.getDayWednesday());
                    checkedItems[4] = intToBool(currentMed.getDayThursday());
                    checkedItems[5] = intToBool(currentMed.getDayFriday());
                    checkedItems[6] = intToBool(currentMed.getDaySaturday());
                } else {
                    // If this is not so make the checked items to be all equal to false
                    int i = 0;
                    while(i < 7) {
                        checkedItems[i] = intToBool(0);
                        i++;
                    }
                }
                AlertDialog.Builder listBuilder = new AlertDialog.Builder(
                        PrescriptionActivity.this);
                listBuilder.setTitle("WeekDays")
                        .setMultiChoiceItems(R.array.week_days, checkedItems,
                                new DialogInterface.
                                OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                // if checked, add 1, else add 0
                               mDaysSelectedToDatabase[which] = isChecked ? 1 : 0;
                            }
                        })
                        .setPositiveButton("Done",acceptSelectionButton)
                        .setNegativeButton("Cancel",rejectChangesButton)
                        .show();
                break;
            case R.id.text_start_date:
                new DatePickerDialog(PrescriptionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String startDate = String.valueOf(dayOfMonth) + " " + sMonths[month];
                        String startYear = String.valueOf(year);
                        Calendar startDateCalendar = Calendar.getInstance();
                        Date startDateInDate = startDateCalendar.getTime();
                        mStartDateToDatabase = startDateInDate.getTime();
                        mStartYear.setText(startYear);
                        mStartDate.setText(startDate);
                    }
                },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.
                        get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.text_end_date:
                new DatePickerDialog(PrescriptionActivity.this, new
                        DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String endDate = String.valueOf(dayOfMonth) + " " + sMonths[month];
                        String endYear = String.valueOf(year);
                        Calendar endDateCalendar = Calendar.getInstance();
                        Date endDateInDate = endDateCalendar.getTime();
                        mEndDateToDatabase = endDateInDate.getTime();
                        mEndYear.setText(endYear);
                        mEndDate.setText(endDate);
                    }
                },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.
                        get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.text_med_time:
                tRemind = setReminder(mTimer);
                break;
            case R.id.text_med_time_1:
                tRemind1 = setReminder(mTimer1);
                break;
            case R.id.text_med_time_2:
                tRemind2 = setReminder(mTimer2);
                break;
            case R.id.text_note:
                // Set up the edit text to be displayed in dialog box
                final EditText descriptionText = new EditText(this);
                descriptionText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                descriptionText.setInputType(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
                // Get the previous text
                descriptionText.setText(mDescription.getText().toString());
                // Dialog accept button
                DialogInterface.OnClickListener doneButton = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Set the value of the edit text
                        mDescriptionTextToDatabase = descriptionText.getText().toString();
                        mDescription.setText(mDescriptionTextToDatabase);
                        if(dialog != null) {
                            dialog.dismiss();
                        }
                    }
                };
                // Dialog cancel button
                DialogInterface.OnClickListener cancelButton = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dialog != null) {
                            dialog.dismiss();
                        }
                    }
                };
                AlertDialog.Builder editTextBuilder = new AlertDialog.Builder(PrescriptionActivity.this);
                editTextBuilder.setTitle("Add description")
                        .setView(descriptionText)
                        .setPositiveButton("Done",doneButton)
                        .setNegativeButton("Cancel",cancelButton)
                        .show();
        }
    }

    private boolean intToBool(int integer) { return integer == 1; }

    private void setUpSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter freqSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_freq_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        freqSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSpinner.setAdapter(freqSpinnerAdapter);
        // Get the previous selection from the database. This should be the default selection
        // This is only done if this is an existing medication
        // initialize the frequency
        if(currentMed != null) {
            mFreq = currentMed.getFrequency();
            mSpinner.setSelection(mFreq);
            // Load up the time from the database based on the value of freq
            switch(mFreq){
                case 0:
                    // Be sure that the other views are invisible
                    mTimer1.setVisibility(View.GONE);
                    mTimer2.setVisibility(View.GONE);
                    // Get the value of only one time
                    if(currentMed.getTimeRemind() != null) {
                        mTimer.setText(currentMed.getTimeRemind());
                    }
                    break;
                case 1:
                    // Be sure that the other views are invisible and right ones are visible
                    mTimer1.setVisibility(View.VISIBLE);
                    mTimer2.setVisibility(View.GONE);
                    // Get the value of two times
                    mTimer.setText(currentMed.getTimeRemind());
                    mTimer1.setText(currentMed.getTimeRemind1());
                    break;
                case 2:
                    // Be sure that the other views are invisible and right ones are visible
                    mTimer1.setVisibility(View.VISIBLE);
                    mTimer2.setVisibility(View.VISIBLE);
                    // Get the values of the three times
                    mTimer.setText(currentMed.getTimeRemind());
                    mTimer1.setText(currentMed.getTimeRemind1());
                    mTimer2.setText(currentMed.getTimeRemind2());
            }
        }
        // Set the integer mFreq to the constant values
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currentSelection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(currentSelection)) {
                    if (currentSelection.equals(getString(R.string.freq_once))) {
                        mFreq = 0;
                        // Set the necessary timers to be visible
                        mTimer1.setVisibility(View.GONE);
                        mTimer2.setVisibility(View.GONE);

                    } else if (currentSelection.equals(getString(R.string.freq_twice))) {
                        mFreq = 1;
                        // Set the necessary timers to be visible
                        mTimer1.setVisibility(View.VISIBLE);
                        mTimer2.setVisibility(View.GONE);
                    } else if (currentSelection.equals(getString(R.string.freq_thrice))) {
                        mFreq = 2;
                        // Set the necessary timers to be visible
                        mTimer1.setVisibility(View.VISIBLE);
                        mTimer2.setVisibility(View.VISIBLE);
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mFreq = 0;
            }
        });
    }

    private void setUpClickListener() {
        mSelectWeekdayButton.setOnClickListener(this);
        mDescription.setOnClickListener(this);
        mStartDate.setOnClickListener(this);
        mEndDate.setOnClickListener(this);
        mTimer.setOnClickListener(this);
        mTimer1.setOnClickListener(this);
        mTimer2.setOnClickListener(this);
    }

    private void setUpViews() {
        // Set the start date
        long startDate = currentMed.getStartDate();
        String startDay = (String) DateFormat.format("dd", startDate);
        String startMonth = sMonths[Integer.
                valueOf((String)DateFormat.format("MM",startDate))];
        String startDateString = startDay + " " + startMonth;
        mStartDate.setText(startDateString);
        mStartYear.setText(DateFormat.format("YYYY",startDate));
        // Set the end date
        long endDate = currentMed.getEndDate();
        String endDay = (String) DateFormat.format("dd", endDate);
        String endMonth = sMonths[Integer.
                valueOf((String)DateFormat.format("MM",endDate))];
        String endDateString = endDay + " " + endMonth;
        mEndDate.setText(endDateString);
        mEndYear.setText(DateFormat.format("YYYY", endDate));
        // Set the Med name
        mMedName.setText(currentMed.getMedName());
        // Set the Med description
        mDescription.setText(currentMed.getMedDescription());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpTouchListener() {
        mMedName.setOnTouchListener(mTouchListener);
        mDescription.setOnTouchListener(mTouchListener);
        mSpinner.setOnTouchListener(mTouchListener);
        mTimer.setOnTouchListener(mTouchListener);
        mTimer1.setOnTouchListener(mTouchListener);
        mTimer2.setOnTouchListener(mTouchListener);
        mSelectWeekdayButton.setOnTouchListener(mTouchListener);
        mStartDate.setOnTouchListener(mTouchListener);
        mEndDate.setOnTouchListener(mTouchListener);
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
            if (manager != null) {
                manager.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        }
       // Get the med name and description
        String medNameString= mMedName.getText().toString();
        PrescriptionInfo newMed = new PrescriptionInfo(medNameString,mDescriptionTextToDatabase,
                mFreq,tRemind, tRemind1, tRemind2,mDaysSelectedToDatabase[0],mDaysSelectedToDatabase[1],
                mDaysSelectedToDatabase[2],mDaysSelectedToDatabase[3],mDaysSelectedToDatabase[4],
                mDaysSelectedToDatabase[5],mDaysSelectedToDatabase[6],mStartDateToDatabase,
                mEndDateToDatabase);
        mViewModel.insert(newMed);
    }


    private void deletePrescription() {
        //Delete the entry
        mViewModel.delete(currentMed);
    }

    private String setReminder(final TextView timeSet) {
        // Get the current date, minute and hour
        Calendar currentTime = Calendar.getInstance();
        final String[] selectedTime = new String[1];
        // Initialize the time picker
        TimePickerDialog timePicker;
        timePicker = new TimePickerDialog(PrescriptionActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Save the time to the database
                Calendar setTime = Calendar.getInstance();
                setTime.set(Calendar.HOUR,hourOfDay);
                setTime.set(Calendar.MINUTE,minute);
                // Display the time
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
                selectedTime[0] = hourString + ":" + minuteString
                        + " " + aMpM;
                timeSet.setText(selectedTime[0]);
            }
        },currentTime.get(Calendar.HOUR_OF_DAY),currentTime.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(PrescriptionActivity.this));
        timePicker.setTitle("Select time");
        timePicker.show();
        return selectedTime[0];
    }
}
