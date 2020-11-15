package io.github.loggerworld.domain.user_account

import io.github.loggerworld.domain.BaseEntity
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.AttributeOverride
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne

@Entity(name = "user_property")
@AttributeOverride(name = "id", column = Column(name = "user_property_id"))
data class UserProperty(

    @Lob
    @Column(name = "value")
    var value: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    var user: UserAccount? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_property_type_id")
    var propertyType: UserPropertyType? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,

    ) : BaseEntity<Int>()