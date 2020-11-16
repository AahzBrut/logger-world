package io.github.loggerworld.domain.user_account

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.UserStatuses
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity(name = "user_status")
@AttributeOverride(name = "id", column = Column(name = "user_status_id"))
data class UserStatus(

    @Column(name = "name")
    var name: String = "",

    @Column(name = "description")
    var description: String = ""

) : BaseEntity<Byte>() {

    constructor(userStatus: UserStatuses) : this("", "") {
        this.id = userStatus.ordinal.toByte()
    }

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "status")
    var users: MutableList<UserAccount> = mutableListOf()
}