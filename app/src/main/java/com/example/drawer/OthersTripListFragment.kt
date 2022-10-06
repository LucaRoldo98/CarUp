package com.example.drawer

import android.content.Context
import android.os.Bundle
import android.view.*
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
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat


class OthersTripListFragment : Fragment(R.layout.fragment_others_trip_list) {

    lateinit var userID: String
    lateinit var pickUpLocation: String
    lateinit var dropLocation: String
    lateinit var pickUpHour: String
    lateinit var dropHour: String
    lateinit var day: String
    lateinit var collection3: Query
    var avaSeatsMin: Int = 0
    var priceMax: Int = 1000
    var pickUpHour_hh: Int = 0
    var pickUpHour_mm: Int = 0
    var dropHour_hh: Int = 23
    var dropHour_mm: Int = 59

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())//get signed in account
        userID = account?.id.toString()//get id of the account

        setHasOptionsMenu(true)



        //I retrieve data from filter list fragment to set the filter criteria for Firestore
        val bundle = this.arguments
        if(bundle != null) {
            //I fill avaSeatsMin from bundle
            avaSeatsMin = arguments?.getInt("avaSeatsMin")!!

            //I fill priceMax from bundle
            priceMax = arguments?.getInt("priceMax")!!


            pickUpHour = arguments?.getString("pickUpHour").toString()
             //I take hour and minutes separately
             pickUpHour_hh = pickUpHour.substringBefore(':').toInt()
             pickUpHour_mm = pickUpHour.substringAfter(':').toInt()


             dropHour = arguments?.getString("dropHour").toString()
             dropHour_hh = dropHour.substringBefore(':').toInt()
             dropHour_mm = dropHour.substringAfter(':').toInt()

            //The user is obliged to fill this fields in the filter fragment
            pickUpLocation = arguments?.getString("pickUpLocation").toString().toLowerCase()
            dropLocation = arguments?.getString("dropLocation").toString().toLowerCase()
            day = arguments?.getString("data").toString()
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_others_trip_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //create a snackbar instance and show it
        //Snackbar.make(view, "Loading trips...", Snackbar.LENGTH_SHORT).show()

        // The snackbar makes the app crash...
        val loading = Toast.makeText(requireContext(), "Loading trips...", Toast.LENGTH_LONG)
        loading.show()
        val db = FirebaseFirestore.getInstance()//create firestore instance
        //get all the trips created by other users
        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewOthers)
        val data = mutableListOf<TripOthers>()
        val noTripsTV = view.findViewById<TextView>(R.id.noTripsFound)



        //concatenation of filter is not possible if filters are applied to different field
        //so, every time a new variable is created, applying to it a new filter
        //if the fragment list is called by the filter fragment, firebase is called with filters
        if(arguments?.get("check") == '0') {
            collection3 = db.collection("trips").whereEqualTo("pickUpLocality", pickUpLocation).whereEqualTo("dropLocality", dropLocation).whereEqualTo("data", day)
        }else{
            //otherwise all trips are listed
            collection3= db.collection("trips")
        }
        collection3.get().addOnSuccessListener { documents ->
            if(documents?.isEmpty == false){
                //used to check if others trips were found
                var tripFound = false
                val timeStamp = System.currentTimeMillis()

                for (thisTripOthers in documents) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm").parse("${thisTripOthers["data"]} ${thisTripOthers["pickUpHour"]}")
                    //prende solo trip con data maggiore dell'attuale
                    if (timeStamp < sdf.time) {
                        if (thisTripOthers["userID"] != userID &&
                            //filter to select only trips with a minimum nuber of seats
                            thisTripOthers["avaSeats"].toString().toInt() >= avaSeatsMin &&
                            //filter to select only trips with a max price
                            thisTripOthers["price"].toString().toInt() <= priceMax &&
                            //filter to select only trips after a certain pickup hour and minutes. I convert all in minutes
                            (thisTripOthers["pickUpHour"].toString().substringBefore(':')
                                .toInt() * 60 + thisTripOthers["pickUpHour"].toString()
                                .substringAfter(':')
                                .toInt()) >= (pickUpHour_hh * 60 + pickUpHour_mm) &&

                            //filter to select only trip before a certain drop hour and minutes. I convert all in minutes
                            (thisTripOthers["dropHour"].toString().substringBefore(':')
                                .toInt() * 60 + thisTripOthers["dropHour"].toString()
                                .substringAfter(':').toInt()) <= (dropHour_hh * 60 + dropHour_mm)

                        ) {
                            tripFound = true
                            data.add(
                                TripOthers(
                                    requireContext(),
                                    thisTripOthers["tripID"].toString(),
                                    thisTripOthers["carPhotoPath"].toString(),
                                    thisTripOthers["pickUpLocation"].toString(),
                                    thisTripOthers["dropLocation"].toString(),
                                    thisTripOthers["pickUpHour"].toString(),
                                    thisTripOthers["data"].toString(),
                                    thisTripOthers["estDuration"].toString(),
                                    thisTripOthers["dropHour"].toString(),
                                    thisTripOthers["avaSeats"].toString(),
                                    thisTripOthers["price"].toString(),
                                    thisTripOthers["addInfo"].toString(),
                                    thisTripOthers["userID"].toString()
                                )
                            )
                            data.sortBy {
                                SimpleDateFormat("dd/MM/yyyy HH:mm").parse("${it.depDate} ${it.depTime}").time }
                            rv.layoutManager = LinearLayoutManager(rv.context)
                            rv.adapter = TripAdapterOthers(data, findNavController())
                            loading.cancel() // Make the toast disappear
                        }
                    }
                }
                if(!tripFound)
                {
                    loading.cancel()
                    noTripsTV.visibility=View.VISIBLE
                }
            } else {
                loading.cancel()
                noTripsTV.visibility=View.VISIBLE
            }
        }.addOnFailureListener {
            Snackbar.make(view, "Failed to retrieve trips", Snackbar.LENGTH_INDEFINITE).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu_item, menu)
        return
    }

    //clicking the search sign I go to filter fragment
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionSearch) {
            findNavController().navigate(R.id.action_nav_trip_list_others_to_nav_filter_list)
        }
        return super.onOptionsItemSelected(item)
    }

}

open class TripOthers(
    val context: Context,
    val id: String,
    val carImageURI: String,
    val depLocation: String,
    val arrLocation: String,
    val depTime: String,
    val depDate: String,
    val estDuration: String,
    val arrTime: String,
    val avSeats: String,
    val price: String,
    val addInfo: String,
    val otherUserID: String
) // We can leave it empty because the data class has already everything that we need

class TripAdapterOthers(val data: List<TripOthers>, val navController: NavController) : RecyclerView.Adapter<TripAdapterOthers.TripViewHolder>() {

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

            //Rimuovo la matitina di modifica
            likeButton.visibility=View.INVISIBLE
            /*cambio la foto della penna con la foto del pollicione nella cardview*/
            /*likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24)
            likeButton.setOnClickListener {
                navController.navigate(
                    R.id.action_nav_trip_list_others_to_nav_trip_detail, bundleOf(
                        "TripID" to t.id,
                        "otherUserID" to t.otherUserID,
                        "check" to '0',
                    )
                )
            } */
            cardView.setOnClickListener {
                navController.navigate(
                    R.id.action_nav_trip_list_others_to_nav_trip_detail, bundleOf(
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



    override fun onViewRecycled(holder: TripAdapterOthers.TripViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layout = layoutInflater.inflate(R.layout.list_item, parent, false)
        return TripAdapterOthers.TripViewHolder(layout)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        //display the list
        holder.bind(data[position], navController)
    }

    override fun getItemCount(): Int {
        //get number of cardview of our list
        return data.size
    }

}