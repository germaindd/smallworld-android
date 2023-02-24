package com.example.smallworld.ui.map.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.smallworld.R
import com.example.smallworld.ui.map.PermissionsManager
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.removeOnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : Fragment() {
    private var _mapView: MapView? = null

    private val mapView: MapView get() = _mapView!!
    private val onMapClickListener: OnMapClickListener = OnMapClickListener {
        upstreamOnMapClickListener?.invoke()
        false
    }

    @Inject
    lateinit var permissionsManager: PermissionsManager

    private var upstreamOnMapClickListener: (() -> Unit)? = null

    private var compassMarginTop: Float? = null
    private var compassMarginRight: Float? = null

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
            compassMarginTop?.let { compass.marginTop = it }
            compassMarginRight?.let { compassMarginRight = it }
            getMapboxMap().gesturesPlugin {
                addOnMapClickListener(onMapClickListener)
            }
        }

        if (permissionsManager.hasLocationPermissions) {
            showLocation()
        } else {
            lifecycleScope.launch {
                val havePermissions =
                    permissionsManager.requestLocationPermissions(this@MapFragment)
                if (havePermissions) showLocation()
            }
        }

        return mapView
    }

    private fun showLocation() {
        mapView.location.let {
            it.enabled = true
            it.locationPuck = LocationPuck2D(
                bearingImage = requireContext().getDrawable(R.drawable.map_screen_location_puck),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
    }

    override fun onDestroy() {
        mapView.getMapboxMap().removeOnMapClickListener(onMapClickListener)
        upstreamOnMapClickListener = null
        super.onDestroy()
    }

    fun setOnMapClickListener(operation: () -> Unit) {
        upstreamOnMapClickListener = operation
    }

    fun setCompassMargins(top: Dp? = null, right: Dp? = null) {
        Density(requireContext()).run {
            top?.toPx()?.let {
                compassMarginTop = it
                _mapView?.compass?.marginTop = it
            }
            right?.toPx()?.let {
                compassMarginRight = it
                _mapView?.compass?.marginRight = it
            }
        }
    }
}