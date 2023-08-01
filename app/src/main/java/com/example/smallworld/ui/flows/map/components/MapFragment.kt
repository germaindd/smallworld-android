package com.example.smallworld.ui.flows.map.components

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresPermission
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.example.smallworld.BuildConfig
import com.example.smallworld.R
import com.example.smallworld.data.location.model.Location
import com.example.smallworld.ui.flows.map.LocationProvider
import com.example.smallworld.ui.flows.map.MapUtils
import com.example.smallworld.ui.flows.map.MapViewModel
import com.example.smallworld.ui.flows.map.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.removeOnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.maps.plugin.viewport.ViewportStatus
import com.mapbox.maps.plugin.viewport.ViewportStatusObserver
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.state.FollowPuckViewportState
import com.mapbox.maps.plugin.viewport.viewport
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DEFAULT_ZOOM = 4.0
private const val DEFAULT_BEARING = 0.0
private const val DEFAULT_PITCH = 0.0

private const val GO_TO_LOCATION_ANIMATION_DURATION = 1000L
private const val GO_TO_LOCATION_ANIMATION_DECELERATION_FACTOR = 2f

private const val FLY_INTO_ANIMATION_STARTING_ZOOM = 3.0
private const val FLY_INTO_ANIMATION_DURATION_MILLISECONDS = 2000L
private const val FLY_INTO_ANIMATION_DECELERATION_FACTOR = 2f

@AndroidEntryPoint
class MapFragment : Fragment() {
    @Inject
    lateinit var permissionsManager: PermissionsManager

    @Inject
    lateinit var locationDiffTool: LocationDiffTool

    @Inject
    lateinit var locationProvider: LocationProvider

    private var _mapView: MapView? = null
    private val mapView: MapView get() = _mapView!!

    private val userIdToViewAnnotationMap = hashMapOf<String, ImageView>()
    private var oldLocations: Iterable<Location> = emptySet()

    private var _viewModel: MapViewModel? = null
    private val viewModel get() = _viewModel ?: error("_viewModel not yet initialized")

    private var upstreamOnMapClickListener: (() -> Unit)? = null
    private val onMapClickListener: OnMapClickListener = OnMapClickListener {
        upstreamOnMapClickListener?.invoke()
        false
    }
    private val viewportStatusObserver = ViewportStatusObserver { _, to: ViewportStatus, _ ->
        when (to) {
            ViewportStatus.Idle -> viewModel.setTrackLocation(false)
            is ViewportStatus.State,
            is ViewportStatus.Transition -> Unit
        }
    }

    private var compassMarginTop: Float? = null
    private var compassMarginRight: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialise PermissionsManager
        permissionsManager.init(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _mapView = MapView(
            requireContext(),
            MapInitOptions(
                requireContext(),
                styleUri = Style.MAPBOX_STREETS,
                resourceOptions = ResourceOptions.Builder()
                    .accessToken(BuildConfig.MAPBOX_ACCESS_TOKEN).build()
            )
        ).apply {
            logo.enabled = false
            attribution.enabled = false
            scalebar.enabled = false
            location.locationPuck = MapUtils.getLocationPuck(requireContext())
            compassMarginTop?.let { compass.marginTop = it }
            compassMarginRight?.let { compassMarginRight = it }
            getMapboxMap().gesturesPlugin {
                addOnMapClickListener(onMapClickListener)
            }
            viewport.addStatusObserver(viewportStatusObserver)
        }

        return mapView
    }

    override fun onDestroyView() {
        mapView.viewport.removeStatusObserver(viewportStatusObserver)
        mapView.getMapboxMap().removeOnMapClickListener(onMapClickListener)
        super.onDestroyView()
    }

    override fun onDestroy() {
        upstreamOnMapClickListener = null
        val cameraState = mapView.getMapboxMap().cameraState
        viewModel.saveCameraPosition(
            latitude = cameraState.center.latitude(),
            longitude = cameraState.center.longitude(),
            zoom = cameraState.zoom,
            bearing = cameraState.bearing
        )
        super.onDestroy()
    }

    // because of limitations with jetpack compose, it is impossible to find the ViewModelStoreOwner of the compose navigation destinations
    // that this fragment is nested in. the ViewModelStoreOwner is passed down manually as a workaround on fragment inflation in
    // MapScreen.kt. Hence why all the flows are observed here rather than in onViewCreated()
    @SuppressLint("MissingPermission")
    fun setViewModelStoreOwner(viewModelStoreOwner: ViewModelStoreOwner) {
        _viewModel = ViewModelProvider(viewModelStoreOwner)[MapViewModel::class.java]
        lifecycleScope.launch {
            viewModel.goToCurrentLocation.collect { goToCurrentLocation() }
        }
        lifecycleScope.launch {
            for (event in viewModel.onRequestLocationPermissions) {
                val havePermissions =
                    permissionsManager.requestLocationPermissions()
                viewModel.onLocationPermissionsResult(havePermissions)
            }
        }
        lifecycleScope.launch {
            for (event in viewModel.flyIntoCurrentLocation) {
                flyIntoCurrentLocation()
            }
        }
        lifecycleScope.launch {
            viewModel.cameraState.collect { cameraState ->
                cameraState ?: return@collect
                mapView.getMapboxMap().setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(cameraState.longitude, cameraState.latitude))
                        .zoom(cameraState.zoom)
                        .bearing(cameraState.bearing)
                        .build()
                )
            }
        }
        lifecycleScope.launch {
            viewModel.isLocationEnabled.collect { enabled ->
                mapView.location.enabled = enabled
            }
        }
        lifecycleScope.launch {
            viewModel.friendsLocations.collect { locations ->
                updateFriendLocationAnnotations(locations)
            }
        }
        lifecycleScope.launch {
            viewModel.goToLocation.collect { location ->
                flyIntoLocation(location.longitude, location.latitude)
            }
        }
        lifecycleScope.launch {
            viewModel.isTrackingLocation.collect { tracking ->
                if (tracking) trackViewportToLocationPuck() else mapView.viewport.idle()
            }
        }
    }

    private fun updateFriendLocationAnnotations(locations: Iterable<Location>) {
        val annotationManager = mapView.viewAnnotationManager

        val (added, removed, updated) = locationDiffTool.diff(
            oldLocations,
            locations
        )
        oldLocations = locations

        val imageWidth = (resources.displayMetrics.density * 36).toInt()
        added.forEach { location ->
            val imageView = ImageView(requireContext()).apply {
                layoutParams = FrameLayout.LayoutParams(imageWidth, imageWidth)
                setImageResource(R.drawable.account_circle)
            }
            annotationManager.addViewAnnotation(
                imageView,
                options = viewAnnotationOptions {
                    geometry(Point.fromLngLat(location.longitude, location.latitude))
                    allowOverlap(true)
                }
            )
            imageView.setOnClickListener {
                viewModel.onFriendLocationClick(location)
            }
            userIdToViewAnnotationMap[location.userId] = imageView
        }

        removed.forEach { location ->
            annotationManager.removeViewAnnotation(
                userIdToViewAnnotationMap[location.userId]
                    ?: error("Location does not exist in locationToViewAnnotationMap")
            )
            userIdToViewAnnotationMap.remove(location.userId)
        }

        updated.forEach { location ->
            val imageViewToUpdate = userIdToViewAnnotationMap[location.userId]
                ?: error("Location does not exist in locationToViewAnnotationMap")
            annotationManager.updateViewAnnotation(imageViewToUpdate,
                viewAnnotationOptions {
                    geometry(Point.fromLngLat(location.longitude, location.latitude))
                }
            )
        }
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    private suspend fun goToCurrentLocation() {
        val locationPoint = locationProvider.getCurrentLocation().let {
            Point.fromLngLat(it.longitude, it.latitude)
        }

        mapView.camera.easeTo(
            CameraOptions.Builder()
                .center(locationPoint)
                .zoom(DEFAULT_ZOOM)
                .bearing(DEFAULT_BEARING)
                .build(),
            MapAnimationOptions.mapAnimationOptions {
                duration(GO_TO_LOCATION_ANIMATION_DURATION)
                interpolator(
                    DecelerateInterpolator(
                        GO_TO_LOCATION_ANIMATION_DECELERATION_FACTOR
                    )
                )
                animatorListener(MapUtils.onAnimationEndListener { viewModel.setTrackLocation(true) })
            })
    }

    private fun trackViewportToLocationPuck() {
        val viewportPlugin = mapView.viewport
        val followPuckViewportState: FollowPuckViewportState =
            viewportPlugin.makeFollowPuckViewportState(
                FollowPuckViewportStateOptions.Builder()
                    .bearing(
                        FollowPuckViewportStateBearing.Constant(
                            DEFAULT_BEARING
                        )
                    )
                    .pitch(DEFAULT_PITCH)
                    .zoom(DEFAULT_ZOOM)
                    .build()
            )
        viewportPlugin.transitionTo(
            followPuckViewportState,
            viewportPlugin.makeImmediateViewportTransition()
        )
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    private suspend fun flyIntoCurrentLocation() {
        val locationPoint = locationProvider.getCurrentLocation().let { location ->
            Point.fromLngLat(location.longitude, location.latitude)
        }
        val baseCameraOptions =
            CameraOptions.Builder().bearing(DEFAULT_BEARING).pitch(DEFAULT_PITCH)
                .center(locationPoint)

        // start off zoomed out
        mapView.getMapboxMap().setCamera(
            baseCameraOptions.zoom(FLY_INTO_ANIMATION_STARTING_ZOOM).build()
        )

        // zoom closer into the map using an animation, achieving the "fly into" current
        // location effect)
        mapView.camera.flyTo(
            baseCameraOptions.zoom(DEFAULT_ZOOM).build(),
            MapAnimationOptions.mapAnimationOptions {
                duration(FLY_INTO_ANIMATION_DURATION_MILLISECONDS)
                interpolator(DecelerateInterpolator(FLY_INTO_ANIMATION_DECELERATION_FACTOR))
                animatorListener(MapUtils.onAnimationEndListener { viewModel.setTrackLocation(true) })
            })
    }

    private fun flyIntoLocation(longitude: Double, latitude: Double) {
        // stop tracking location if location is being tracked, or the transition will not happen
        mapView.viewport.idle()
        mapView.camera.flyTo(
            CameraOptions.Builder().bearing(DEFAULT_BEARING).pitch(DEFAULT_PITCH)
                .center(Point.fromLngLat(longitude, latitude)).zoom(DEFAULT_ZOOM).build(),
            MapAnimationOptions.mapAnimationOptions {
                duration(FLY_INTO_ANIMATION_DURATION_MILLISECONDS)
                interpolator(DecelerateInterpolator(FLY_INTO_ANIMATION_DECELERATION_FACTOR))
            })
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