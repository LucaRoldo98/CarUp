package com.example.drawer


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DriverRatingFragment : Fragment() {

    lateinit var imageURL: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_driver_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val NameSurname = view.findViewById<TextView>(R.id.nameSurname)
        val photoProfile = view.findViewById<ImageView>(R.id.photoProfile)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val buttonRate = view.findViewById<Button>(R.id.buttonRate)

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val account = GoogleSignIn.getLastSignedInAccount(requireContext())//get signed in account
        val userID = account?.id.toString()//get id of the account
        val db = FirebaseFirestore.getInstance()//create firestore instance
        var success = 0


        //find the driver data

        val userRef = db.collection("users")
            .document(arguments?.get("otherUserID").toString())//get other user data from firestore
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    //set the data of the user in the various views
                    NameSurname.text = document.get("fullName").toString()

                    if (document["imgPath"].toString() == "default") {//if the field of the url is the default
                        //imageURL = account?.photoUrl.toString()
                        // i load the google profile picture
                        Glide.with(this).load(resources.getDrawable(R.drawable.ic_user1))
                            .into(photoProfile)
                    } else {
                        val imageRef =
                            storageRef.child(document["imgPath"].toString())//otherwise i take the one specified in the field
                        imageRef.downloadUrl.addOnSuccessListener { Uri ->
                            imageURL = Uri.toString()
                            Glide.with(this).load(imageURL).into(photoProfile)
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(), "Unable to get user data", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(requireContext(), "Unable to get user data", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        buttonRate.setOnClickListener {
            val msg = ratingBar.rating.toLong()

            var docRef = db.collection("trips").document(arguments?.getString("TripId").toString())
            docRef.update(
                mapOf(
                    "usersRateDriver.${userID}" to msg
                )
            ).addOnSuccessListener {
                success++
                if(success == 2) {
                    findNavController().navigate(R.id.action_rateDriverTripsListFragment_to_rateTripsListFragment)
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error in saving the rating", Toast.LENGTH_SHORT)
                    .show()
            }
            docRef = db.collection("users").document(arguments?.getString("otherUserID").toString())
            docRef.update(
                mapOf(
                "driverRate" to FieldValue.increment(msg),
                "numDrives" to FieldValue.increment(1)
                 )
            ).addOnSuccessListener {
                success++
                if(success == 2) {
                    findNavController().navigate(R.id.action_rateDriverTripsListFragment_to_rateTripsListFragment)
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error in saving the rating", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}


