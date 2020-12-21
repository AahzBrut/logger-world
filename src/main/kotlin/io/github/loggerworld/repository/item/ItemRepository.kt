package io.github.loggerworld.repository.item

import io.github.loggerworld.domain.item.Item
import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository: JpaRepository<Item, Long>
