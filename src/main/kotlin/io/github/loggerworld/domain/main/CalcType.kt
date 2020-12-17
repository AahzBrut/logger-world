package io.github.loggerworld.domain.main

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.enums.CalcTypes
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity(name = "calc_type")
@AttributeOverride(name = "id", column = Column(name = "calc_type_id"))
data class CalcType(

    @Enumerated(EnumType.STRING)
    @Column(name = "code")
    var code: CalcTypes = CalcTypes.ADD_MUL_COEFF,

    ) : BaseEntity<Byte>() {

    init {
        this.id = this.code.ordinal.toByte()
    }
}
