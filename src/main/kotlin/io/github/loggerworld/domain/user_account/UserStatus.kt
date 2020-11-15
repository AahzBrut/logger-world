package io.github.loggerworld.domain.user_account

import io.github.loggerworld.domain.BaseEntity
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
    var description: String = "",

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "status")
    var users: List<UserAccount> = mutableListOf()

) : BaseEntity<Byte>()