package com.example.drawer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat


class TripListFragment : Fragment(R.layout.fragment_trip_list) {
    lateinit var userID: String
    //lateinit var imageURL: String
    val TAG = "TripListFragment"
    //var listEmptyOrNull = true


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())//get signed in account
        userID = account?.id.toString()//get id of the account

        return inflater.inflate(R.layout.fragment_trip_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val fab_plus = view.findViewById<FloatingActionButton>(R.id.fab_plus)//get floating plus button
        //create a snackbar instance
        val toastLoad  = Toast.makeText(requireContext(), "Loading trips...", Toast.LENGTH_LONG)
        toastLoad.show()//show the snackbar
        fab_plus.setOnClickListener {
            toastLoad.cancel()//remove the snackbars
            findNavController().navigate(R.id.action_nav_trip_list_to_nav_trip_edit)
        }
        val db = FirebaseFirestore.getInstance()//create firestore instance
        //get all the trips created by the user
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        val data = mutableListOf<Trip>()
        var tripFound = false
        val noTripsTV = view.findViewById<TextView>(R.id.noTripsFound)

        db.collection("trips").whereEqualTo("userID", userID).get().addOnSuccessListener() { documents ->
            if (documents?.isEmpty == false) {

                for (thisTrip in documents) {
                    tripFound = true
                    toastLoad.cancel()//remove the snackbar
                    data.add(
                        Trip(
                            requireContext(),
                            thisTrip["tripID"].toString(),
                            thisTrip["carPhotoPath"].toString(),
                            thisTrip["pickUpLocation"].toString(),
                            thisTrip["dropLocation"].toString(),
                            thisTrip["pickUpHour"].toString(),
                            thisTrip["data"].toString(),
                            thisTrip["estDuration"].toString(),
                            thisTrip["dropHour"].toString(),
                            thisTrip["avaSeats"].toString(),
                            thisTrip["price"].toString(),
                            thisTrip["addInfo"].toString()
                        )
                    )
                    data.sortByDescending {
                        SimpleDateFormat("dd/MM/yyyy HH:mm").parse("${it.depDate} ${it.depTime}").time
                    }
                    rv.layoutManager = LinearLayoutManager(rv.context)
                    rv.adapter = TripAdapter(data, findNavController())

                }
            }
            if(tripFound == false) {
                //show the snack to see if no trips were found
                toastLoad.cancel()
                noTripsTV.visibility=View.VISIBLE
            }
        }
    }
}

open class Trip(val context: Context, val id: String, val carImageURI: String, val depLocation: String, val arrLocation: String, val depTime: String, val depDate: String, val estDuration: String, val arrTime: String, val avSeats: String, val price: String, val addInfo: String) // We can leave it empty because the data class has already everything that we need

class TripAdapter( val data: List<Trip>, val navController: NavController) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    class TripViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val carImage = v.findViewById<ImageView>(R.id.imageViewProfileImage)
        val depLocation = v.findViewById<TextView>(R.id.pickupLocation)
        val arrLocation = v.findViewById<TextView>(R.id.destination)
        val depTime = v.findViewById<TextView>(R.id.departureHour)
        val arrTime = v.findViewById<TextView>(R.id.arrivalHour)
        val depDate = v.findViewById<TextView>(R.id.date)
        val duration = v.findViewById<TextView>(R.id.estimatedDuration)
        val seats = v.findViewById<TextView>(R.id.availableSeats)
        val price = v.findViewById<TextView>(R.id.price)
        val editButton = v.findViewById<ImageButton>(R.id.edit)
        val cardView = v.findViewById<CardView>(R.id.cardView)

        fun bind( t: Trip, navController: NavController) {
            if ((t.carImageURI != "null") &&(t.carImageURI != "default")) {//check if the photo tag is the default one
                val storage = FirebaseStorage.getInstance()//get instance of the storage
                val storageRef = storage.reference //create storage reference
                val imageRef = storageRef.child(t.carImageURI)//get storage ref to image
                imageRef.downloadUrl.addOnSuccessListener { Uri ->//download url
                    val carImageURL = Uri.toString()
                    Glide.with(t.context).load(carImageURL).into(carImage)
                    carImage.tag = t.carImageURI
                }
            } else {
                carImage.load(R.drawable.img)//load default image
            }
            depLocation.text = t.depLocation
            arrLocation.text = t.arrLocation
            depTime.text = t.depTime
            arrTime.text = t.arrTime
            depDate.text = t.depDate
            duration.text = t.estDuration
            seats.text = t.avSeats
            price.text = t.price
            editButton.setOnClickListener {

                navController.navigate(R.id.action_nav_trip_list_to_nav_trip_edit, bundleOf("TripID" to t.id))
                //Toast.makeText(depLocation.context, "Test", Toast.LENGTH_LONG).show()
            }
            cardView.setOnClickListener {
                //navController.navigate(R.id.action_tripListFragment_to_tripDetailsFragment, bundleOf("TripID" to t.id))
                navController.navigate(R.id.action_nav_trip_list_to_nav_trip_detail, bundleOf("TripID" to t.id))
                //Toast.makeText(depLocation.context, "Test", Toast.LENGTH_LONG).show()
            }
        }

        fun unbind() {
            editButton.setOnClickListener { null }
            cardView.setOnClickListener { null }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripAdapter.TripViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layout = layoutInflater.inflate(R.layout.list_item, parent, false)
        return TripAdapter.TripViewHolder(layout)
    }

    override fun onBindViewHolder(holder: TripAdapter.TripViewHolder, position: Int) {
        holder.bind( data[position], navController)
    }

    override fun getItemCount(): Int {
        return data.size
    }



    override fun onViewRecycled(holder: TripAdapter.TripViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

}

