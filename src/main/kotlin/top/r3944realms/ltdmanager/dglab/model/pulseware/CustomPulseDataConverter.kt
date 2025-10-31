package top.r3944realms.ltdmanager.dglab.model.pulseware

import com.r3944realms.dg_lab.api.message.data.PulseWave
import com.r3944realms.dg_lab.api.message.data.PulseWaveList

object CustomPulseDataConverter {

    /**
     * 将频率转换为 Dg-Lab 格式
     *
     * @param frequency 频率值
     * @return Dg-Lab 格式的数字
     */
    private fun convertFrequency(frequency: Int): Int {
        return when {
            frequency <= 10 -> 10
            frequency <= 100 -> frequency
            frequency <= 600 -> (frequency - 100) / 5 + 100
            frequency <= 1000 -> (frequency - 600) / 10 + 200
            else -> 10
        }
    }

    /**
     * 将频率数组转换为 Dg-Lab 格式
     *
     * @param frequencies 频率数组
     * @return 转换后的频率数组
     */
    private fun convertFrequencies(frequencies: IntArray): IntArray {
        return IntArray(4) { index ->
            if (index < frequencies.size) {
                convertFrequency(frequencies[index])
            } else {
                10 // 默认值
            }
        }
    }

    /**
     * 将自定义波形数据转换为 PulseWaveList
     *
     * @param customPulseData Map<String, List<Array<IntArray>>>
     * 每个 int[][] 包含两个长度为 4 的 int 数组，第一个是 frequencies，第二个是 strengths
     * @return Map<String, PulseWaveList>
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

                // 转换频率为 Dg-Lab 格式
                val convertedFreqs = convertFrequencies(freqs)

                val wave = PulseWave.fromArrays(convertedFreqs, strengths)
                waveList.add(wave)
            }

            pulseWaveLists[name] = waveList
        }

        return pulseWaveLists
    }

    /**
     * 转换单个 PulseWave 的频率
     */
    private fun convertPulseWaveFrequencies(pulseWave: PulseWave): PulseWave {
        val freqs = intArrayOf(
            convertFrequency(pulseWave.f1()),
            convertFrequency(pulseWave.f2()),
            convertFrequency(pulseWave.f3()),
            convertFrequency(pulseWave.f4())
        )
        val strengths = intArrayOf(
            pulseWave.s1(),
            pulseWave.s2(),
            pulseWave.s3(),
            pulseWave.s4()
        )
        return PulseWave.fromArrays(freqs, strengths)
    }

    /**
     * 转换整个 PulseWaveList 的频率
     */
    fun convertPulseWaveListFrequencies(pulseWaveList: PulseWaveList): PulseWaveList {
        val convertedList = PulseWaveList()
        convertedList.name = pulseWaveList.name

        for (i in 0 until pulseWaveList.list.size) {
            val convertedWave = convertPulseWaveFrequencies(pulseWaveList.list[i])
            convertedList.add(convertedWave)
        }

        return convertedList
    }

    fun PulseWave.toSerializable(): PulseWaveSerializable =
        PulseWaveSerializable(
            convertFrequency(f1()),
            convertFrequency(f2()),
            convertFrequency(f3()),
            convertFrequency(f4()),
            s1(), s2(), s3(), s4()
        )

    private fun PulseWaveSerializable.toPulseWave(): PulseWave =
        PulseWave.fromArrays(
            intArrayOf(convertFrequency(f1), convertFrequency(f2), convertFrequency(f3), convertFrequency(f4)),
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