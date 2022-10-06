package com.example.drawer

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.sql.Time
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil


const val TAKE_PROFILE_PICTURE = 4
const val PICK_PROFILE_IMAGE = 5

var photoUrl: Uri? = null

@Suppress("DEPRECATION")
class EditProfileFragment : Fragment() {

    lateinit var userID: String
    //lateinit var imageURL: String
    val TAG = "EditProfileFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            //when i go back with the button from trip detail i go back to the trip list
            photoUrl=null
            findNavController().navigate(R.id.action_nav_edit_profile_to_nav_show_profile)
        }
        return inflater.inflate(R.layout.activity_edit_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())//get signed in account
        userID = account?.id.toString()//get id of the account

        //create storage instance
        val storage = FirebaseStorage.getInstance()
        //create storage reference
        val storageRef = storage.reference

        //get all the views
        val buttonCamera = view.findViewById<ImageButton>(R.id.imageButton)
        val age = view.findViewById<EditText>(R.id.editTextAge)
        val fullName = view.findViewById<EditText>(R.id.editTextFullName)
        val nickName = view.findViewById<EditText>(R.id.editTextNickName)
        val emailAddress = view.findViewById<EditText>(R.id.editTextEmailAddress)
        val postalAddress = view.findViewById<EditText>(R.id.editTextPostalAddress)
        val photo = view.findViewById<ImageView>(R.id.imageView2)
        //if i press the camera button
        buttonCamera.setOnClickListener {
            buttonCamera.performLongClick()
        }
        registerForContextMenu(buttonCamera)//set context menu

        val db = FirebaseFirestore.getInstance()//create Firestore instance
        //get user data from Firestore
        val docRef = db.collection("users").document(userID)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    //set the data of the user in the various views
                    fullName.setText(document.get("fullName").toString())
                    nickName.setText(document.get("nickName").toString())
                    age.setText(document.get("age").toString())
                    emailAddress.setText(document.get("emailAddress").toString())
                    postalAddress.setText(document.get("postalAddress").toString())
                    //if the photo uri is not null i load the photo that i took

                    if (photoUrl!=null) {
                        photo.load(photoUrl)
                        photo.tag = photoUrl

                    } else {
                        if (document["imgPath"].toString() == "default") {//if the field of the url is the default
                            //val imageURL = account?.photoUrl.toString()//i load the google profile picture
                            Glide.with(this).load(resources.getDrawable(R.drawable.ic_user1)).into(photo)
                        } else {
                            val imageRef =
                                storageRef.child(document["imgPath"].toString())//otherwise i take he one specified in the field
                            imageRef.downloadUrl.addOnSuccessListener { Uri ->
                                val imageURL = Uri.toString()
                                Glide.with(this).load(imageURL).into(photo)
                                photo.tag = document["imgPath"].toString()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Unable to get user data", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Log.d(TAG, "Failed with: ", task.exception)
                Toast.makeText(requireContext(), "Unable to get user data", Toast.LENGTH_SHORT)
                    .show()
            }
        }
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
            photoUrl = this.toUri()
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
                    startActivityForResult(takePictureIntent, TAKE_PROFILE_PICTURE)
                }
            }
        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(photoUrl?.path.toString())
            mediaScanIntent.data = Uri.fromFile(f)
            requireActivity().sendBroadcast(mediaScanIntent)
        }
    }

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
                    //startActivityForResult(takePictureIntent, TAKE_PROFILE_PICTURE)
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
                startActivityForResult(intent, PICK_PROFILE_IMAGE)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PROFILE_PICTURE && resultCode == AppCompatActivity.RESULT_OK) {
            /* // Code for low resolution pictures
            val imageBitmap = data!!.extras?.get("data") as Bitmap
            val imageView = view?.findViewById<ImageView>(R.id.imageView2)
            val imgFile = File(
                context?.filesDir,
                "${userID}_pro.png"
            ) // Store the image everytime it is returned from the activity
            val fo = FileOutputStream(imgFile)
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fo)
            fo.flush()
            fo.close()
            val filepath = imgFile.absolutePath.toString()
            photoUrl = imgFile.toUri()
            imageView?.load(File(filepath))
            imageView?.tag = imgFile.toUri() */
            val imageView = view?.findViewById<ImageView>(R.id.imageView2)
            galleryAddPic()
            imageView?.load(photoUrl)
            // Compress using compression library
            val compressedImgFile = Compressor(requireContext()).compressToFile(photoUrl?.toFile())
            val compressedUri = compressedImgFile.toUri()
            photoUrl = compressedUri
            imageView?.tag = compressedUri

        } else if (requestCode == PICK_PROFILE_IMAGE && resultCode == Activity.RESULT_OK) {
            val fileURI = data!!.data
            val imageView = view?.findViewById<ImageView>(R.id.imageView2)
            /* if (imageView != null) {
                Glide.with(this).load(fileURI).into(imageView)
            } */

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
            var orientation = 0
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
            val imgFile = File(context?.filesDir, "${id}_profile.jpeg")
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
            //Further compress using compression library
            val compressedImgFile = Compressor(requireContext()).compressToFile(imgFile)
            val compressedUri = compressedImgFile.toUri()
            //val compressedUri = imgFile.toUri()
            photoUrl = compressedUri
            imageView?.load(compressedUri)
            imageView?.tag = compressedUri
        }
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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        photoUrl=null // So that if we come back into the edit it won't show the selected picture again (in the case it wasn't saved)
        if (item.itemId == R.id.save) {
            var success = 0 //variable to see if all steps are successful
            //get all the views
            val fullName = view?.findViewById<EditText>(R.id.editTextFullName)
            val nickName = view?.findViewById<EditText>(R.id.editTextNickName)
            val age = view?.findViewById<EditText>(R.id.editTextAge)
            val emailAddress = view?.findViewById<EditText>(R.id.editTextEmailAddress)
            val postalAddress = view?.findViewById<EditText>(R.id.editTextPostalAddress)
            val photo = view?.findViewById<ImageView>(R.id.imageView2)

            val storage = FirebaseStorage.getInstance()//get instance of a storage
            //get the reference of the storage
            var imageURL = "default"
            if (photo?.tag != null) { //if the photo has been created
                //var file = Uri.fromFile(File(photo.tag.toString()))
                if (!photo.tag.toString()
                        .startsWith("userImages/")
                ) { // It means that a new photo was added
                    val file: Uri = photo.tag as Uri
                    imageURL = "userImages/${userID}_${Timestamp(System.currentTimeMillis())}"
                    val storageRef = storage.reference.child(imageURL)

                    MainScope().launch {
                        withContext(Dispatchers.IO) {
                            storageRef.putFile(file).addOnFailureListener {
                                Log.d(TAG, "Failed with: ", it)
                                Toast.makeText(
                                    requireContext(),
                                    "Unable upload user picture",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.addOnSuccessListener {
                                success++
                                if (success == 2) {
                                    findNavController().navigate(R.id.action_nav_edit_profile_to_nav_show_profile)
                                }
                            }
                        }
                    }
                } else { // User didn't change profile pic. Keep the same uri that was passed
                    imageURL = photo.tag.toString()
                    success++
                }
            }
            else {
                success++
            }

            val db = FirebaseFirestore.getInstance()//create Firestore instance
            //get user data from Firestore
            val docRef = db.collection("users").document(userID)
            docRef.update(
                mapOf(
                    "fullName" to fullName?.text.toString(),
                    "nickName" to nickName?.text.toString(),
                    "age" to age?.text.toString(),
                    "emailAddress" to emailAddress?.text.toString(),
                    "postalAddress" to postalAddress?.text.toString(),
                    "imgPath" to imageURL
                )
            ).addOnSuccessListener {
                success++
                if (success == 2) {
                    findNavController().navigate(R.id.action_nav_edit_profile_to_nav_show_profile)
                }
            }.addOnFailureListener {
                Log.d(TAG, "Failed with: ", it)
                Toast.makeText(
                    requireContext(),
                    "Unable update user information",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        /*if (photo?.tag != null) {

            // Persist information when the EditProfileActivity returns data

            val jsonProfile= JSONObject()
            jsonProfile.put("fullName", fullName?.text)
            jsonProfile.put("nickName", nickName?.text)
            jsonProfile.put("emailAddress", emailAddress?.text)
            jsonProfile.put("postalAddress", postalAddress?.text)
            jsonProfile.put("imagePath", photo.tag)

            val toStore = jsonProfile.toString()

            val sharedPref = this.activity?.getPreferences(Context.MODE_PRIVATE)
            with (sharedPref?.edit()) {
                this?.putString("profile", toStore)
                this?.apply()
            }
            findNavController().navigate(R.id.action_nav_edit_profile_to_nav_show_profile)
        }
        else {

            // Persist information when the EditProfileActivity returns data
            val jsonProfile= JSONObject()
            jsonProfile.put("fullName", fullName?.text)
            jsonProfile.put("nickName", nickName?.text)
            jsonProfile.put("emailAddress", emailAddress?.text)
            jsonProfile.put("postalAddress", postalAddress?.text)

            val toStore = jsonProfile.toString()

            val sharedPref = this.activity?.getPreferences(Context.MODE_PRIVATE)
            with (sharedPref?.edit()) {
                this?.putString("profile", toStore)
                this?.apply()
            }
            findNavController().navigate(R.id.action_nav_edit_profile_to_nav_show_profile)
        }*/
        return super.onOptionsItemSelected(item)
    }
}

