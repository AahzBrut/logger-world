package io.github.loggerworld.domain.logging

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.character.Player
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "log_entry")
@AttributeOverride(name = "id", column = Column(name = "log_entry_id"))
data class LogEntry(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_type_message_id")
    var logTypeMessage: LogTypeMessage = LogTypeMessage(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    var player: Player = Player(),

    @CreationTimestamp
    @Column(name = "created")
    var createdAt: LocalDateTime? = null,

    ) : BaseEntity<Long>() {

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "logEntry")
    var logEntryVals: MutableList<LogEntryVals> = mutableListOf()
}
