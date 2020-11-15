package io.github.loggerworld.domain.user_account

import io.github.loggerworld.domain.BaseEntity
import io.github.loggerworld.domain.main.Language
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

@Entity(name = "user_account")
@AttributeOverride(name = "id", column = Column(name = "user_account_id"))
data class UserAccount(

    @Column(name = "login_name")
    var loginName: String = "",

    @Column(name = "password")
    var password: String = "",

    @Column(name = "display_name")
    var displayName: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_status_id")
    var status: UserStatus? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    var language: Language? = null,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null,

    @Column(name = "last_login_at")
    var lastLoginAt: LocalDateTime? = null,

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user")
    var properties: List<UserProperty> = mutableListOf()

    ) : BaseEntity<Long>()