package com.rnd.aedss_android.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.rnd.aedss_android.utils.Constants
import com.rnd.aedss_android.R

class EditTimeActivity : AppCompatActivity() {

    private lateinit var daySpinner: Spinner

    private lateinit var hourFromSpinner: Spinner
    private lateinit var minFromSpinner: Spinner
    private lateinit var periodFromSpinner: Spinner

    private lateinit var hourToSpinner: Spinner
    private lateinit var minToSpinner: Spinner
    private lateinit var periodToSpinner: Spinner

    private lateinit var titleText: TextView

    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_time)

        daySpinner = findViewById(R.id.day_spinner)
        val daySpinnerAdapter = ArrayAdapter<String>(this, R.layout.day_spinner_item, Constants.DAY_LIST)
        daySpinner.adapter = daySpinnerAdapter

        hourFromSpinner = findViewById(R.id.hour_spinner_from)
        val hourFromSpinnerAdapter = ArrayAdapter<String>(this, R.layout.day_spinner_item, Constants.createHourList())
        hourFromSpinner.adapter = hourFromSpinnerAdapter

        minFromSpinner = findViewById(R.id.min_spinner_from)
        val minFromSpinnerAdapter = ArrayAdapter<String>(this, R.layout.day_spinner_item, Constants.createMinList())
        minFromSpinner.adapter = minFromSpinnerAdapter

        periodFromSpinner = findViewById(R.id.period_spinner_from)
        val periodFromSpinnerAdapter = ArrayAdapter<String>(this, R.layout.day_spinner_item, Constants.PERIOD_LIST)
        periodFromSpinner.adapter = periodFromSpinnerAdapter

        hourToSpinner = findViewById(R.id.hour_spinner_to)
        val hourToSpinnerAdapter = ArrayAdapter<String>(this, R.layout.day_spinner_item, Constants.createHourList())
        hourToSpinner.adapter = hourToSpinnerAdapter

        minToSpinner = findViewById(R.id.min_spinner_to)
        val minToSpinnerAdapter = ArrayAdapter<String>(this, R.layout.day_spinner_item, Constants.createMinList())
        minToSpinner.adapter = minToSpinnerAdapter

        periodToSpinner = findViewById(R.id.period_spinner_to)
        val periodToSpinnerAdapter = ArrayAdapter<String>(this, R.layout.day_spinner_item, Constants.PERIOD_LIST)
        periodToSpinner.adapter = periodToSpinnerAdapter

        titleText = findViewById(R.id.title_name)
        var intent = intent
        if (intent != null) {
            title = intent.getStringExtra("Edit").toString()
            titleText.setText(title)
        }
    }
}