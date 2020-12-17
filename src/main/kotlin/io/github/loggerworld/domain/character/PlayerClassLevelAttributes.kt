package io.github.loggerworld.domain.character

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.main.ChangeType
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "class_level_attributes")
@AttributeOverride(name = "id", column = Column(name = "class_level_attributes_id"))
data class PlayerClassLevelAttributes(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_type_id")
    var changeType: ChangeType = ChangeType(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_class_id")
    var playerClass: PlayerClass = PlayerClass(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_attribute_id")
    var playerAttribute: PlayerAttribute = PlayerAttribute(),

    @Column(name = "level")
    var level: Byte = 0,

    @Column(name = "value")
    var value: Float = 0f
) : BaseEntity<Short>()
