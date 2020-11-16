package io.github.loggerworld.domain.user_account

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.UserPropertyTypes
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity(name = "user_property_type")
@AttributeOverride(name = "id", column = Column(name = "user_property_type_id"))
data class UserPropertyType(

    @Column(name = "name")
    var name: String = "",

    @Column(name = "description")
    var description: String = "",

) : BaseEntity<Short>() {

    constructor(type: UserPropertyTypes) : this("", "") {
        this.id = type.ordinal.toShort()
    }

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "propertyType")
    var properties: MutableList<UserProperty> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "propertyType")
    var typeDescriptions: MutableList<UserPropertyTypeDescription> = mutableListOf()

}