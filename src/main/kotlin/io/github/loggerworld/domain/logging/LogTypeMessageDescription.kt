package io.github.loggerworld.domain.logging

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.Languages
import io.github.loggerworld.domain.main.Language
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "log_type_message_description")
@AttributeOverride(name = "id", column = Column(name = "log_type_message_description_id"))
data class LogTypeMessageDescription(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_type_message_id")
    var logTypeMessage: LogTypeMessage = LogTypeMessage(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    var language: Language = Language(Languages.EN),

    @Column(name = "value")
    var value: String,

    ) : BaseEntity<Int>()
