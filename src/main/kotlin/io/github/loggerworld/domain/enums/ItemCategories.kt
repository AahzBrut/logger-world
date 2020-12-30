package io.github.loggerworld.domain.enums

import com.fasterxml.jackson.annotation.JsonValue
import io.github.loggerworld.domain.enums.ItemStatEnum.DURABILITY
import io.github.loggerworld.domain.enums.ItemStatEnum.MAX_DAMAGE
import io.github.loggerworld.domain.enums.ItemStatEnum.MAX_DURABILITY
import io.github.loggerworld.domain.enums.ItemStatEnum.MIN_DAMAGE
import io.github.loggerworld.domain.enums.ItemStatEnum.STACK_SIZE
import io.github.loggerworld.domain.enums.ItemStatEnum.USES_PER_DURABILITY
import io.github.loggerworld.domain.enums.ItemStatEnum.WEIGHT


val weaponStats = setOf(
    DURABILITY,
    MAX_DURABILITY,
    USES_PER_DURABILITY,
    WEIGHT,
    MIN_DAMAGE,
    MAX_DAMAGE
)

val goldStats = setOf(WEIGHT, STACK_SIZE)

val emptyStats = emptySet<ItemStatEnum>()

enum class ItemCategories(
    val parent: ItemCategories?,
    val isItem: Boolean,
    val applicableStats: Set<ItemStatEnum>
) {
    NOTHING(null, false, emptyStats),
    VALUABLES(null, false, emptyStats),
    WEAPON(null, false, emptyStats),
    ARMOR(null, false, emptyStats),
    CONSUMABLE(null, false, emptyStats),
    GOLD(VALUABLES, true, goldStats),
    MELEE(WEAPON, false, emptyStats),
    ONE_HANDED(MELEE, false, emptyStats),
    SWORD(ONE_HANDED, false, emptyStats),
    SHORT_SWORD(
        SWORD, true, weaponStats
    );

    fun getAllParents(): Set<ItemCategories> {
        if (this == this.parent) return emptySet()
        val result = mutableSetOf<ItemCategories>()
        var currentNode = this.parent

        while (currentNode != null) {
            result.add(currentNode)
            currentNode = currentNode.parent
        }

        return result
    }

    fun getChildItems(): List<ItemCategories> {
        if (this.isItem) return listOf(this)

        return values()
            .filter {
                it.isItem
            }
            .filter {
                it.getAllParents().contains(this)
            }
            .toList()
    }

    @JsonValue
    fun jsonValue() = ordinal
}