package com.example.drawer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlin.properties.Delegates

lateinit var TripID: String

class RequestsFragment : Fragment(R.layout.fragment_requests) {
    lateinit var requestsUserID: String
    //lateinit var imageURL: String
    //lateinit var userNickname: String
    var TripIsPast by Delegates.notNull<Boolean>()
    //lateinit var TripID: String

    val TAG = "RequestsFragment"
    //var listEmptyOrNull = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            //when i go back with the button from trip detail i go back to the trip list
            findNavController().navigate(R.id.action_requestsFragment_to_nav_trip_detail, bundleOf("TripID" to TripID))
        }
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        TripID = arguments?.getString("TripID").toString()
        TripIsPast = arguments?.getBoolean("tripIsPast")!!

        //create a snackbar instance
        val db = FirebaseFirestore.getInstance()//create firestore instance
        //get all the trips created by the user
        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewReq)
        val data = mutableListOf<ListUser>()
        //val account = GoogleSignIn.getLastSignedInAccount(requireContext())//get signed in account
        val noReqFoundTV = view.findViewById<TextView>(R.id.noReqFound)
        //list all the requests on the list


        db.collection("trips").whereEqualTo("tripID", TripID).get().addOnSuccessListener() { documents ->
            var found = false
            if (documents?.isEmpty == false) {
                for (thisReq in documents) {

                    if (thisReq.contains("requestUsersID")) {
                        val reqUserID = thisReq["requestUsersID"] as List<String>

                        for (user in reqUserID) {
                            found = true
                            db.collection("users").document(user).get()
                                .addOnCompleteListener() { task ->
                                    if (task.isSuccessful) {

                                        val foundUser = task.result
                                        found = true
                                        if (foundUser!!.exists()) {
                                            data.add(
                                                RequestingUser(
                                                    requireContext(),
                                                    user,
                                                    foundUser.getString("imgPath").toString(),
                                                    foundUser.getString("fullName").toString(),
                                                    isTripPast = false,
                                                    false,
                                                    TripID
                                                )
                                            )
                                        }
                                    }
                                    data.sortBy { it.sortingVal }
                                    // Problem.. We will initialize it after every user in the list
                                    rv.layoutManager = LinearLayoutManager(rv.context)
                                    rv.adapter = RequestUserAdapter(TripIsPast, data, findNavController())
                                }
                        }

                    }

                    if (thisReq.contains("acceptUsersID")) {
                        val accUserID = thisReq["acceptUsersID"] as List<String>
                        val users_rated = thisReq["driverRateUsers"] as Map<String, Int>
                        for (user in accUserID) {
                            found = true
                            var rated = false
                            if(users_rated.containsKey(user)){
                                rated = true
                            }


                            db.collection("users").document(user).get()
                                .addOnCompleteListener() { task ->
                                    if (task.isSuccessful) {
                                        val foundUser = task.result
                                        if (foundUser!!.exists()) {
                                            data.add(
                                                AcceptedUser(
                                                    requireContext(),
                                                    user,
                                                    foundUser.getString("imgPath").toString(),
                                                    foundUser.getString("fullName").toString(),
                                                    isTripPast = TripIsPast,
                                                    rated,
                                                    TripID
                                                )
                                            )
                                        }
                                    }
                                    data.sortBy { it.sortingVal } // We want the requests on top and the already accepted users at the bottom of the view
                                    // Problem.. We will initialize it after every user in the list
                                    rv.layoutManager = LinearLayoutManager(rv.context)
                                    rv.adapter = RequestUserAdapter(TripIsPast, data, findNavController())
                                }
                        }

                    }

                    if (!found) {
                        noReqFoundTV.visibility=View.VISIBLE
                    }
                }
            } else {
                //show the snack to see if no trips were found
                noReqFoundTV.visibility=View.VISIBLE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == 16908332){
            findNavController().navigate(R.id.action_requestsFragment_to_nav_trip_detail, bundleOf("TripID" to TripID))
        }
        return true
    }
}

abstract class ListUser(val context: Context, val userID: String, val profileImageURI: String, val fullname: String, val isTripPast: Boolean, val isRated: Boolean, val tripID: String) {
    abstract val sortingVal: Int
}

class RequestingUser(context: Context, userID: String, profileImageURI: String, fullname: String, isTripPast: Boolean,isRated: Boolean, tripID: String) : ListUser(context, userID, profileImageURI, fullname, isTripPast,isRated, tripID) {
    override val sortingVal = 0 // needed to sort the data list. We want to display first the requests, then the accepted users
}

class AcceptedUser(context: Context, userID: String, profileImageURI: String, fullname: String, isTripPast: Boolean,isRated: Boolean, tripID: String) : ListUser(context, userID, profileImageURI, fullname, isTripPast,isRated, tripID) {
    override val sortingVal = 1
}


class RequestUserAdapter(val isTripPast: Boolean, val data: List<ListUser>, val navController: NavController) : RecyclerView.Adapter<RequestUserAdapter.RequestUserViewHolder>() {

    class RequestUserViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val profileImage = v.findViewById<ImageView>(R.id.ProfileImage)
        val fullname = v.findViewById<TextView>(R.id.ProfileName)
        val cardViewReq = v.findViewById<CardView>(R.id.cardViewReq)
        val acceptButton = v.findViewById<ImageButton>(R.id.accept)
        val refuseButton = v.findViewById<ImageButton>(R.id.refuse)
        val clickToRateTv = v.findViewById<TextView>(R.id.ClickToRate)

        fun bind(r: ListUser, navController: NavController) {
            if ((r.profileImageURI != "null") && (r.profileImageURI != "default")) {//check if the photo tag is the default one
                val storage = FirebaseStorage.getInstance()//get instance of the storage
                val storageRef = storage.reference //create storage reference
                val imageRef = storageRef.child(r.profileImageURI)//get storage ref to image

                imageRef.downloadUrl.addOnSuccessListener { Uri ->//download url
                    val profileImageURL = Uri.toString()
                    Glide.with(r.context).load(profileImageURL).into(profileImage)
                    profileImage.tag = r.profileImageURI
                }
            } else {
                profileImage.load(R.drawable.ic_user1)//load default image
            }
            fullname.text = r.fullname
            if (r.isTripPast) {
                clickToRateTv.visibility = View.VISIBLE
            }
            if(r.isRated){
                clickToRateTv.visibility = View.VISIBLE
                clickToRateTv.text = "Already rated"
            }
            cardViewReq.setOnClickListener {
                navController.navigate(R.id.action_requestsFragment_to_nav_show_profile, bundleOf("userID" to r.userID, "ratePassenger" to !r.isRated, "tripID" to r.tripID))
            }
            if (r is RequestingUser) {
                val db = FirebaseFirestore.getInstance()
                val tripRef = db.collection("trips").document(TripID)
                acceptButton.setOnClickListener {
                    // The owner of the trip accepts the user. Move it from requestUsersID to acceptUsersID
                    // Here we should notify the other user that his request was accepted
                    tripRef.update("requestUsersID", FieldValue.arrayRemove(r.userID))
                    tripRef.update("acceptUsersID", FieldValue.arrayUnion(r.userID))
                    tripRef.update("avaSeats", FieldValue.increment(-1))
                    refuseButton.visibility = View.INVISIBLE
                    refuseButton.isClickable = false
                    acceptButton.isClickable = false
                    cardViewReq.setBackgroundResource(R.color.green)

                    // Notify the other requesting users that the trip is full! Also remove all pending requests
                    tripRef.get().addOnSuccessListener {
                        if (it["avaSeats"] == 0L) {
                            // Here we should notify the requsting users that the trip is full and their request was automatically rejected
                            Snackbar.make(itemView, "The trip is now full!", Snackbar.LENGTH_SHORT).show()
                            tripRef.update("requestUsersID", FieldValue.delete()) // remove the entire list of requests. So that the other users cannot send requets anymore and the owner cannot accept anymore
                            navController.navigate(R.id.action_requestsFragment_to_nav_trip_detail, bundleOf("TripID" to TripID))
                        }
                    }

                }
                refuseButton.setOnClickListener {
                    // Remove this user from the request list
                    // Here we should notify the other user that his request was refused
                    tripRef.update("requestUsersID", FieldValue.arrayRemove(r.userID))
                    acceptButton.visibility = View.INVISIBLE
                    refuseButton.isClickable = false
                    acceptButton.isClickable = false
                    cardViewReq.setBackgroundResource(R.color.red)

                }
            }
        }

        fun unbind() {

            cardViewReq.setOnClickListener { null }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestUserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layout = if (viewType == R.layout.request_profile_item) {
            layoutInflater.inflate(R.layout.request_profile_item, parent, false)
        } else {
            layoutInflater.inflate(R.layout.accept_profile_item, parent, false)
        }
        return RequestUserAdapter.RequestUserViewHolder(layout)
    }


    override fun getItemCount(): Int {
        return data.size
    }


    override fun onViewRecycled(holder: RequestUserViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    override fun onBindViewHolder(holder: RequestUserViewHolder, position: Int) {
        holder.bind(data[position], navController)
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position] is AcceptedUser) {
            R.layout.accept_profile_item
        } else {
            R.layout.request_profile_item
        }
    }
}

