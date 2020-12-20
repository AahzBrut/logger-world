package io.github.loggerworld.domain.item

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.ItemQualities
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity(name = "item_quality")
@AttributeOverride(name = "id", column = Column(name = "item_quality_id"))
data class ItemQuality(

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    var code: ItemQualities = ItemQualities.NONE,

    ): BaseEntity<Byte>()
