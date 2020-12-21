package io.github.loggerworld.domain.item

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.ItemStatEnum
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity(name = "item_stat")
@AttributeOverride(name = "id", column = Column(name = "item_stat_id"))
data class ItemStat(

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    var code: ItemStatEnum = ItemStatEnum.NONE,

    ) : BaseEntity<Byte>() {

        init {
            this.id = code.ordinal.toByte()
        }
    }
