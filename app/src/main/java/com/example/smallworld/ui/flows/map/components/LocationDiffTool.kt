package com.example.smallworld.ui.flows.map.components

import com.example.smallworld.data.location.Location
import javax.inject.Inject

data class LocationsDiff(
    val added: Iterable<Location>,
    val removed: Iterable<Location>,
    val updated: Iterable<Location>
)

class LocationDiffTool @Inject constructor() {
    fun diff(oldLocations: Iterable<Location>, newLocations: Iterable<Location>): LocationsDiff {
        val oldUserIdToLocationsMap =
            hashMapOf(*oldLocations.map { it.userId to it }.toTypedArray())
        val newUserIdToLocationMap =
            hashMapOf(*newLocations.map { it.userId to it }.toTypedArray())

        val oldUserIds = oldUserIdToLocationsMap.keys
        val newUserIds = newUserIdToLocationMap.keys

        val addedUserIds = newUserIds - oldUserIds
        val removedUserIds = oldUserIds - newUserIds
        val updatedUserIds = (newUserIds intersect oldUserIds)
            .filter { newUserIdToLocationMap[it] != oldUserIdToLocationsMap[it] }

        return LocationsDiff(
            added = addedUserIds.mapNotNull { newUserIdToLocationMap[it] },
            removed = removedUserIds.mapNotNull { oldUserIdToLocationsMap[it] },
            updated = updatedUserIds.mapNotNull { newUserIdToLocationMap[it] }
        )
    }
}