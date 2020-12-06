package io.github.loggerworld.domain.logging

import io.github.loggerworld.domain.BaseEntity
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "log_type_message")
@AttributeOverride(name = "id", column = Column(name = "log_type_message_id"))
data class LogTypeMessage(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_type_id")
    var logType: LogType = LogType(),

    @Column(name = "ordinal")
    var ordinal: Byte = 0

): BaseEntity<Int>(){
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "logTypeMessage")
    var logTypeMessageDescriptions: MutableList<LogTypeMessageDescription> = mutableListOf()
}
