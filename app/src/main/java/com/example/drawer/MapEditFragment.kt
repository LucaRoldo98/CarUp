package com.example.drawer

import SharedViewModel
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.internal.userAgent
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import java.io.IOException
import java.util.*


class MapEditFragment : DialogFragment() {

    override fun onStart() {
        // Make the dialog fragment wider
        dialog?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //create road manager
        val roadManager: RoadManager = OSRMRoadManager(requireContext(), userAgent)
        var roadOverlay: Polyline? = null
        //get model for live data
        val model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val map = view.findViewById<MapView>(R.id.mapViewDialog)
        map.setTileSource(TileSourceFactory.MAPNIK)
        //get views
        val searchBar = view.findViewById<EditText>(R.id.mapSearchView)
        val searchBtn = view.findViewById<ImageButton>(R.id.mapSearchButton)
        val doneBtn = view.findViewById<TextView>(R.id.mapDoneBtn)
        val dialogTv = view.findViewById<TextView>(R.id.mapDialogTv)

        if (selectedPar == "start") {
            dialogTv.text = resources.getString(R.string.selYourPickup)
        } else {
            dialogTv.text = resources.getString(R.string.selYourDrop)
        }

        //searchBar.setQuery("CIAO" as CharSequence, false)
        searchBtn.setOnClickListener {
            val geocoder = Geocoder(requireContext())
            val strToSearch = searchBar.text.toString()

            if (job == null || job?.isActive == false) {//i will not interrupt any ongoing computation
                job = MainScope().launch {
                    val coordResults = geocoder.getFromLocationName(strToSearch, 1)
                    if (coordResults.size > 0) {
                        val coord: Address = coordResults[0]//we took the first
                        //get the real address
                        var addrStr = coord.locality.toString()
                        //check if the other part of the address are null, if not we add them to the string
                        if (coord.thoroughfare != null) {
                            addrStr += ", ${coord.thoroughfare}"
                            if (coord.subThoroughfare != null) {
                                addrStr += " ${coord.subThoroughfare}"
                            }
                        }
                        addrStr += ", ${coord.countryName}"
                        //update view model
                        if (selectedPar == "start") {
                            model.setPoint(GeoPoint(coord.latitude, coord.longitude), "s")
                            model.setAddress(addrStr, "s")
                        } else {
                            model.setPoint(GeoPoint(coord.latitude, coord.longitude), "e")
                            model.setAddress(addrStr, "e")
                        }
                    } else {
                        Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        //create markers
        var startMarker = Marker(map)
        var endMarker = Marker(map)
        //EVENT MAP TOUCH
        map.overlays.add(object : Overlay() {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onSingleTapConfirmed(
                e: MotionEvent,
                mapView: MapView
            ): Boolean {

                //create projection of map
                val projection = mapView.projection
                val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt())
                val selMarker: Marker?
                val selDrawMarker: Drawable?
                var markerTitle = ""

                //select the right marker
                if (selectedPar == "start") {
                    selMarker = startMarker
                    selDrawMarker = resources.getDrawable(R.drawable.ic_baseline_pick_up_location_on_24)
                    markerTitle = "Pick-up location: "
                } else {
                    selMarker = endMarker
                    selDrawMarker = resources.getDrawable(R.drawable.ic_baseline_drop_location_on_24)
                    markerTitle = "Drop location: "
                }

                //if a marker has been selected

                mapView.overlays.remove(selMarker)//remove previous markers
                mapView.invalidate()
                //set position of new marker

                selMarker.position = geoPoint as GeoPoint
                selMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                map.overlays.add(selMarker)
                selMarker.icon = selDrawMarker
                selMarker.title = markerTitle //set title of new marker

                //start coroutines for getting address from coordinates
                if (job == null || job?.isActive == false)//i will not interrupt any ongoing computation
                    job = MainScope().launch {//create a coroutine, suspendable computation
                        //create object for API query
                        val geocoder = Geocoder(requireContext())
                        try {
                            //Get address from coordinate from web
                            val a = geocoder.getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)

                            //if we found something
                            if (a.size > 0) {
                                val address: Address = a[0]//we took the first row
                                var addrStr = address.locality.toString()
                                markerTitle += addrStr
                                //check if the other part of the address are null, if not we add them to the string
                                if (address.thoroughfare != null) {
                                    addrStr += ", ${address.thoroughfare}"
                                    if (address.subThoroughfare != null) {
                                        addrStr += " ${address.subThoroughfare}"
                                    }
                                }
                                addrStr += ", ${address.countryName}"
                                //update livedata accordingly
                                if (selectedPar == "start") {
                                    model.setAddress(addrStr, "s")
                                    model.setPoint(geoPoint, "s")
                                    startMarker = selMarker
                                } else {
                                    model.setAddress(addrStr, "e")
                                    model.setPoint(geoPoint, "e")
                                    endMarker = selMarker
                                }
                            } else {
                                Toast.makeText(requireContext(), "Place not found", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: IOException) {
                            Toast.makeText(requireContext(), "Error in looking for that place", Toast.LENGTH_LONG).show()
                        }
                    }
                return true
            }
        })
        //dismiss the dialog when clicking on the done button
        doneBtn.setOnClickListener {
            this.dismiss()
        }

        //set view model observer for addresses
        model.startEndAddress.observe(viewLifecycleOwner) {
            val (s, e) = it
            if (selectedPar == "start") {
                searchBar.setText(s)
            } else {
                searchBar.setText(e)
            }
        }
        //set view model observer for coordinates
        model.startEndPoints.observe(viewLifecycleOwner)
        {
            val (s, e) = it
            val waypoints = ArrayList<GeoPoint>()
            waypoints.add(s)
            waypoints.add(e)
            val road = roadManager.getRoad(waypoints)
            if (roadOverlay != null) map.overlays.removeAll(listOf(roadOverlay))
            roadOverlay = RoadManager.buildRoadOverlay(road)
            //var overlays = map.overlays
            map.overlays.add(roadOverlay)
            startMarker.position = s
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            startMarker.icon = resources.getDrawable(R.drawable.ic_baseline_location_pickup)
            startMarker.title = "Pick-up point"
            map.overlays.add(startMarker)
            //get coordinate and create marker of endpoint
            endMarker.position = e
            endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            endMarker.icon = resources.getDrawable(R.drawable.ic_baseline_location_drop)
            endMarker.title = "Drop point"
            map.overlays.add(endMarker)
            //create bounding box for zoom on the map
            if (selectedPar == "start") {
                map.controller.setCenter(s)
            } else {
                map.controller.setCenter(e)
            }
            // If it's the first time that we're opening it, we set the zoom to default; otherwise we keep the zoom as the user set it
            if (map.zoomLevelDouble == 0.0) {
            map.controller.setZoom(11) }
            map.invalidate()
        }

    }


    companion object {
        val TAG = "MapFragment"

        var selectedPar: String? = null
    }

}