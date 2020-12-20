package io.github.loggerworld.domain.item

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.ItemCategories
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity(name = "item_category")
@AttributeOverride(name = "id", column = Column(name = "item_category_id"))
data class ItemCategory(

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    var code: ItemCategories = ItemCategories.NOTHING,

    ) : BaseEntity<Short>()
