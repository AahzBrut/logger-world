package io.github.loggerworld.domain.logging

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.LogTypes
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "log_type")
@AttributeOverride(name = "id", column = Column(name = "log_type_id"))
data class LogType(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_class_id")
    var logClass: LogClass = LogClass(),

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    var code: LogTypes = LogTypes.LOGIN,

    @Column(name = "num_vals")
    var numVals: Byte = 0

    ) : BaseEntity<Byte>() {

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "logType")
    var logTypeDescriptions: MutableList<LogTypeDescription> = mutableListOf()
}