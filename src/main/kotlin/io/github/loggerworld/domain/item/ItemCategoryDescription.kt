package io.github.loggerworld.domain.item

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.ItemCategories
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.geography.Location
import io.github.loggerworld.domain.main.Language
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne

@Entity(name = "item_category_description")
@AttributeOverride(name = "id", column = Column(name = "item_category_description_id"))
data class ItemCategoryDescription(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_category_id")
    var itemCategory: ItemCategory = ItemCategory(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    var language: Language = Language(Languages.EN),

    @Column(name = "name")
    var name: String = "",

    @Lob
    @Column(name = "description")
    var description: String = "",

    ) : BaseEntity<Short>()
