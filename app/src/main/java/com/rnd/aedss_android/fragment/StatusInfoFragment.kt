package com.rnd.aedss_android.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.rnd.aedss_android.utils.Constants
import com.rnd.aedss_android.R
import com.rnd.aedss_android.activity.GalleryActivity

class StatusInfoFragment : Fragment() {

    private lateinit var acSection: LinearLayout
    private lateinit var lightSection: LinearLayout
    private lateinit var doorSection: LinearLayout
    private lateinit var gallerySection: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_status_info, container, false)

        acSection = view.findViewById(R.id.ac_section)
        acSection.setOnClickListener{
            showOptionDialog("AC", true)
        }
        lightSection = view.findViewById(R.id.light_section)
        lightSection.setOnClickListener{
            showOptionDialog("Light", false)
        }
        doorSection = view.findViewById(R.id.door_section)
        doorSection.setOnClickListener{
            showOptionDialog("Door", false)
        }

        gallerySection = view.findViewById(R.id.gallery_section)
        gallerySection.setOnClickListener{
            val intent = Intent(context, GalleryActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        return view
    }

    private fun showOptionDialog(title: String, status: Boolean) {
        val dialogView = View.inflate(context, R.layout.option_dialog, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val optionDialog = builder.create()
        optionDialog.show()
        optionDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var optionDialogTitle = dialogView.findViewById<TextView>(R.id.option_dialog_title)
        var optionDialogPowerSection = dialogView.findViewById<CardView>(R.id.power_section)
        var optionDialogFrequencyBtn = dialogView.findViewById<Button>(R.id.frequency_btn)
        var optionDialogPowerBtn = dialogView.findViewById<Button>(R.id.power_btn)

        optionDialogTitle.setText(title)

        if (title == "Door") {
            optionDialogPowerSection.visibility = View.GONE
        }

        optionDialogPowerBtn.setOnClickListener {
            optionDialog.dismiss()
            showPowerDialog(title, status)
        }

        optionDialogFrequencyBtn.setOnClickListener{
            optionDialog.dismiss()
            showFrequencyDialog(title)
        }
    }

    private fun showPowerDialog(title: String, status: Boolean) {
        val dialogView = View.inflate(context, R.layout.power_dialog, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val powerDialog = builder.create()
        powerDialog.show()
        powerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var powerDialogTitle = dialogView.findViewById<TextView>(R.id.power_dialog_title)
        var powerDialogCurrentStatus = dialogView.findViewById<TextView>(R.id.status_text)
        var powerDialogNextStatus= dialogView.findViewById<TextView>(R.id.question_text)

        powerDialogTitle.setText(title)
        if (status) {
            powerDialogCurrentStatus.setText("ON")
            powerDialogNextStatus.setText("OFF")
        } else {
            powerDialogCurrentStatus.setText("OFF")
            powerDialogNextStatus.setText("ON")
        }
    }

    private fun showFrequencyDialog(title: String) {
        val dialogView = View.inflate(context, R.layout.frequency_dialog, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val frequencyDialog = builder.create()
        frequencyDialog.show()
        frequencyDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var hourSpinner = dialogView.findViewById<Spinner>(R.id.hour_spinner)
        val spinnerAdapter =
            context?.let { ArrayAdapter<String>(it, R.layout.hour_spinner_item, Constants.createHourList()) }
        hourSpinner.adapter = spinnerAdapter

        var frequencyDialogTitle = dialogView.findViewById<TextView>(R.id.frequency_dialog_title)
        frequencyDialogTitle.setText(title)
    }
}