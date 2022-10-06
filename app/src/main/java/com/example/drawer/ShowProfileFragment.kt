package com.example.drawer

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage



class ShowProfileFragment : Fragment() {

    lateinit var userID: String
    lateinit var imageURL: String

    val TAG = "ShowProfileFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.show_profile_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //get all the views
        val et0 = view.findViewById<TextView>(R.id.Age)
        val et1 = view.findViewById<TextView>(R.id.FullName)
        val et2 = view.findViewById<TextView>(R.id.Nickname)
        val et3 = view.findViewById<TextView>(R.id.EmailAddress)
        val et4 = view.findViewById<TextView>(R.id.Location)
        val numRatePass = view.findViewById<TextView>(R.id.numRatePassenger)
        val numRateDriver = view.findViewById<TextView>(R.id.numRateDriver)
        val starPass = view.findViewById<RatingBar>(R.id.ratingBarPassenger)
        val starDriver = view.findViewById<RatingBar>(R.id.ratingBarDriver)
        val image = view.findViewById<ImageView>(R.id.imageView2)
        val buttonRate = view.findViewById<Button>(R.id.rateButton)
        val passRateTv = view.findViewById<TextView>(R.id.passengerRate)
        val driveRateTv = view.findViewById<TextView>(R.id.DriverRate)
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())//get signed in account
        var rateUser = false
        //create storage instance
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference //create storage reference
        val db = FirebaseFirestore.getInstance()//create firestore instance

        // If we want to see out profile or one of the other profiles
        if (arguments?.containsKey("userID") == true) {
            userID = arguments?.getString("userID").toString()
        }else{
            //get id of the account
            userID = account?.id.toString()
        }
        if (arguments?.containsKey("ratePassenger") == true) {
            rateUser = arguments?.getBoolean("ratePassenger")!!
        }
        //if we have to rate a user
        if(rateUser){
            buttonRate.visibility = View.VISIBLE
            driveRateTv.visibility = View.GONE
            passRateTv.text = "Rate this passenger"
            starDriver.visibility = View.GONE
            starPass.setIsIndicator(false)
            numRatePass.visibility = View.GONE
            numRateDriver.visibility = View.GONE
        }
        //when we press the rate button

        buttonRate.setOnClickListener {
            val msg = starPass.rating.toLong()
            var success = 0

            var docRef = db.collection("trips").document(arguments?.getString("tripID").toString())
            docRef.update(
                mapOf(
                    "driverRateUsers.${userID}" to msg
                )
            ).addOnSuccessListener {
                success++
                if(success == 2) {
                    findNavController().navigate(R.id.action_nav_show_profile_to_requestsFragment, bundleOf("TripID" to TripID, "tripIsPast" to rateUser))
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error in saving the rating", Toast.LENGTH_SHORT)
                    .show()
            }
            docRef = db.collection("users").document(userID)
            docRef.update(
                mapOf(
                    "passengerRate" to FieldValue.increment(msg),
                    "numTravel" to FieldValue.increment(1)
                )
            ).addOnSuccessListener {
                success++
                if(success == 2) {
                    findNavController().navigate(R.id.action_nav_show_profile_to_requestsFragment, bundleOf("TripID" to TripID, "tripIsPast" to rateUser))
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error in saving the rating", Toast.LENGTH_SHORT).show()
            }
        }

        val docRef = db.collection("users").document(userID)//get user data from firestore
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    //set the data of the user in the various views
                    et0.text = document.get("age").toString()
                    et1.text = document.get("fullName").toString()
                    et2.text = document.get("nickName").toString()
                    et3.text = document.get("emailAddress").toString()
                    et4.text = document.get("postalAddress").toString()
                    numRateDriver.text = "(${document.get("numDrives").toString()})"
                    numRatePass.text = "(${document.get("numTravel").toString()})"
                    //compute the average of ratings
                    var rating = document.get("passengerRate") as Long
                    if ((rating > 0) && !rateUser) {
                        rating /= document.get("numTravel").toString().toLong()
                        starPass.rating = rating.toFloat()
                    }

                    rating = document.get("driverRate") as Long
                    if (rating > 0) {
                        rating /= document.get("numDrives").toString().toLong()
                        starDriver.rating = rating.toFloat()
                    }

                    if (document["imgPath"].toString() == "default") {//if the field of the url is the default
                        if (arguments?.containsKey("userID") == false) { // if we are seeing our profile
                            imageURL = account?.photoUrl.toString()
                            Glide.with(this).load(imageURL).into(image)
                        } //i load the google profile picture
                        else {
                            imageURL = "default"
                            image.setImageResource(R.drawable.ic_user1)
                        }
                    } else {
                        val imageRef = storageRef.child(document["imgPath"].toString())//otherwise i take he one specified in the field
                        imageRef.downloadUrl.addOnSuccessListener { Uri ->
                            imageURL = Uri.toString()
                            Glide.with(this).load(imageURL).into(image)
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

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.pencil, menu)
        return
    }

    // If it's another user's profile we don't want the menu
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (arguments?.containsKey("userID") == true) {
            menu.clear()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.edit_pencil) {
           /* val et0 = view?.findViewById<TextView>(R.id.Age)
            val et1 = view?.findViewById<TextView>(R.id.FullName)
            val et2 = view?.findViewById<TextView>(R.id.Nickname)
            val et3 = view?.findViewById<TextView>(R.id.EmailAddress)
            val et4 = view?.findViewById<TextView>(R.id.Location)*/
            //val image = view?.findViewById<ImageView>(R.id.imageView2)

            findNavController().navigate(R.id.action_nav_show_profile_to_nav_edit_profile)

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        /*val et1 = view?.findViewById<TextView>(R.id.FullName)
        val et2 = view?.findViewById<TextView>(R.id.Nickname)
        val et3 = view?.findViewById<TextView>(R.id.EmailAddress)
        val et4 = view?.findViewById<TextView>(R.id.Location)
        val image = view?.findViewById<ImageView>(R.id.imageView2)
        outState.putString("fullName", et1?.text.toString())
        outState.putString("nickname", et2?.text.toString())
        outState.putString("email", et3?.text.toString())
        outState.putString("location", et4?.text.toString())
        if (image?.tag != null) {
            val imagePath = image.tag.toString()
            outState.putString("imagePath", imagePath)
        }*/
    }


}