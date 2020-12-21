package io.github.loggerworld.domain.enums

import io.github.loggerworld.domain.enums.ItemStatEnum.DURABILITY
import io.github.loggerworld.domain.enums.ItemStatEnum.MAX_DAMAGE
import io.github.loggerworld.domain.enums.ItemStatEnum.MAX_DURABILITY
import io.github.loggerworld.domain.enums.ItemStatEnum.MIN_DAMAGE
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

val emptyStats = emptySet<ItemStatEnum>()

enum class ItemCategories(
    private val parent: ItemCategories?,
    val isItem: Boolean,
    val applicableStats: Set<ItemStatEnum>
) {
    NOTHING(NOTHING, false, emptyStats),
    WEAPON(WEAPON, false, emptyStats),
    ARMOR(ARMOR, false, emptyStats),
    CONSUMABLE(CONSUMABLE, false, emptyStats),
    MELEE(WEAPON, false, emptyStats),
    ONE_HANDED(MELEE, false, emptyStats),
    SWORD(ONE_HANDED, false, emptyStats),
    SHORT_SWORD(
        SWORD, true, weaponStats
    );

    fun getAllParents(): Set<ItemCategories> {
        if (this == this.parent) return emptySet()
        val result = mutableSetOf<ItemCategories>()
        var currentNode = this.parent!!

        while (currentNode != currentNode.parent) {
            result.add(currentNode)
            currentNode = currentNode.parent!!
        }

        return result
    }
}