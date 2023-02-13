package com.example.smallworld.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar

class MapFragment : Fragment() {
    private var _mapView: MapView? = null
    private val mapView: MapView get() = _mapView!!
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mapView = MapView(
            requireContext(),
            MapInitOptions(requireContext(), styleUri = Style.MAPBOX_STREETS)
        ).apply {
            logo.enabled = false
            attribution.enabled = false
            scalebar.enabled = false
        }
        return mapView
    }
}