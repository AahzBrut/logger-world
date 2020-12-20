package io.github.loggerworld.domain.enums

enum class ItemCategories(val parent: ItemCategories?) {
    NOTHING(NOTHING),
    WEAPON(WEAPON),
    ARMOR(ARMOR),
    CONSUMABLE(CONSUMABLE),
    MELEE(WEAPON),
    ONE_HANDED(MELEE),
    SWORD(ONE_HANDED),
    SHORT_SWORD(SWORD);

    fun getAllParents() : Set<ItemCategories> {
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