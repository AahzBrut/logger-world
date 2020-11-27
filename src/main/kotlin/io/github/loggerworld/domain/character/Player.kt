package io.github.loggerworld.domain.character

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.PlayerClasses
import io.github.loggerworld.domain.geography.Location
import io.github.loggerworld.domain.user_account.UserAccount
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "character")
@AttributeOverride(name = "id", column = Column(name = "character_id"))
data class Player(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    var userAccount: UserAccount = UserAccount(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_class_id")
    var playerClass: PlayerClass = PlayerClass(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    var location: Location = Location(),

    @Column(name = "name")
    var name: String = "",

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,

    ) : BaseEntity<Long>() {

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "player")
    var playerStats: MutableList<PlayerStats> = mutableListOf()
}