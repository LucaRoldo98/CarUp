package com.example.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat


class TripRatingList : Fragment(R.layout.fragment_trip_rating_list) {

    lateinit var userID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())//get signed in account
        userID = account?.id.toString()//get id of the account
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_rating_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toastLoad = Toast.makeText(requireContext(), "Loading trips...", Toast.LENGTH_LONG)
        toastLoad.show()//show the snackbar
        val db = FirebaseFirestore.getInstance()//create firestore instance
        //get all the trips created by the user
        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewRating)
        val data = mutableListOf<TripOthers>()
        val noTripsFoundTV = view.findViewById<TextView>(R.id.noTripsFound)


        val timeStamp = System.currentTimeMillis()

        db.collection("trips").whereArrayContains("acceptUsersID", userID).get()
            .addOnSuccessListener() { documents ->
                if (documents?.isEmpty == false) {
                    var thereIsOneTrip = false
                    for (thisTrip in documents) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm").parse("${thisTrip["data"]} ${thisTrip["dropHour"]}")
                        if(thisTrip["usersRateDriver"] != null) {
                            val usr = thisTrip["usersRateDriver"] as Map<String, Int>
                            if (timeStamp > sdf.time && !usr.containsKey(userID)) {
                                thereIsOneTrip = true
                                toastLoad.cancel()
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
                            }
                        }
                        data.sortByDescending {
                            SimpleDateFormat("dd/MM/yyyy HH:mm").parse("${it.depDate} ${it.depTime}").time }
                        rv.layoutManager = LinearLayoutManager(rv.context)
                        rv.adapter = TripAdapterRate(data, findNavController())
                    }
                    if(thereIsOneTrip == false){
                        toastLoad.cancel()
                        noTripsFoundTV.visibility=View.VISIBLE
                    }
                } else {
                    //show the snack to see if no trips were found
                    toastLoad.cancel()
                    noTripsFoundTV.visibility=View.VISIBLE
                }
            }
    }
}



class TripAdapterRate(val data: List<TripOthers>, val navController: NavController) : RecyclerView.Adapter<TripAdapterRate.TripViewRate>() {

    class TripViewRate(v: View) : RecyclerView.ViewHolder(v) {
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
                    R.id.action_rateTripsListFragment_to_rateDriverTripsListFragment, bundleOf(
                        "otherUserID" to t.otherUserID,
                        "TripId" to t.id
                    )
                )
            }
        }

        fun unbind() {
            likeButton.setOnClickListener { null }
            cardView.setOnClickListener { null }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewRate {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layout = layoutInflater.inflate(R.layout.list_item, parent, false)
        return TripViewRate(layout)
    }

    override fun onBindViewHolder(holder: TripViewRate, position: Int) {
        holder.bind(data[position], navController)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onViewRecycled(holder: TripAdapterRate.TripViewRate) {
        super.onViewRecycled(holder)
        holder.unbind()
    }


}
