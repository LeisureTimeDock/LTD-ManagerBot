package top.r3944realms.ltdmanager.dglab.model.pulseware

import com.r3944realms.dg_lab.api.message.data.PulseWave
import com.r3944realms.dg_lab.api.message.data.PulseWaveList


object CustomPulseDataConverter {
    /**
     * 将自定义波形数据转换为 PulseWaveList
     *
     * @param customPulseData Map<String></String>, List<int></int>[][]>>
     * 每个 int[][] 包含两个长度为 4 的 int 数组，第一个是 frequencies，第二个是 strengths
     * @return Map<String></String>, PulseWaveList>
     */
    fun convert(customPulseData: Map<String, List<Array<IntArray>>>): Map<String, PulseWaveList> {
        val pulseWaveLists: MutableMap<String, PulseWaveList> = HashMap()

        for ((name, operations) in customPulseData) {
            val waveList = PulseWaveList()
            waveList.name = name

            for (op in operations) {
                val freqs = op[0]
                val strengths = op[1]

                // 确保每个数组长度为4
                require(!(freqs.size != 4 || strengths.size != 4)) { "每个波形段必须包含 4 个频率和 4 个强度值" }

                val wave = PulseWave.fromArrays(freqs, strengths)
                waveList.add(wave)
            }

            pulseWaveLists[name] = waveList
        }

        return pulseWaveLists
    }
    fun PulseWave.toSerializable(): PulseWaveSerializable =
        PulseWaveSerializable(f1(), f2(), f3(), f4(), s1(), s2(), s3(), s4())

    fun PulseWaveSerializable.toPulseWave(): PulseWave =
        PulseWave.fromArrays(
            intArrayOf(f1, f2, f3, f4),
            intArrayOf(s1, s2, s3, s4)
        )

    fun PulseWaveList.toSerializable(): PulseWaveListSerializable =
        PulseWaveListSerializable(name, list.map { it.toSerializable() }.toMutableList())

    fun PulseWaveListSerializable.toPulseWaveList(): PulseWaveList {
        val listObj = PulseWaveList()
        listObj.setName(name)
        list.forEach { listObj.add(it.toPulseWave()) }
        return listObj
    }
}