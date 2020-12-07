package io.github.loggerworld.domain.logging

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

@Entity(name = "log_class_description")
@AttributeOverride(name = "id", column = Column(name = "log_class_description_id"))
data class LogClassDescription(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_class_id")
    var logClass: LogClass = LogClass(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    var language: Language = Language(Languages.EN),

    @Column(name = "name")
    var name: String = "",

    @Lob
    @Column(name = "description")
    var description: String = "",

    ) : BaseEntity<Short>()
