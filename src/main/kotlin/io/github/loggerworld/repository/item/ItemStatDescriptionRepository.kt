package io.github.loggerworld.repository.item

import io.github.loggerworld.domain.item.ItemStatDescription
import org.springframework.data.jpa.repository.JpaRepository

interface ItemStatDescriptionRepository: JpaRepository<ItemStatDescription, Short>
