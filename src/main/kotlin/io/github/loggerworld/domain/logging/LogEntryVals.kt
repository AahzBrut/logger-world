package io.github.loggerworld.domain.logging

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.character.Player
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "log_entry_vals")
@AttributeOverride(name = "id", column = Column(name = "log_entry_vals_id"))
data class LogEntryVals(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_entry_id")
    var logEntry: LogEntry = LogEntry(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    var player: Player = Player(),

    @Column(name = "ordinal")
    var ordinal: Byte = 0,

    @Column(name = "value")
    var value: String,

) : BaseEntity<Long>()
