package io.github.loggerworld.domain.item

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.main.Language
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne

@Entity(name = "item_quality_description")
@AttributeOverride(name = "id", column = Column(name = "item_quality_description_id"))
data class ItemQualityDescription(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_quality_id")
    var itemQuality: ItemQuality = ItemQuality(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    var language: Language = Language(Languages.EN),

    @Column(name = "name")
    var name: String = "",

    @Lob
    @Column(name = "description")
    var description: String = "",

    ) : BaseEntity<Short>()
