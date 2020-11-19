package io.github.loggerworld.domain.geography

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.LocationTypes
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "location")
@AttributeOverride(name = "id", column = Column(name = "location_id"))
data class Location(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_type_id")
    var locationType: LocationType = LocationType(LocationTypes.VOID),

    @Column(name = "xcoord")
    var xCoord: Byte = -1,

    @Column(name = "ycoord")
    var yCoord: Byte = -1,

) : BaseEntity<Short>() {

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "location")
    var locationDescriptions: MutableList<LocationDescription> = mutableListOf()

}