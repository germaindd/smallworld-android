package com.example.smallworld.ui.flows.map

import android.animation.Animator
import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import com.example.smallworld.R
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D

object MapUtils {
    fun getLocationPuck(context: Context) = LocationPuck2D(
        bearingImage = AppCompatResources.getDrawable(
            context, R.drawable.map_screen_location_puck
        ), scaleExpression = interpolate {
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

    fun onAnimationEndListener(onEnd: () -> Unit) = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}

        override fun onAnimationEnd(animation: Animator) {
            onEnd()
        }

        override fun onAnimationCancel(animation: Animator) {}

        override fun onAnimationRepeat(animation: Animator) {}
    }
}
