package com.example.drawer

import SharedViewModel
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import kotlinx.coroutines.*
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
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil


const val TAKE_CAR_PICTURE = 2
const val PICK_CAR_IMAGE = 3
const val REQUEST_PERMISSIONS_REQUEST_CODE = 10

var photoTakenUrl: Uri? = null

var job: Job? = null

class TripEditFragment : Fragment(R.layout.fragment_trip_edit), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    lateinit var id: String
    lateinit var userID: String
    lateinit var pickUpHour: EditText
    lateinit var dropHour: EditText
    //lateinit var pickUpLocation: EditText
    val TAG = "TripEditFragment"
    lateinit var imageURL: String
    lateinit var startMarker: Marker
    lateinit var endMarker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /********************************OPTION MENU*************************/
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_trip_edit, container, false)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var s: GeoPoint?
        var e: GeoPoint?


        //GET VIEWS
        val tvFullName = view.findViewById<TextView>(R.id.full_name)
        val imgProfile = view.findViewById<ImageView>(R.id.profile_imageView)
        //val sharedPref = this.activity?.getPreferences(Context.MODE_PRIVATE)
        val pickUpLocation = view.findViewById<EditText>(R.id.textViewPickupLocation)
        val dropLocation = view.findViewById<EditText>(R.id.textViewDropLocation)
        val price = view.findViewById<EditText>(R.id.Price)
        val avaSeats = view.findViewById<EditText>(R.id.textViewAvailableSeats)
        val addInfo = view.findViewById<EditText>(R.id.AddInfo)
        val data = view.findViewById<EditText>(R.id.tvDateField)
        pickUpHour = view.findViewById(R.id.textViewDepartureHour)
        dropHour = view.findViewById(R.id.textViewArrivalHour)
        val carPhoto = view.findViewById<ImageView>(R.id.imageViewProfileImage)

        //getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))

        //create roadManager
        val roadManager: RoadManager = OSRMRoadManager(requireContext(), userAgent)
        var roadOverlay: Polyline? = null

        //create map
        val map = view.findViewById<MapView>(R.id.mapView)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.isClickable = true
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER) // This map should not be zoomed in!
        map.minZoomLevel = 5.0

        map.overlays.add(object : Overlay() {
            override fun onSingleTapConfirmed(
                e: MotionEvent,
                mapView: MapView
            ): Boolean {
                MapDetailsFragment.zoom = map.zoomLevel
                MapDetailsFragment().show(childFragmentManager, MapEditFragment.TAG)
                return true
            }
        }
        )
        //observable of livedata for changing startPoint and endpoint
        val model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        model.startEndPoints.observe(viewLifecycleOwner) {
            val (s1, e1) = it
            s = s1
            e = e1
            val waypoints = ArrayList<GeoPoint>()
            waypoints.add(s1)
            waypoints.add(e1)
            val road = roadManager.getRoad(waypoints)
            if (roadOverlay != null) map.overlays.removeAll(listOf(roadOverlay))
            roadOverlay = RoadManager.buildRoadOverlay(road)
            //var overlays = map.overlays
            map.overlays.add(roadOverlay)
            startMarker.position = s
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            startMarker.icon = resources.getDrawable(R.drawable.ic_baseline_pick_up_location_on_24)
            startMarker.title = "Pick-up point"
            map.overlays.add(startMarker)
            //get coordinate and create marker of endpoint
            endMarker.position = e
            endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            endMarker.icon = resources.getDrawable(R.drawable.ic_baseline_drop_location_on_24)
            endMarker.title = "Drop point"
            map.overlays.add(endMarker)

            if (s1!=e1) {
                //creating bounding box around start location and end location
                val boundingBox: BoundingBox =
                    BoundingBox.fromGeoPoints(listOf(s1, e1)).increaseByScale(1.4f)
                // Remove the min and max zoom limits
                map.maxZoomLevel=18.0
                map.minZoomLevel=0.0
                map.zoomToBoundingBox(boundingBox, false)
                // Reset min and max zoom to the current zoom, so that the double tap doesn't do anything
                if (map.zoomLevel!=0) {
                    map.maxZoomLevel=map.zoomLevelDouble
                    map.minZoomLevel=map.zoomLevelDouble }
                //Disable scrolling
                map.setScrollableAreaLimitDouble(boundingBox)
                // Set min and max zoom to the current zoom, so that the double tap doesn't do anything
                map.invalidate() }
            else { // else zoom to the point. This because bounding box gave problems when the s1 and e1 were the same
                map.controller.setCenter(s1)
                map.controller.setZoom(7.0)
                // Remove the min and max zoom limits
                map.maxZoomLevel=18.0
                map.minZoomLevel=0.0
                // Set min and max zoom to the current zoom, so that the double tap doesn't do anything
                if (map.zoomLevel!=0) {
                    map.maxZoomLevel=map.zoomLevelDouble
                    map.minZoomLevel=map.zoomLevelDouble }
                // Set min and max zoom to the current zoom, so that the double tap doesn't do anything
                map.invalidate()
            }
        }

        model.startEndAddress.observe(viewLifecycleOwner) {
            val (s, e) = it
            pickUpLocation.setText(s)
            dropLocation.setText(e)
        }

        val items = ArrayList<OverlayItem>()
        items.add(OverlayItem("Title", "Description", GeoPoint(0.0, 0.0)))

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        startMarker = Marker(map)
        endMarker = Marker(map)


        val account = GoogleSignIn.getLastSignedInAccount(requireContext())//get signed in account
        userID = account?.id.toString()
        imageURL = account?.photoUrl.toString()
        val storage = FirebaseStorage.getInstance()//get instance of the storage
        val storageRef = storage.reference //create storage reference
        val db = FirebaseFirestore.getInstance()//create firestore instance
        /*GET USER DATA: NAME AND PICTURE*/
        val userRef = db.collection("users").document(userID)//get user data from firestore
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    //set the data of the user in the various views
                    tvFullName.text = document.get("fullName").toString()
                    if (document["imgPath"].toString() == "default") {//if the field of the url is the default
                        //imageURL = account?.photoUrl.toString()
                        //i load the default picture
                        Glide.with(this).load(resources.getDrawable(R.drawable.ic_user1)).into(imgProfile)
                    } else {
                        val imageRef =
                            storageRef.child(document["imgPath"].toString())//otherwise i take he one specified in the field
                        imageRef.downloadUrl.addOnSuccessListener { Uri ->
                            imageURL = Uri.toString()
                            Glide.with(this).load(imageURL).into(imgProfile)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Unable to get user data", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d(TAG, "Failed with: ", task.exception)
                Toast.makeText(requireContext(), "Unable to get user data", Toast.LENGTH_SHORT).show()
            }
        }


        //click event on edit texts for selecting position on the map
        pickUpLocation.setOnClickListener {
            MapEditFragment.selectedPar = "start"
            MapEditFragment().show(childFragmentManager, MapEditFragment.TAG)
        }
        dropLocation.setOnClickListener {
            MapEditFragment.selectedPar = "end"
            MapEditFragment().show(childFragmentManager, MapEditFragment.TAG)
        }


        /*CHECK IF WE ARE IN CREATE OR EDIT MODE*/
        var listOfTripsID: List<String> = emptyList()
        //get all the trips
        val collRef = db.collection("trips").get().addOnSuccessListener { documents ->
            for (document in documents) {
                listOfTripsID += (document.id)
            }
            var editing = false//edit flag
            if (arguments?.containsKey("TripID") == true) {//if arguments contains TripID
                if (arguments?.getString("TripID") != "null") {//if tripID is not null
                    editing = true//we are in edit mode
                    id = arguments?.getString("TripID").toString()
                } else {//if ti is null, we are not in edit mode
                    id = UUID.randomUUID().toString()//I generate a new trip id
                    while (listOfTripsID.contains(id)) {//check if the id already exist
                        id = UUID.randomUUID().toString()
                    }
                }
            } else {//if ti is null, we are not in edit mode
                id = UUID.randomUUID().toString()
                while (listOfTripsID.contains(id)) {//check if the id already exist
                    id = UUID.randomUUID().toString()
                }
            }
            //if we are in edit mode and we have the ID I load all the data in the views
            if (editing && listOfTripsID.contains(id)) {
                val tripRef = db.collection("trips").document(id)
                tripRef.get().addOnSuccessListener { thisTrip ->
                    //pickUpLocation.setText(thisTrip["pickUpLocation"].toString())
                    model.setAddresses(thisTrip["pickUpLocation"].toString(), thisTrip["dropLocation"].toString())
                    //dropLocation.setText(thisTrip["dropLocation"].toString())
                    price.setText(thisTrip["price"].toString())
                    avaSeats.setText(thisTrip["avaSeats"].toString())
                    addInfo.setText(thisTrip["addInfo"].toString())
                    data.setText(thisTrip["data"].toString())
                    pickUpHour.setText(thisTrip["pickUpHour"].toString())
                    dropHour.setText(thisTrip["dropHour"].toString())

                    if (thisTrip.contains("longitudePickUp") && thisTrip.contains("longitudeDrop")) {
                        //get coordinates and create marker of startpoint
                        val startPoint = GeoPoint(thisTrip["latitudePickUp"].toString().toDouble(), thisTrip["longitudePickUp"].toString().toDouble())
                        startMarker.position.longitude = thisTrip["longitudePickUp"].toString().toDouble()
                        startMarker.position.latitude = thisTrip["latitudePickUp"].toString().toDouble()
                        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        startMarker.icon = resources.getDrawable(R.drawable.ic_baseline_pick_up_location_on_24)
                        startMarker.title = "Pick-up point"
                        map.overlays.add(startMarker)
                        //get coordinate and create marker of endpoint
                        val endPoint = GeoPoint(thisTrip["latitudeDrop"].toString().toDouble(), thisTrip["longitudeDrop"].toString().toDouble())
                        endMarker.position.longitude = thisTrip["longitudeDrop"].toString().toDouble()
                        endMarker.position.latitude = thisTrip["latitudeDrop"].toString().toDouble()
                        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        endMarker.icon = resources.getDrawable(R.drawable.ic_baseline_drop_location_on_24)
                        endMarker.title = "Drop point"
                        map.overlays.add(endMarker)
                        //update live data for street drawing
                        model.setPoints(startPoint, endPoint)


                    } else {
                        //if the coordinates are not on firebase we zoom on Turin
                        val point = GeoPoint(45.078, 7.676)
                        map.controller.setZoom(9.5)
                        map.controller.setCenter(point)
                        map.invalidate()
                    }

                    //Load the car photo in imageview
                    if (photoTakenUrl != null) {
                        carPhoto.load(photoTakenUrl)
                        carPhoto.tag = photoTakenUrl
                    } else {
                        if (thisTrip["carPhotoPath"].toString() == "default") {//if the field of the url is the default
                            /*var carImageURL = account?.photoUrl.toString()//i load the google profile picture
                    Glide.with(this).load(carImageURL).into(carPhoto)*/
                            carPhoto.tag = "default"
                            carPhoto.load(R.drawable.purple_muscle)
                        } else {
                            val imageRef =
                                storageRef.child(thisTrip["carPhotoPath"].toString())//otherwise i take the one specified in the field
                            imageRef.downloadUrl.addOnSuccessListener { Uri ->
                                val carImageURL = Uri.toString()
                                Glide.with(this).load(carImageURL).into(carPhoto)
                                carPhoto.tag = thisTrip["carPhotoPath"].toString()
                                //carPhoto.tag = Uri
                            }
                        }
                    }
                }
            }
            else {
                // A new trip is being created! Clean from every data picked from live data.
                model.setPoints(GeoPoint(45.05,7.67),GeoPoint(45.05, 7.67)) // Set the points to default
                model.setAddresses("","")
                map.overlayManager.removeAll(map.overlays) // Remove the two pins and route
                map.controller.setCenter(GeoPoint(45.05, 7.67)) // Torino caput mundi
                // We still want the map to be clickable
                map.overlays.add(object : Overlay() {
                    override fun onSingleTapConfirmed(
                        e: MotionEvent,
                        mapView: MapView
                    ): Boolean {
                        MapDetailsFragment.zoom = map.zoomLevel
                        MapDetailsFragment().show(childFragmentManager, MapEditFragment.TAG)
                        return true
                    }
                } )
                map.controller.setZoom(6.0)
                // We don't want it scrollable
                map.setScrollableAreaLimitDouble(map.boundingBox)
                map.invalidate()
                pickUpLocation.text = null
                dropLocation.text = null
            }
        }

        /*3*******************floatContextMenu**********************/
        val buttonCamera = view.findViewById<ImageButton>(R.id.photo_selector)
        buttonCamera.setOnClickListener {
            buttonCamera.performLongClick()
        }
        registerForContextMenu(buttonCamera)

        /**********************************************************/

        /*2******************************DATA PICKER******************************2*/

        val dataButton = view.findViewById<EditText>(R.id.tvDateField)

        dataButton.setOnClickListener {
            val c = Calendar.getInstance()
            val dataPicker = DatePickerDialog(it.context, { _, i, i2, i3 ->

                dataButton.setText("$i3/${i2 + 1}/$i")

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
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            photoTakenUrl = this.toUri()
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, TAKE_CAR_PICTURE)
                }
            }
        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(photoTakenUrl?.path.toString())
            mediaScanIntent.data = Uri.fromFile(f)
            requireActivity().sendBroadcast(mediaScanIntent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_CAR_PICTURE && resultCode == RESULT_OK) {
            // Low quality images using this code
            /*val imageBitmap = data!!.extras?.get("data") as Bitmap
            val imageView = view?.findViewById<ImageView>(R.id.imageViewProfileImage)
            val imgFile = File(
                context?.filesDir,
                "${id}_car.jpeg"
            ) // Store the image everytime it is returned from the activity
            val fo = FileOutputStream(imgFile)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fo)
            fo.flush()
            fo.close()
            val filepath = imgFile.absolutePath.toString()
            photoTakenUrl = Uri.fromFile(imgFile)
            imageView?.load(File(filepath))
            imageView?.tag = Uri.fromFile(imgFile)*/
            val imageView = view?.findViewById<ImageView>(R.id.imageViewProfileImage)
            galleryAddPic()
            imageView?.load(photoTakenUrl)
            // Compress using compression library
            val compressedImgFile = Compressor(requireContext()).compressToFile(photoTakenUrl?.toFile())
            val compressedUri = compressedImgFile.toUri()
            photoTakenUrl = compressedUri
            imageView?.tag = compressedUri


        } else if (requestCode == PICK_CAR_IMAGE && resultCode == RESULT_OK) {

            val fileURI = data!!.data
            val imageView = view?.findViewById<ImageView>(R.id.imageViewProfileImage)

            val orientationColumn = arrayOf(MediaStore.Images.Media.ORIENTATION)
            val cur: Cursor? =
                fileURI?.let {
                    requireActivity().contentResolver.query(
                        it,
                        orientationColumn,
                        null,
                        null,
                        null
                    )
                }
            var orientation = -1
            if (cur != null && cur.moveToFirst()) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
            }

            // retrieve the bitmap from the file
            val bitmap =
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, fileURI)
            // Check the orientation of the photo, in order to display it correctly
            val matrix = Matrix()
            matrix.postRotate(orientation.toFloat())
            // if the photo is not rotated, this line will simply apply a 0 degree rotation
            val rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            val imgFile = File(context?.filesDir, "${id}_car.png")
            val fo = FileOutputStream(imgFile)
            // quality depends on the size of the bitmap: if it's bigger than 1MB, lower the quality to 100 - sizeInMBofImage
            val MB = 1024 * 1024
            val quality = if (bitmap.byteCount > MB) {
                100 - Math.min(95, ceil((bitmap.byteCount.toFloat() / MB)).toInt())
            } else {
                100
            }
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fo)
            fo.flush()
            fo.close()
            // Compress further using the compression library. Size becomes 1/4 of the previous size!
            val compressedImgFile = Compressor(requireContext()).compressToFile(imgFile)
            val compressedUri = compressedImgFile.toUri()
            //val compressedUri = imgFile.toUri()
            photoTakenUrl = compressedUri
            imageView?.load(compressedUri)
            imageView?.tag = compressedUri
        }
    }


    /*3*************************************floatContexMenu************************************3*/
    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        this.activity?.menuInflater?.inflate(R.menu.floating_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionCamera -> {
                //val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    dispatchTakePictureIntent()
                    //startActivityForResult(takePictureIntent, TAKE_CAR_PICTURE)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this.context, "No camera found", Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.actionGallery -> {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(intent, PICK_CAR_IMAGE)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    /*3******************************************************************************************3*/

    /*2********************************######****************************************2*/
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

    }
    /*2******************************************************************************2*/

    /*4**************************************OPTION MENU************************************4*/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
        return
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        photoTakenUrl=null

        if (item.itemId == R.id.save) {

            val saving = Toast.makeText(requireContext(), "Saving...", Toast.LENGTH_LONG)
            saving.show()
            var success = 0 //variable to see if all steps are successful
            //Saving content of the views on the cloud
            val pickUpLocation = view?.findViewById<EditText>(R.id.textViewPickupLocation)
            val dropLocation = view?.findViewById<EditText>(R.id.textViewDropLocation)
            val price = view?.findViewById<EditText>(R.id.Price)
            val avaSeats = view?.findViewById<EditText>(R.id.textViewAvailableSeats)
            val addInfo = view?.findViewById<EditText>(R.id.AddInfo)
            val data = view?.findViewById<EditText>(R.id.tvDateField)
            val pickUpHour = view?.findViewById<EditText>(R.id.textViewDepartureHour)
            val dropHour = view?.findViewById<EditText>(R.id.textViewArrivalHour)
            val carPhoto = view?.findViewById<ImageView>(R.id.imageViewProfileImage)

            if (pickUpLocation?.text.toString().isEmpty() || dropLocation?.text.toString()
                    .isEmpty() ||
                price?.text.toString().isEmpty() || avaSeats?.text.toString().isEmpty() ||
                data?.text.toString().isEmpty() || pickUpHour?.text.toString().isEmpty() ||
                dropHour?.text.toString().isEmpty()
            ) {
                Toast.makeText(context, "Fill all the fields", Toast.LENGTH_SHORT).show()

            } else {
                val bundle = bundleOf(

                    "TripID" to id,
                    "pickUpLocation" to pickUpLocation?.text.toString()
                        .dropLastWhile { !it.isLetter() }, //remove the space after the name of the city
                    "dropLocation" to dropLocation?.text.toString()
                        .dropLastWhile { !it.isLetter() },
                    "price" to price?.text.toString().toInt(),
                    "avaSeats" to avaSeats?.text.toString().toInt(),
                    "addInfo" to addInfo?.text.toString(),
                    "data" to data?.text.toString(),
                    "pickUpHour" to pickUpHour?.text.toString(),
                    "dropHour" to dropHour?.text.toString(),
                    "carPhotoPath" to carPhoto?.tag.toString()
                )

                // Salvo i dati nella memoria locale: assegno un unique ID ad ogni bundle di informazioni, così che siano univocamente accessibili

                /*val sharedPref = this.activity?.getPreferences(Context.MODE_PRIVATE)
                val jsonTrip = JSONObject() */

                // Calculate the estimated duration
                val arrivalInMinutes =
                    dropHour?.text.toString().split(":")[0].toInt() * 60 + dropHour?.text.toString()
                        .split(":")[1].toInt()
                val departureMinutes = pickUpHour?.text.toString()
                    .split(":")[0].toInt() * 60 + pickUpHour?.text.toString().split(":")[1].toInt()
                var difference = arrivalInMinutes - departureMinutes
                if (difference < 0) difference += 24 * 60 // arrival is the next day
                val minutes = difference % 60
                val hours = difference / 60
                val estDuration =
                    if (hours > 0) "$hours h $minutes m" else "$minutes m"  //Better looking format
                /*SAVE THE CAR PICTURE*/
                val storage = FirebaseStorage.getInstance()//get instance of the storage
                //val storageRef = storage.reference //create storage reference
                var carPhotoPath = "default"
                // Case in which a user saves without changing the photo, but the photo isn't the default one (He changed in a previous save).
                // In this case we don't upload anything, we just need to set the photo path to the previous one
                if (carPhoto?.tag.toString().startsWith("carImages/")) {
                    carPhotoPath = carPhoto?.tag.toString()
                    success++
                } else if ((carPhoto?.tag != null) && (carPhoto.tag != "default")) {//if the photo has been created
                    //var file = Uri.fromFile(File(carPhoto.tag.toString()))
                    val file: Uri? = carPhoto.tag as Uri?
                    carPhotoPath = "carImages/${id}_${Timestamp(System.currentTimeMillis())}"
                    val storageRef = storage.reference.child(carPhotoPath)

                    if (file != null) {

                        //Upload using coroutines, because it takes time to upload
                        MainScope().launch {
                            withContext(Dispatchers.IO) {
                                storageRef.putFile(file).addOnFailureListener {
                                    Log.d(TAG, "Failed with: ", it)
                                    Toast.makeText(
                                        requireContext(),
                                        "Unable to upload user picture",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }.addOnSuccessListener {
                                    success++
                                    if (success == 2) {
                                        findNavController().navigate(
                                            R.id.action_nav_trip_edit_to_nav_trip_detail,
                                            bundle
                                        )
                                    }
                                }.addOnFailureListener {
                                    Log.d(TAG, "Failed with: ", it)
                                    Toast.makeText(
                                        requireContext(),
                                        "Unable to save car image",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                } else {
                    success++
                }

                val db = FirebaseFirestore.getInstance()//create firestore instance

                var availableSeats = avaSeats?.text.toString().toInt()
                var deleteReq =
                    false // needed because when we want to delete the requests when we have the trip reference
                // We must cover the case in which the user changes the available seats by editing the trip. If it's 0 (or lower otherw. it doesn't make sense) we remove all the pending requests.
                if (availableSeats <= 0) {
                    availableSeats = 0 // No negative numbers allowed
                    deleteReq = true
                    // Here we should notify the users of the pending requests that their request was automatically refused
                }


                /*SAVE THE TRIP INFORMATION*/
                val trip = hashMapOf(//create hash map to upload
                    "tripID" to id,
                    "pickUpLocation" to pickUpLocation?.text.toString()
                        .dropLastWhile { !it.isLetter() }, //remove the space after the name of the city
                    "pickUpLocality" to pickUpLocation?.text.toString()
                        .dropLastWhile { !it.isLetter() }.substringBefore(',').toLowerCase(),
                    "dropLocation" to dropLocation?.text.toString()
                        .dropLastWhile { !it.isLetter() },
                    "dropLocality" to dropLocation?.text.toString()
                        .dropLastWhile { !it.isLetter() }.substringBefore(',').toLowerCase(),
                    "price" to price?.text.toString().toInt(),
                    "avaSeats" to availableSeats,
                    "addInfo" to addInfo?.text.toString(),
                    "data" to data?.text.toString(),
                    "pickUpHour" to pickUpHour?.text.toString(),
                    "dropHour" to dropHour?.text.toString(),
                    "carPhotoPath" to carPhotoPath,
                    "estDuration" to estDuration,
                    "userID" to userID,
                    "bookingUsersID" to emptyList<String>(),
                    "longitudePickUp" to startMarker.position.longitude.toString(),
                    "latitudePickUp" to startMarker.position.latitude.toString(),
                    "longitudeDrop" to endMarker.position.longitude.toString(),
                    "latitudeDrop" to endMarker.position.latitude.toString(),
                    "driverRateUsers" to emptyMap<String, String>(),
                    "usersRateDriver" to emptyMap<String, String>()
                )
                //i upload the trip
                val tripRef = db.collection("trips").document(id)

                // We have the reference to the trip. If we want to delete the requests AND if the trip already exists, update that trip accordingly
                if (deleteReq) {
                    tripRef.get().addOnSuccessListener { thisTrip ->
                        if (thisTrip.contains("requestUsersID")) {
                            tripRef.update("requestUsersID", FieldValue.delete())
                        }
                    }
                }

                // The merge option is crucial: if the trip exists, it updates it!! If it doesn't exits, it creates it
                tripRef.set(trip, SetOptions.merge()).addOnSuccessListener {
                    success++
                    if (success == 2) {
                        findNavController().navigate(
                            R.id.action_nav_trip_edit_to_nav_trip_detail,
                            bundle
                        )
                    }
                }.addOnFailureListener {
                    Log.d(TAG, "Failed with: ", it)
                    Toast.makeText(requireContext(), "Unable save the trip", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            saving.cancel()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        TODO("Not yet implemented")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val carPhoto = view?.findViewById<ImageView>(R.id.imageViewProfileImage)
        outState.putString("carPhoto", carPhoto?.tag.toString())
        super.onSaveInstanceState(outState)
    }

    /*4*************************************************************************************4*/
    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}

