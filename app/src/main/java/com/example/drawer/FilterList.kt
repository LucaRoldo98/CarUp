package com.example.drawer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import java.util.*

class FilterList : Fragment(R.layout.fragment_filter_list) {

    var avaSeats: Int = 0
    var priceMax: Int = 1000
    lateinit var pickUpH: String
    lateinit var dropH: String
    lateinit var day: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //when I press the back button I go back to others trip list fragment
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_nav_filter_list_to_nav_trip_list_others)
        }

        return inflater.inflate(R.layout.fragment_filter_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val avaSeatsMin = view.findViewById<EditText>(R.id.textViewAvailableSeats)
        val price = view.findViewById<EditText>(R.id.Price)
        val pickUpLocation = view.findViewById<EditText>(R.id.textViewPickupLocation)
        val dropLocation = view.findViewById<EditText>(R.id.textViewDropLocation)
        val data = view.findViewById<EditText>(R.id.tvDateField)
        val pickUpHour = view.findViewById<EditText>(R.id.textViewDepartureHour)
        val dropHour = view.findViewById<EditText>(R.id.textViewArrivalHour)

        /*2******************************DATA PICKER******************************2*/

        data.setOnClickListener {
            val c = Calendar.getInstance()
            val dataPicker = DatePickerDialog(it.context, { _, i, i2, i3 ->

                data.setText("$i3/${i2 + 1}/$i")

            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

            dataPicker.datePicker.minDate = c.timeInMillis

            dataPicker.show()
        }
        /*******************************************************************************/


        /****************USA IL TIME PICKER PER SELEZIONARE L'ORA'**********************/

        pickUpHour.setOnClickListener {
            val c = Calendar.getInstance()
            val tp = TimePickerDialog(it.context, TimePickerDialog.OnTimeSetListener { _, i, i1 ->

                //pickUpHour.setText("h:$i m:$i1")
                if (i1 == 0) {
                    pickUpHour.setText("$i:00")
                } else if (i1 < 10) {
                    pickUpHour.setText("$i:0$i1")
                } else {
                    pickUpHour.setText("$i:$i1")
                }

            }, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), true)

            tp.show()
        }
        dropHour.setOnClickListener {
            val c = Calendar.getInstance()
            val tp = TimePickerDialog(it.context, TimePickerDialog.OnTimeSetListener { _, i, i1 ->

                //pickUpHour.setText("h:$i m:$i1")
                if (i1 == 0) {
                    dropHour.setText("$i:00")
                } else if (i1 < 10) {
                    dropHour.setText("$i:0$i1")
                } else {
                    dropHour.setText("$i:$i1")
                }

            }, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), true)

            tp.show()
        }
        /*******************************************************************************/

        val buttonFilter = view.findViewById<Button>(R.id.buttonFilter)
        //when the button is pressed, filter sends data to others trip list
        buttonFilter.setOnClickListener {
            if(pickUpLocation.text.toString().isEmpty() || dropLocation.text.toString().isEmpty() || data.text.toString().isEmpty()) {
                Toast.makeText(context, "Fill pickup location, drop location and data", Toast.LENGTH_SHORT).show()
            }else{
                //If both cities are inserted, I check if the other fields are not empty, in this last case, I fill them with default values
                if(avaSeatsMin.text.toString().isNotEmpty()){
                    avaSeats = avaSeatsMin.text.toString().toInt()
                }
                if(price.text.toString().isNotEmpty()){
                    priceMax = price.text.toString().toInt()
                }
                if(pickUpHour.text.toString().isNotEmpty()){
                    pickUpH = pickUpHour.text.toString()
                }else{
                    pickUpH = "00:00"
                }
                if(dropHour.text.toString().isNotEmpty()){
                    dropH = dropHour.text.toString()
                }
                else{
                    dropH = "23:59"
                }
                if(data.text.toString().isNotEmpty()){
                    day = data.text.toString()
                }else{
                    day = " "
                }
                findNavController().navigate(
                    R.id.action_nav_filter_list_to_nav_trip_list_others, bundleOf(
                        "avaSeatsMin" to avaSeats,
                        "priceMax" to priceMax,
                        "pickUpLocation" to pickUpLocation.text.toString().dropLastWhile { !it.isLetter() }, //remove the space after the name of the city
                        "dropLocation" to dropLocation.text.toString().dropLastWhile { !it.isLetter() },
                        "pickUpHour" to pickUpH,
                        "dropHour" to dropH,
                        "data" to data.text.toString(),
                        "check" to '0'
                    )
                )
            }
        }
    }
}