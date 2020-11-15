package io.github.loggerworld.domain.user_account

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.main.Language
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "user_property_type_description")
@AttributeOverride(name = "id", column = Column(name = "user_property_type_description_id"))
class UserPropertyTypeDescription(

    @Column(name = "name")
    var name: String = "",

    @Column(name = "description")
    var description: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    var language: Language? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_property_type_id")
    var propertyType: UserPropertyType? = null

    ) : BaseEntity<Int>()