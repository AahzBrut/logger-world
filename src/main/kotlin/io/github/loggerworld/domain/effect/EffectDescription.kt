package io.github.loggerworld.domain.effect

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.main.Language
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne

@Entity(name = "effect_description")
@AttributeOverride(name = "id", column = Column(name = "effect_description_id"))
data class EffectDescription(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "effect_id")
    var effect: Effect = Effect(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    var language: Language = Language(),

    @Column(name = "name")
    var name: String = "",

    @Lob
    @Column(name = "description")
    var description: String = "",

    ) : BaseEntity<Int>()
