package io.github.loggerworld.domain.character

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.PlayerStatEnum
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.OneToMany

@Entity(name = "character_stat")
@AttributeOverride(name = "id", column = Column(name = "character_stat_id"))
data class PlayerStat(

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    var code: PlayerStatEnum = PlayerStatEnum.NOTHING


) : BaseEntity<Byte>(){

    init {
        this.id = this.code.ordinal.toByte()
    }

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "playerStat")
    var playerStatDescriptions: MutableList<PlayerStatDescription> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "playerStat")
    var playerStats: MutableList<PlayerStats> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "playerStat")
    var levelStats: MutableList<PlayerClassLevelStats> = mutableListOf()
}