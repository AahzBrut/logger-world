package io.github.loggerworld.service.domain

import io.github.loggerworld.domain.enums.PlayerAttributeEnum
import io.github.loggerworld.domain.enums.PlayerStatEnum
import io.github.loggerworld.domain.enums.statCalculators
import io.github.loggerworld.dto.inner.PlayerEffectiveStatData
import io.github.loggerworld.dto.inner.StatAttributeDependency
import io.github.loggerworld.repository.character.PlayerStatDependencyRepository
import io.github.loggerworld.util.LogAware
import org.springframework.stereotype.Service
import javax.transaction.Transactional


@Service
class PlayerAttributeDomainService(
    private val playerStatDependencyRepository: PlayerStatDependencyRepository,
) : LogAware {

    private lateinit var playerEffectiveStatData: PlayerEffectiveStatData

    @Transactional
    fun getAllStatsDependencies(): PlayerEffectiveStatData {

        val dependencies = playerStatDependencyRepository.findAll()
        val stats: MutableMap<PlayerStatEnum, MutableMap<PlayerAttributeEnum, StatAttributeDependency>> = mutableMapOf()

        dependencies.forEach { stat ->
            if (!stats.containsKey(stat.playerStat.code)) stats[stat.playerStat.code] = mutableMapOf()
            if (!stats[stat.playerStat.code]!!.containsKey(stat.playerAttribute.code)) stats[stat.playerStat.code]!![stat.playerAttribute.code] = StatAttributeDependency(stat.coeff, stat.calcType.code)
        }

        return PlayerEffectiveStatData(stats)
    }

    fun setPlayerEffectiveStatData(playerEffectiveStatData: PlayerEffectiveStatData) {
        this.playerEffectiveStatData = playerEffectiveStatData
    }

    fun getEffectiveStats(baseStats: Map<Byte, Float>, effectiveAttributes: Map<Byte, Float>) : Map<Byte, Float> {

        return baseStats.entries.associate {
            it.key to if (!playerEffectiveStatData.stats.containsKey(PlayerStatEnum.getById(it.key))) it.value else it.value + calcEffect(effectiveAttributes, PlayerStatEnum.getById(it.key))
        }
    }

    private fun calcEffect(effectiveAttributes: Map<Byte, Float>, stat: PlayerStatEnum): Float {
        val sum = playerEffectiveStatData.stats[stat]!!.entries.map {
            statCalculators[it.value.formula]!!.invoke(
                0f,
                effectiveAttributes[it.key.ordinal.toByte()]!!,
                it.value.coeff
            )
        }.sum()
        return sum
    }
}