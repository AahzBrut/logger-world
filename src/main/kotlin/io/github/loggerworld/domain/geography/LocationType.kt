package io.github.loggerworld.domain.geography

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.LocationTypes
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.OneToMany

@Entity(name = "location_type")
@AttributeOverride(name = "id", column = Column(name = "location_type_id"))
data class LocationType(

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    var code: LocationTypes

) : BaseEntity<Byte>() {

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "locationType")
    var locationTypeDescriptions: MutableList<LocationTypeDescription> = mutableListOf()

}