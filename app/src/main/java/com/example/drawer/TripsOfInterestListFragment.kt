package com.example.drawer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat

class TripsOfInterestListFragment : Fragment() {
    lateinit var userID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())//get signed in account
        userID = account?.id.toString()//get id of the account
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trips_of_interest_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toastLoad  = Toast.makeText(requireContext(), "Loading trips...", Toast.LENGTH_LONG)
        toastLoad.show()//show the snackbar
        val db = FirebaseFirestore.getInstance()//create firestore instance
        //get all the trips created by the user
        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewInterest)
        val data = mutableListOf<TripOthers>()
        val noTripsFoundTV = view.findViewById<TextView>(R.id.noTripsFound)

        db.collection("trips").whereArrayContains("requestUsersID", userID).get().addOnSuccessListener() { documents ->
            if (documents?.isEmpty == false) {
                val timeStamp = System.currentTimeMillis()
                for (thisTrip in documents) {
                    val sdf =
                        SimpleDateFormat("dd/MM/yyyy HH:mm").parse("${thisTrip["data"]} ${thisTrip["pickUpHour"]}")
                    //only trips with a date greater than the current one
                    if (timeStamp < sdf.time) {
                        toastLoad.cancel()//remove the snackbar
                        data.add(
                            TripOthers(
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
                                thisTrip["addInfo"].toString(),
                                thisTrip["userID"].toString()
                            )
                        )
                        data.sortBy {
                            SimpleDateFormat("dd/MM/yyyy HH:mm").parse("${it.depDate} ${it.depTime}").time }
                        rv.layoutManager = LinearLayoutManager(rv.context)
                        rv.adapter = TripAdapterInterest(data, findNavController())
                    }
                }
            } else {
                //show the snack to see if no trips were found
                toastLoad.cancel()
                noTripsFoundTV.visibility=View.VISIBLE
            }
        }
    }
}


class TripAdapterInterest(val data: List<TripOthers>, val navController: NavController) : RecyclerView.Adapter<TripAdapterInterest.TripViewHolder>() {

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
        val cardView = v.findViewById<CardView>(R.id.cardView)
        val likeButton = v.findViewById<ImageButton>(R.id.edit)


        fun bind(t: TripOthers, navController: NavController) {
            if ((t.carImageURI != "null") && (t.carImageURI != "default")) {//check if the photo tag is the default one
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

            //Rimuovo la matitina di modifica
            likeButton.visibility = View.INVISIBLE

            cardView.setOnClickListener {
                navController.navigate(
                    R.id.action_tripsOfInterestListFragment_to_nav_trip_detail, bundleOf(
                        "TripID" to t.id,
                        "otherUserID" to t.otherUserID,
                        "check" to '0',
                    )
                )
                //Toast.makeText(depLocation.context, "Test", Toast.LENGTH_LONG).show()
            }
        }

        fun unbind() {
            likeButton.setOnClickListener { null }
            cardView.setOnClickListener { null }
        }
    }


    override fun onViewRecycled(holder: TripAdapterInterest.TripViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layout = layoutInflater.inflate(R.layout.list_item, parent, false)
        return TripViewHolder(layout)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(data[position], navController)
    }

    override fun getItemCount(): Int {
        //get number of cardview of our list
        return data.size
    }
}

