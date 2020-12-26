package io.github.loggerworld.ecs.system

import com.badlogic.ashley.core.EntitySystem
import io.github.loggerworld.ecs.EngineSystems
import io.github.loggerworld.ecs.component.EquipmentChangeComponent
import io.github.loggerworld.ecs.component.EquipmentComponent
import io.github.loggerworld.ecs.component.InventoryChangedComponent
import io.github.loggerworld.ecs.component.InventoryComponent
import io.github.loggerworld.ecs.component.ItemComponent
import io.github.loggerworld.ecs.component.PlayerComponent
import io.github.loggerworld.ecs.component.PlayerMapComponent
import io.github.loggerworld.messagebus.EventBus
import io.github.loggerworld.messagebus.event.PlayerEquipItemCommand
import io.github.loggerworld.util.LogAware
import io.github.loggerworld.util.logger
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.get
import org.springframework.stereotype.Service

@Service
class EquipmentSystem(
    private val equipItemCommandBus: EventBus<PlayerEquipItemCommand>,
) : EntitySystem(EngineSystems.EQUIPMENT_SYSTEM.ordinal), LogAware {

    private val playerMap by lazy { engine.getEntitiesFor(allOf(PlayerMapComponent::class).get())[0][PlayerMapComponent.mapper]!!.playerMap }

    override fun update(deltaTime: Float) {
        while (equipItemCommandBus.receiveEvent { command ->
                val player = playerMap[command.playerId]
                val inventoryComp = player[InventoryComponent.mapper]!!
                val equipmentComp = player[EquipmentComponent.mapper]!!

                val item = inventoryComp.slots.find { it[ItemComponent.mapper]?.id == command.itemId }
                val itemInSlot = equipmentComp.equipmentSlots[command.slotType]
                if (item != null) {
                    if (itemInSlot != null) {
                        inventoryComp.slots.add(itemInSlot)
                        equipmentComp.equipmentSlots.remove(command.slotType)
                    }
                    inventoryComp.slots.remove(item)
                    equipmentComp.equipmentSlots[command.slotType] = item
                    logger().debug("\n\nPlayer: ${player[PlayerComponent.mapper]!!.playerName} equipped Item: ${item[ItemComponent.mapper]!!.category} in Slot: ${command.slotType} ")
                }
                player.addComponent<InventoryChangedComponent>(engine)
                player.addComponent<EquipmentChangeComponent>(engine)
            }) {
            // Empty block
        }
    }
}