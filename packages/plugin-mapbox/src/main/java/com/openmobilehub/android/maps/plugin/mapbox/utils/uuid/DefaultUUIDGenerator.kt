package com.openmobilehub.android.maps.plugin.mapbox.utils.uuid

import java.util.UUID

class DefaultUUIDGenerator : UUIDGenerator {
    override fun generate(): UUID {
        return UUID.randomUUID()
    }
}
