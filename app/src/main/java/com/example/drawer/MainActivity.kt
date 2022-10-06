package com.example.drawer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


@GlideModule
class MyAppGlideModule : AppGlideModule()


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    lateinit var userID: String
    lateinit var fullName: String
    lateinit var email: String

    lateinit var imageURL: String
    lateinit var account: GoogleSignInAccount
    private var TAG = "MainActivity"
    //var listEmptyOrNull: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //get data from login intent
        val bundleAccount = intent?.extras?.getBundle("bundleAccount")
        // set fragment trip list Arguments

        //get account var from bundle
        account = bundleAccount?.get("gAccount") as GoogleSignInAccount
        userID = account.id.toString()//get user ID
        //imageURL = account.photoUrl.toString()
        //create instance of show profile fragment
        val showProfileFragment = ShowProfileFragment()
        //set as showProfile arguments the userID
        showProfileFragment.arguments = bundleOf("userID" to userID)
        //getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, showProfileFragment).commit();

        //create storage instance
        val storage = FirebaseStorage.getInstance()
        //create storage reference
        val storageRef = storage.reference
        //create DB instance
        val db = FirebaseFirestore.getInstance()
        //get user data

        val docRef = db.collection("users").document(userID)

        //LOOK FOR USER BY SEARCHING ITS ID
        docRef.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot?> { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {//if it exist i retrieve the data for display name, email and photo in the

                    if (document["imgPath"].toString() == "default") {//if the field of the url is the default
                        //imageURL = account.photoUrl.toString()//i load the google profile picture
                        imageURL = "default"
                    } else {
                        val imageRef =
                            storageRef.child(document["imgPath"].toString())//otherwise i take he one specified in the field
                        imageRef.downloadUrl.addOnSuccessListener { Uri ->
                            imageURL = Uri.toString()
                        }
                    }

                    fullName = document["fullName"].toString()
                    email = document["emailAddress"].toString()

                    Log.d(TAG, "ID found")

                } else {//if it doesn't exist I create a new record for the user
                    Log.d(TAG, "ID not found")
                    //initialize the lateinit variables
                    fullName = account.displayName.toString()
                    email = account.email.toString()
                    //create a map with all the fields
                    val userData = hashMapOf(
                        "fullName" to fullName,
                        "nickName" to "",
                        "emailAddress" to email,
                        "postalAddress" to "",
                        "userID" to userID,
                        "bookingTripsID" to emptyList<String>(),
                        "imgPath" to "default",
                        "driverRate" to 0,
                        "passengerRate" to 0,
                        "numDrives" to 0,
                        "numTravel" to 0,
                        "age" to 0,
                        "pickUpLocality" to "",
                        "dropLocality" to ""
                    )
                    //imageURL = account.photoUrl.toString()
                    imageURL = "default" // Load the default image; problems with the google profile image
                    //add a new record for this user in the database
                    db.collection("users").document(userID)
                        .set(userData)
                        .addOnSuccessListener {
                            Log.d(
                                TAG,
                                "DocumentSnapshot successfully written!"
                            )
                        }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                }
            } else {
                Log.d(TAG, "Failed with: ", task.exception)
            }
        })

        //SNAPSHOT LISTENER FOR USER DATA CHANGES
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {//if it was successful
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            //if the snapshot is not null and it exist
            if (snapshot != null && snapshot.exists()) {
                fullName = snapshot["fullName"].toString()
                email = snapshot["emailAddress"].toString()
                if (snapshot["imgPath"].toString() == "default") {//if the field of the url is the default
                    //imageURL = account.photoUrl.toString()//i load the google profile picture
                    imageURL="default"
                } else {
                    val imageRef =
                        storageRef.child(snapshot["imgPath"].toString())//otherwise i take he one specified in the field
                    imageRef.downloadUrl.addOnSuccessListener { Uri ->
                        imageURL = Uri.toString()
                    }
                }
            }
        }


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_show_profile,
                R.id.nav_trip_list,
                R.id.nav_trip_list_others,
                R.id.nav_trip_edit,
                R.id.nav_trip_detail,
                R.id.nav_filter_list,
                R.id.tripsOfInterestListFragment,
                R.id.boughtTripsListFragment,
                R.id.rateDriverTripsListFragment,
                R.id.rateTripsListFragment
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        /*LOGOUT*/
        //val menu = navView.menu//get menu
        //val item = menu.findItem(R.id.nav_logout)//get item for logout
        var successLogout = 0
        //ge logout button
        val logoutButton = findViewById<Button>(R.id.logout_button)


        //item.setOnMenuItemClickListener {
        logoutButton.setOnClickListener {
            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)//get sign in information
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            val mGoogleSignInClient =
                GoogleSignIn.getClient(this, gso)//get the google sign in client
            //sign out of the account
            mGoogleSignInClient.signOut().addOnCompleteListener(this, OnCompleteListener<Void?> {
                successLogout++
                if (successLogout == 2) {//check if the whole procedure has been performed
                    intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            })
            //revoke access
            mGoogleSignInClient.revokeAccess().addOnCompleteListener(this) {
                successLogout++
                if (successLogout == 2) {//check if the whole procedure has been performed
                    intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            true
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onSupportNavigateUp(): Boolean {
        //get nav and header
        val navView: NavigationView = findViewById(R.id.nav_view)
        val header: View = navView.getHeaderView(0)
        //get the views
        val tvFullNameNav = header.findViewById<TextView>(R.id.nav_head_full_name)
        val tvEmailNav = header.findViewById<TextView>(R.id.nav_header_email)
        val imgProfileNav = header.findViewById<ImageView>(R.id.nav_header_profile_img)
        //set value to the textviews
        tvFullNameNav?.text = fullName
        tvEmailNav?.text = email
        //load image into the imageview

        if (imageURL=="default") {
            imgProfileNav.load(resources.getDrawable(R.drawable.ic_user1))
        }
        else {
            Glide.with(this).load(imageURL).into(imgProfileNav) }

        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}

data class User(
    val fullName: String,
    val nickName: String,
    val emailAddress: String,
    val postalAddress: String,
    val userID: String,
    val bookingsTripsID: List<String>,
    val imgPath: String
) {

    constructor() : this("", "", "", "", "", emptyList(), "")

}