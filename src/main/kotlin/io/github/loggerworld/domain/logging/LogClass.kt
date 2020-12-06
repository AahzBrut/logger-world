package io.github.loggerworld.domain.logging

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.LogClasses
import javax.persistence.AttributeOverride
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.OneToMany


@Entity(name = "log_class")
@AttributeOverride(name = "id", column = Column(name = "log_class_id"))
data class LogClass(

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    var code: LogClasses = LogClasses.SYSTEM,

    ) : BaseEntity<Byte>() {

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "logClass")
    var logTypes: MutableList<LogType> = mutableListOf()

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "logClass")
    var logClasDescriptions: MutableList<LogClassDescription> = mutableListOf()
}
