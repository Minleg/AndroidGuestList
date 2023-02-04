package com.bignerdranch.android.guestlist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

const val LAST_GUEST_NAME_KEY = "last-guest-name_bundle-key"

class MainActivity : AppCompatActivity() {

    private lateinit var addGuestButton: Button
    private lateinit var clearGuestButton: Button
    private lateinit var newGuestEditText: EditText
    private lateinit var guestList: TextView
    private lateinit var lastGuestAdded: TextView

    // mutable list to store all the guests
    // val guestNames = mutableListOf<String>()

    // activity doesn't exist here \at this point, thats why lazy initialization
    private val guestListViewModel: GuestListViewModel by lazy {
        // lazy initialization - lamda won't be called until guestListViewmodel is used
        ViewModelProvider(this).get(GuestListViewModel::class.java) // made viewmodel activity aware and gets the class definition of GuestListViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addGuestButton = findViewById(R.id.add_guest_button)
        clearGuestButton = findViewById(R.id.clear_guest_button)
        newGuestEditText = findViewById(R.id.new_guest_input)
        guestList = findViewById(R.id.list_of_guests)
        lastGuestAdded = findViewById(R.id.last_guest_added)

        addGuestButton.setOnClickListener {
            addNewGuest()
        }

        clearGuestButton.setOnClickListener {
            guestListViewModel.clearGuestNames() // clears list in the view model
            updateGuestList() // updates the activity with the updated view model data
            lastGuestAdded.text = "" // last guest added text set to empty string
        }

        // checks if any data is saved with key LAST_GUEST_NAME_KEY when the activity starts or restarts
        val savedLastGuestMessage = savedInstanceState?.getString(LAST_GUEST_NAME_KEY)
        lastGuestAdded.text = savedLastGuestMessage

        updateGuestList() // update from view model - needed when activity is destroyed and recreated
    }

    // Android system will call this function when activity is about to be destroyed or shut down
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_GUEST_NAME_KEY, lastGuestAdded.text.toString())
    }

    private fun addNewGuest() {
        // gets the name of the guest entered by the user
        val newGuestName = newGuestEditText.text.toString()
        if (newGuestName.isNotBlank()) { // makes sure user entered some name
            // guestNames.add(newGuestName)
            guestListViewModel.addGuest(newGuestName)
            updateGuestList()
            newGuestEditText.text.clear() // clears the field to enter the name of the next guest
            lastGuestAdded.text = getString(R.string.Last_guest_message, newGuestName)
        }
    }

    private fun updateGuestList() {
        // guest mutable lists is converted to string by first sorting the guest names, then joining together by adding a newline
        // between each guest name.
        val guests = guestListViewModel.getSortedGuestNames()
        val guestDisplay = guests.joinToString(separator = "\n")
        guestList.text = guestDisplay
    }
}
