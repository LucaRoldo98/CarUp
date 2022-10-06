package com.example.drawer

import SharedViewModel
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp.now
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import okhttp3.internal.userAgent
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import java.text.SimpleDateFormat
import java.util.*

class TripDetailsFragment : Fragment(R.layout.fragment_trip_details) {

    lateinit var TripID: String
    lateinit var userID: String
    val TAG = "TripDetailsFragment"
    lateinit var imageURL: String
    lateinit var startMarker: Marker
    lateinit var endMarker: Marker

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        setHasOptionsMenu(true)
        //when I press the back button
        /*val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if(arguments?.get("check") != '0') {
                //when i go back with the button from trip detail i go back to the trip list
                findNavController().navigate(R.id.action_nav_trip_detail_to_nav_trip_list)
            } else {
                //when i go back with the button from trip detail i go back to the trip list others
                findNavController().navigate(R.id.action_nav_trip_detail_to_nav_trip_list_others)
            }
        }*/


        return inflater.inflate(R.layout.fragment_trip_details, container, false)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
        return
    }


    /*****SE IL TRIP DETAILS RICEVE check=0 (OtherTripLists) TRAMITE BUNDLE, LA PENNA E IL CESTINO VENGONO NASCOSTI******/
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(arguments?.get("check") == '0')
        {
            menu.clear()

        }
    }
    /******************************************************************************************************/


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //get views
        val tvFullName = view.findViewById<TextView>(R.id.full_name)
        val imgProfile = view.findViewById<ImageView>(R.id.profile_imageView)

        var requested = false// variable to set if the user already requested this trip (created by another user). Set to "1" if true
        var accepted= false // same as above for accepted users


        val account = GoogleSignIn.getLastSignedInAccount(requireContext())//get signed in account
        userID = account?.id.toString()
        imageURL = account?.photoUrl.toString()
        val storage = FirebaseStorage.getInstance()//get instance of the storage
        val storageRef = storage.reference //create storage reference
        val db = FirebaseFirestore.getInstance()//create firestore instance

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        //create map
        val map = view.findViewById<MapView>(R.id.mapView)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.isClickable = true
        // This map should not be zoomed in!
        // Remove the zoom controller
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        map.setMultiTouchControls(false)

        //initializing markers
        startMarker = Marker(map)
        endMarker = Marker(map)
        //create roadManager
        val roadManager: RoadManager = OSRMRoadManager(requireContext(), userAgent)
        var roadOverlay: Polyline? = null
        //get viewmodel instance
        val model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        model.startEndPoints.observe(viewLifecycleOwner) {
            val (s1, e1) = it
            val waypoints = ArrayList<GeoPoint>()
            waypoints.add(s1)
            waypoints.add(e1)
            val road = roadManager.getRoad(waypoints)
            if (roadOverlay != null) map.overlays.removeAll(listOf(roadOverlay))
            roadOverlay = RoadManager.buildRoadOverlay(road)
            //var overlays = map.overlays
            map.overlays.add(roadOverlay)
            startMarker.position = s1
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            startMarker.icon = resources.getDrawable(R.drawable.ic_baseline_pick_up_location_on_24)
            startMarker.title = "Pick-up point"
            map.overlays.add(startMarker)
            //get coordinate and create marker of endpoint
            endMarker.position = e1
            endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            endMarker.icon = resources.getDrawable(R.drawable.ic_baseline_drop_location_on_24)
            endMarker.title = "Drop point"
            map.overlays.add(endMarker)
            //creating bounding box around start location and end location
            val boundingBox: BoundingBox =
                BoundingBox.fromGeoPoints(listOf(s1, e1)).increaseByScale(1.4f)
            map.zoomToBoundingBox(boundingBox, false)
            map.setScrollableAreaLimitDouble(boundingBox)
            // Set min and max zoom to the current zoom, so that the double tap doesn't do anything
            if (map.zoomLevel!=0) {
            map.maxZoomLevel=map.zoomLevelDouble
            map.minZoomLevel=map.zoomLevelDouble }
            map.invalidate()
        }


        //map click event
        map.overlays.add(object : Overlay() {
            override fun onSingleTapConfirmed(
                e: MotionEvent,
                mapView: MapView
            ): Boolean {
                // Set the zoom level for the dialog fragment (zoomToBoundingBox doesn't work in the DialogFragment...)
                MapDetailsFragment.zoom = map.zoomLevel
                MapDetailsFragment().show(childFragmentManager, MapEditFragment.TAG)
                return true
            }
        })


        /*GET USER DATA: NAME AND PICTURE*/
        if(arguments?.get("check") != '0') {
            //QUESTA PARTE DI IF E' DEDICATA ALLA CHIAMATA DA PARTE DI TRIP LIST
            val userRef = db.collection("users").document(userID)//get user data from firestore
            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document!!.exists()) {
                        //set the data of the user in the various views
                        tvFullName.text = document.get("fullName").toString()

                        if (document["imgPath"].toString() == "default") {//if the field of the url is the default
                            //imageURL = account?.photoUrl.toString()
                            // i load the google profile picture
                            Glide.with(this).load(resources.getDrawable(R.drawable.ic_user1)).into(imgProfile)
                        } else {
                            val imageRef =
                                storageRef.child(document["imgPath"].toString())//otherwise i take the one specified in the field
                            imageRef.downloadUrl.addOnSuccessListener { Uri ->
                                imageURL = Uri.toString()
                                Glide.with(this).load(imageURL).into(imgProfile)
                            }
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Unable to get user data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.exception)
                    Toast.makeText(requireContext(), "Unable to get user data", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }else {
            //QUESTA PARTE DI IF E' DEDICATA ALLA CHIAMATA DA PARTE DI TRIP LIST OTHERS
            /**************PRENDERE LA FOTO E IL NOME DELL'UTENTE DEL TRIP CORRENTE FACENDOLA PASSARE DAL BUNDLE DI OTHER TRIPS E IMPOSTANDOLA************/
            /*db.collection("trips").document(arguments?.get("TripID").toString()).get().addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    val document = task.result
                    otherUserID = document?.get("userID").toString()
                }
            }*/

            val userRef = db.collection("users")
                .document(arguments?.get("otherUserID").toString())//get other user data from firestore

            // Add the feature of going to the user profile from the trip details by clicking the user profile pic
            imgProfile.isClickable=true
            imgProfile.setOnClickListener {
                findNavController().navigate(R.id.action_nav_trip_detail_to_nav_show_profile, bundleOf("userID" to arguments?.getString("otherUserID")))
            }
            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document!!.exists()) {
                        //set the data of the user in the various views
                        tvFullName.text = document.get("fullName").toString()

                        if (document["imgPath"].toString() == "default") {//if the field of the url is the default
                            // We should load the image of another google account
                            /* imageURL =
                                    account?.photoUrl.toString()//i load the google profile picture
                                Glide.with(this).load(imageURL).into(imgProfile) */
                                    // For now we load the default user icon, not the google photo
                            imgProfile.setImageResource(R.drawable.ic_user1)
                        } else {
                            val imageRef =
                                storageRef.child(document["imgPath"].toString())//otherwise i take the one specified in the field
                            imageRef.downloadUrl.addOnSuccessListener { Uri ->
                                imageURL = Uri.toString()
                                Glide.with(this).load(imageURL).into(imgProfile)
                            }
                        }
                    }
                }
            }
        }

        //get views
        val pickUpLocation = view.findViewById<TextView>(R.id.textViewPickupLocation)
        val dropLocation = view.findViewById<TextView>(R.id.textViewDropLocation)
        val price = view.findViewById<TextView>(R.id.Price)
        val avaSeats = view.findViewById<TextView>(R.id.textViewAvailableSeats)
        val addInfo = view.findViewById<TextView>(R.id.AddInfo)
        val data = view.findViewById<TextView>(R.id.tvDateField)
        val pickUpHour = view.findViewById<TextView>(R.id.textViewDepartureHour)
        val dropHour = view.findViewById<TextView>(R.id.textViewArrivalHour)
        val carPhoto = view.findViewById<ImageView>(R.id.imageViewProfileImage)
        val estimateDurTv = view.findViewById<TextView>(R.id.estimatedDuration)
        /*SET DATA INTO VIEWS*/
        if (arguments?.containsKey("TripID") == true) {//if the trip id has been passed
            TripID = arguments?.getString("TripID").toString()//get the trip id

            val tripRef = db.collection("trips").document(TripID)//db instance of the document
            tripRef.get().addOnSuccessListener { thisTrip ->

                model.setAddresses(thisTrip["pickUpLocation"].toString(), thisTrip["dropLocation"].toString())
                pickUpLocation.text = thisTrip["pickUpLocation"].toString()
                dropLocation.text = thisTrip["dropLocation"].toString()
                price.text = thisTrip["price"].toString()
                avaSeats.text = thisTrip["avaSeats"].toString()
                addInfo.text = thisTrip["addInfo"].toString()
                data.text = thisTrip["data"].toString()
                pickUpHour.text = thisTrip["pickUpHour"].toString()
                dropHour.text = thisTrip["dropHour"].toString()

                if (thisTrip.contains("longitudePickUp") && thisTrip.contains("longitudeDrop")) {
                    //get coordinates and create marker of startpoint
                    val startPoint = org.osmdroid.util.GeoPoint(thisTrip["latitudePickUp"].toString().toDouble(), thisTrip["longitudePickUp"].toString().toDouble())
                    //get coordinate and create marker of endpoint
                    val endPoint = org.osmdroid.util.GeoPoint(thisTrip["latitudeDrop"].toString().toDouble(), thisTrip["longitudeDrop"].toString().toDouble())
                    //update live data for street drawing
                    model.setPoints(startPoint, endPoint)


                } else {
                    //if the coordinates are not on firebase we zoom on Turin
                    val point = org.osmdroid.util.GeoPoint(45.078, 7.676)
                    map.controller.setZoom(9.5)
                    map.controller.setCenter(point)
                    map.invalidate()
                }


                // Check if the user already requested this trip or it was already accepted
                if (thisTrip.contains("requestUsersID")) {
                if (userID in thisTrip["requestUsersID"] as List<String>) {
                    requested = true
                    }
                }
                if (thisTrip.contains("acceptUsersID")) {
                    if (userID in thisTrip["acceptUsersID"] as List<String>) {
                        accepted = true
                    }
                }

                //Load the car photo in imageview
                if (thisTrip["carPhotoPath"].toString() == "default") {//if the field of the url is the default
                    carPhoto.tag = "default"
                    carPhoto.load(R.drawable.purple_muscle)
                } else {
                    val imageRef = storageRef.child(thisTrip["carPhotoPath"].toString())//otherwise i take he one specified in the field
                    imageRef.downloadUrl.addOnSuccessListener { Uri ->
                        val carImageURL = Uri.toString()
                        Glide.with(this).load(carImageURL).into(carPhoto)
                        carPhoto.tag = thisTrip["carPhotoPath"].toString()
                    }
                }

                // I move this code here because it was running asynchronously. We need syncrhonization to set the buttons correctly

                //if I enter TripDetails from OthersTripList (check=0), I see the request button for the trip
                //otherwise I see the button to see the requests for my trip
                val button = view.findViewById<Button>(R.id.button)
                if(arguments?.get("check") == '0') {
                    // If the user already requested the trip
                    if (requested) {
                        button.isClickable=false
                        button.text = "✓\t\tRequested"
                        //button.setBackgroundColor(resources.getColor(R.color.ic_user_yellow_background))
                    }
                    // If the user requested the trip and was accepeted
                    else if (accepted) {
                        button.isClickable=false
                        button.text = "✓\t\tAccepted"
                        button.setBackgroundColor(resources.getColor(R.color.green))
                    }
                    else if(thisTrip["avaSeats"] == 0L) { // The trip is full
                        button.isClickable=false
                        button.text = "This trip is full"
                        button.setBackgroundColor(Color.GRAY)
                    }
                    else {
                        button.setOnClickListener {
                            db.collection("trips")
                                .document(TripID)
                                .update("requestUsersID", (FieldValue.arrayUnion(userID)))
                            db.collection("trips")
                                .document(TripID)
                                .update("dateLastModified", now())//db instance of the document
                            // Now it behaves as "requested"
                            button.isClickable=false
                            button.text = "Requested\t\u2713"
                            //button.setBackgroundColor(resources.getColor(R.color.ic_user_yellow_background))
                        }
                    }
                } else{
                    button.text = "View requests"

                    val tripTimestamp = SimpleDateFormat("dd/MM/yyyy hh:mm").parse("${thisTrip["data"]} ${thisTrip["dropHour"]}").time
                    val timeStampNow = System.currentTimeMillis()
                    var tripIsPast = false
                    //check if the trip is past or not
                    if(tripTimestamp < timeStampNow) tripIsPast = true
                    button.setOnClickListener {
                        findNavController().navigate(R.id.action_nav_trip_detail_to_requestsFragment, bundleOf("TripID" to TripID, "tripIsPast" to tripIsPast))
                    }
                }
            }

            //manca gestione foto!
            tripRef.addSnapshotListener{ value, error->
                if (error != null) throw error
                if (value!=null) {

                    if (value["carPhotoPath"].toString() == "default") {//if the field of the url is the default
                        carPhoto.tag = "default"
                        carPhoto.load(R.drawable.purple_muscle)
                    } else {
                        val imageRef =
                            storageRef.child(value["carPhotoPath"].toString())//otherwise i take he one specified in the field
                        imageRef.downloadUrl.addOnSuccessListener { Uri ->
                            val carImageURL = Uri.toString()
                            if (activity!=null) {
                                Glide.with(this).load(carImageURL).into(carPhoto)
                                carPhoto.tag = value["carPhotoPath"].toString() }
                        }
                    }

                    avaSeats.text=value["avaSeats"].toString()
                    addInfo.text=value["addInfo"].toString()
                    data.text=value["data"].toString()
                    dropHour.text=value["dropHour"].toString()
                    dropLocation.text=value["dropLocation"].toString()
                    addInfo.text=value["addInfo"].toString()
                    estimateDurTv.text=value["estDuration"].toString()
                    pickUpHour.text=value["pickUpHour"].toString()
                    pickUpLocation.text=value["pickUpLocation"].toString()
                    price.text=value["price"].toString()
                }
            }

            /*val fabTripReq: View = view.findViewById(R.id.fab
            TripReq)
            fabTripReq.setOnClickListener { view ->
                Snackbar.make(view, "Are you sure you want to book this trip?", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show()
            }*/



            /*val sharedPref = this.activity?.getPreferences(Context.MODE_PRIVATE)
            val jsonTripList = JSONObject(sharedPref?.getString("tripList", "null").toString())
            TripID = requireArguments().getString("TripID").toString()
            val thisTrip = JSONObject(jsonTripList[TripID].toString())
            pickUpLocation.text = thisTrip["pickUpLocation"].toString()
            dropLocation.text = thisTrip["dropLocation"].toString()
            price.text = thisTrip["price"].toString()
            avaSeats.text = thisTrip["avaSeats"].toString()
            addInfo.text = thisTrip["addInfo"].toString()
            data.text= thisTrip["data"].toString()
            pickUpHour.text= thisTrip["pickUpHour"].toString()
            dropHour.text= thisTrip["dropHour"].toString()
            estimateDurTv.text = thisTrip["estDuration"].toString()
            if (thisTrip["carPhotoPath"]!="null") carPhoto.load(File(thisTrip["carPhotoPath"].toString()))*/
        }

    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var delete = 0 //variable of the process of delete
        if (item.itemId == R.id.delete)//delete the trip from your list
        {
            val storage = FirebaseStorage.getInstance()//get instance of the storage
            val storageRef = storage.reference //create storage reference
            val db = FirebaseFirestore.getInstance()//create firestore instance

            MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.title_delete))

                    .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->

                    }
                    .setPositiveButton(resources.getString(R.string.delete_sure)) { dialog, which ->
                        //delete the document with the id of the trip
                        db.collection("trips").document(TripID).delete().addOnSuccessListener {
                            delete++
                            if (delete == 2) findNavController().navigate(R.id.action_nav_trip_detail_to_nav_trip_list)

                            Log.d(TAG, "DocumentSnapshot successfully deleted!")
                        }.addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

                        val carPhotoRef = storageRef.child("carImages/ $TripID _car.png")
                        carPhotoRef.delete().addOnSuccessListener {
                            delete++
                            if (delete == 2) findNavController().navigate(R.id.action_nav_trip_detail_to_nav_trip_list)
                        }.addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                        /*val sharedPref = this.activity?.getPreferences(Context.MODE_PRIVATE)//get shared preferences
                        val jsonTripList = JSONObject(sharedPref?.getString("tripList", "null").toString())//get the trip list
                        jsonTripList.remove(TripID)//remove the trp with the selected trip id
                        //save modifications
                        with(sharedPref?.edit()) {
                            this?.putString("tripList", jsonTripList.toString())
                            this?.apply()
                        }*/
                        findNavController().navigate(R.id.action_nav_trip_detail_to_nav_trip_list)
                    }
                    .show()


        } else if (item.itemId == R.id.edit)//edit the trip in the list
        {
            TripID = arguments?.getString("TripID").toString()
            findNavController().navigate(R.id.action_nav_trip_detail_to_nav_trip_edit, bundleOf("TripID" to TripID))

        }
        return super.onOptionsItemSelected(item)
    }

}

