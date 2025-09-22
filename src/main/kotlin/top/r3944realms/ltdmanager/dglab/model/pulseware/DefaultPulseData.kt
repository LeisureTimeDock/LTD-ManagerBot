package top.r3944realms.ltdmanager.dglab.model.pulseware

import com.r3944realms.dg_lab.api.message.data.PulseWave
import com.r3944realms.dg_lab.api.message.data.PulseWaveList

object DefaultPulseData {

    fun allPulseWaveLists(): Map<String, PulseWaveList> {
        return mapOf(
            "呼吸" to Breath,
            "潮汐" to Tide,
            "连击" to Combo,
            "快速按捏" to FastPinch,
            "按捏渐强" to PinchGradual,
            "心跳节奏" to Heartbeat,
            "压缩" to Compress,
            "节奏步伐" to RhythmStep,
            "颗粒摩擦" to GranularFriction,
            "渐变弹跳" to GradualBounce,
            "波浪涟漪" to WaveRipple,
            "雨水冲刷" to RainWash,
            "变速敲击" to SpeedHit,
            "信号灯" to SignalLight,
            "挑逗1" to Tease1,
            "挑逗2" to Tease2
        )
    }

    val Breath: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "呼吸"

        // 每段频率和强度
        val segments = listOf(
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 5, 10, 20)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(20, 25, 30, 40)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(40, 45, 50, 60)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(60, 65, 70, 80)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0))
        )

        // 转成 PulseWave 并加入列表
        for (seg in segments) {
            list.add(PulseWave.fromArrays(seg[0], seg[1]))
        }

        list
    }
    val Tide: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "潮汐"
        val segments = listOf(
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 4, 8, 17)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(17, 21, 25, 33)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(50, 50, 50, 50)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(50, 54, 58, 67)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(67, 71, 75, 83)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 98, 96, 92)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(92, 90, 88, 84)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(84, 82, 80, 76)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(68, 68, 68, 68))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }

    val Combo: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "连击"
        val segments = listOf(
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 92, 84, 67)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(67, 58, 50, 33)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 1)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(2, 2, 2, 2))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }
    val FastPinch: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "快速按捏"
        val segments = listOf(
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }
    val PinchGradual: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "按捏渐强"
        val segments = listOf(
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(29, 29, 29, 29)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(52, 52, 52, 52)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(2, 2, 2, 2)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(73, 73, 73, 73)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(87, 87, 87, 87)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }

    val Heartbeat: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "心跳节奏"
        val segments = listOf(
            arrayOf(intArrayOf(110, 110, 110, 110), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(110, 110, 110, 110), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(75, 75, 75, 75)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(75, 77, 79, 83)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(83, 85, 88, 92)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }
    val Compress: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "压缩"
        val segments = listOf(
            arrayOf(intArrayOf(25, 25, 24, 24), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(24, 23, 23, 23), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(22, 22, 22, 21), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(21, 21, 20, 20), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(20, 19, 19, 19), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(18, 18, 18, 17), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(17, 16, 16, 16), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(15, 15, 15, 14), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(14, 14, 13, 13), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(13, 12, 12, 12), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(11, 11, 11, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }
    val RhythmStep: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "节奏步伐"
        val segments = listOf(
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 5, 10, 20)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(20, 25, 30, 40)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(40, 45, 50, 60)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(60, 65, 70, 80)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 6, 12, 25)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(25, 31, 38, 50)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(50, 56, 62, 75)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 8, 16, 33)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(33, 42, 50, 67)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 12, 25, 50)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }

    val GranularFriction: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "颗粒摩擦"
        val segments = listOf(
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }

    val GradualBounce: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "渐变弹跳"
        val segments = listOf(
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(1, 1, 1, 1)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(1, 9, 18, 34)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(34, 42, 50, 67)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(0, 0, 0, 0), intArrayOf(0, 0, 0, 0))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }
    val WaveRipple: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "波浪涟漪"
        val segments = listOf(
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(1, 1, 1, 1)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(1, 3, 7, 13)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(13, 25, 40, 60)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(60, 75, 90, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(50, 50, 50, 50)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }

    val RainWash: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "雨水冲刷"
        val segments = listOf(
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 5, 15, 30)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(40, 50, 60, 70)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(80, 90, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }

    val SpeedHit: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "变速敲击"
        val segments = listOf(
            arrayOf(intArrayOf(15, 15, 15, 15), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(20, 20, 20, 20), intArrayOf(50, 50, 50, 50)),
            arrayOf(intArrayOf(25, 25, 25, 25), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(20, 20, 20, 20), intArrayOf(50, 50, 50, 50)),
            arrayOf(intArrayOf(15, 15, 15, 15), intArrayOf(0, 0, 0, 0))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }
    val SignalLight: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "信号灯"
        val segments = listOf(
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 0, 0, 0)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 100, 100, 100))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }

    val Tease1: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "挑逗1"
        val segments = listOf(
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 30, 60, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 70, 40, 0))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }

    val Tease2: PulseWaveList by lazy {
        val list = PulseWaveList()
        list.name = "挑逗2"
        val segments = listOf(
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(0, 50, 100, 100)),
            arrayOf(intArrayOf(10, 10, 10, 10), intArrayOf(100, 50, 0, 0))
        )
        segments.forEach { list.add(PulseWave.fromArrays(it[0], it[1])) }
        list
    }
}