package io.github.loggerworld.domain.user_account

import io.github.loggerworld.domain.BaseEntity
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

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "propertyType")
    var properties: List<UserProperty> = mutableListOf(),

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "propertyType")
    var typeDescriptions: List<UserPropertyTypeDescription> = mutableListOf()

) : BaseEntity<Short>()