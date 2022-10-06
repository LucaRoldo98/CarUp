package com.example.drawer

import SharedViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import okhttp3.internal.userAgent
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.util.*


class MapDetailsFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_details, container, false)
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
        val doneBtn = view.findViewById<TextView>(R.id.mapDoneBtn)

        //create markers
        val startMarker = Marker(map)
        val endMarker = Marker(map)
        //EVENT MAP TOUCH

        //dismiss the dialog when clicking on the done button
        doneBtn.setOnClickListener {
            this.dismiss()
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
            startMarker.icon = resources.getDrawable(R.drawable.ic_baseline_pick_up_location_on_24)
            startMarker.title = "Pick-up point"
            map.overlays.add(startMarker)
            //get coordinate and create marker of endpoint
            endMarker.position = e
            endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            endMarker.icon = resources.getDrawable(R.drawable.ic_baseline_drop_location_on_24)
            endMarker.title = "Drop point"
            map.overlays.add(endMarker)
            //create bounding box. Zoom to bounding box doesn't work so we will do it manually
            val boundingBox: BoundingBox =
                BoundingBox.fromGeoPoints(listOf(s, e))
            val midPoint = boundingBox.center
            map.controller.setCenter(midPoint)
            /*if (boundingBox.diagonalLengthInMeters < 100000) {
            map.controller.setZoom(10) }
            else {
                map.controller.setZoom(8)
            }*/

            // The zoom was retrieved by the bounding box in tripDetails (or tripEdit)
            map.controller.setZoom(zoom)
            map.invalidate()
        }

    }


    companion object {
        val TAG = "MapFragment"
        var selectedPar: String? = null
        var zoom: Int = 0
    }

}