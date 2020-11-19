package io.github.loggerworld.domain.main

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.geography.LocationDescription
import io.github.loggerworld.domain.geography.LocationTypeDescription
import io.github.loggerworld.domain.user_account.UserAccount
import io.github.loggerworld.domain.user_account.UserPropertyTypeDescription
import io.github.loggerworld.domain.user_account.UserStatusDescription
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.OneToMany


@Entity(name = "language")
@AttributeOverride(name = "id", column = Column(name = "language_id"))
data class Language(

    @Enumerated(EnumType.STRING)
    @Column(name = "language_code")
    var code: Languages = Languages.EN,

    @Column(name = "language_name")
    var name: String = ""

) : BaseEntity<Byte>() {

    constructor(language: Languages) : this(Languages.EN, "") {
        this.id = language.ordinal.toByte()
    }

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "language")
    var users: List<UserAccount> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "language")
    var userStatusDescriptions: List<UserStatusDescription> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "language")
    var userPropertyTypeDescriptions: List<UserPropertyTypeDescription> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "language")
    var locationDescriptions: List<LocationDescription> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "language")
    var locationTypeDescriptions: List<LocationTypeDescription> = mutableListOf()

}