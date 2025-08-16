package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MessageElement(
    val type: MessageType,
    val data: Message? = null
) {
    companion object {
        fun text(text: String): MessageElement = MessageElement(MessageType.Text, TextMessage(text))
        fun at(qq: ID, name: String?): MessageElement = MessageElement(MessageType.At, AtMessage(qq, name))
        fun image(file: String, summary: String?): MessageElement = MessageElement(MessageType.Image, ImageMessage(file, summary))
        fun json(json: String): MessageElement = MessageElement(MessageType.JSON, JSONMessage(json))
        fun face(id: Int): MessageElement = MessageElement(MessageType.Face, FaceMessage(id))
        fun record(file: String): MessageElement = MessageElement(MessageType.Record, RecordMessage(file))
        fun markdown(text: String): MessageElement = MessageElement(MessageType.Record, RecordMessage(text))
        fun video(video: String): MessageElement = MessageElement(MessageType.Video, VideoMessage(video))
        fun reply(id: ID, text: String):List<MessageElement> = ArrayList<MessageElement>(2).apply{
            add(MessageElement(MessageType.Reply, ReplyMessage(id)))
            add(text(text))
        }
        fun qqMusic(id: String): MessageElement = MessageElement(MessageType.Music, MusicMessage(MusicMessage.Type.QQ, id))
        fun neteaseMusic(id: String): MessageElement = MessageElement(MessageType.Music, MusicMessage(MusicMessage.Type.NETEASE, id))
        fun customMusic(url: String, audio: String, title: String, image: String): MessageElement = MessageElement(MessageType.Music, CustomMusicMessage(CustomMusicMessage.Type.Custom, url, audio, title, image))
        fun dice(): MessageElement = MessageElement(MessageType.Dice)
        fun rps(): MessageElement = MessageElement(MessageType.Rps)

    }
    @Serializable
    abstract class Message

    /**
     * 文本
     */
    @Serializable
    data class TextMessage(
        val text: String
    ) : Message()

    /**
     * At
     */
    @Serializable
    data class AtMessage(
        val qq: ID,
        val name: String? = null
    ) : Message() {
        fun isAtAll():Boolean {
            if (qq is ID.StringValue) {
                return qq.value == "all"
            }
            return false
        }
    }

    /**
     * 图片
     */
    @Serializable
    data class ImageMessage(
        val file: String,
        val summary: String? = "[图片]"
    ) : Message()

    /**
     * JSON
     */
    @Serializable
    data class JSONMessage(
        val data: String
    ) : Message()

    /**
     * 语音 && MarkDown(在其它/保留接口里)
     */
    @Serializable
    data class RecordMessage(
        val file: String
    ) : Message()
    /**
     * 视频
     */
    @Serializable
    data class VideoMessage(
        val file: String
    ) : Message()
    /**
     * 系统表情
     */
    @Serializable
    data class FaceMessage(
        val id: Int
    ) : Message()

    /**
     * 回复
     */
    @Serializable
    data class ReplyMessage(
        val id: ID
    ) : Message()

    /**
     * 音乐
     */
    @Serializable
    data class MusicMessage(
        val type: Type,
        val id: String,
    ): Message() {
        @Serializable
        enum class Type(val string: String) {
            @SerialName("163")NETEASE("netease"),
            @SerialName("qq")QQ("qq")
        }
    }
    /**
     * 音乐
     */
    @Serializable
    data class CustomMusicMessage(
        val type: Type,
        /**
         * 链接
         */
        val url: String,
        /**
         * 音频
         */
        val audio: String,
        /**
         * 标题
         */
        val title: String,
        /**
         * 图片
         */
        val image: String,
    ) : Message() {
        @Serializable
        enum class Type(val string: String) {
            @SerialName("custom")Custom("custom")
        }
    }

    @Serializable
    data class DiceMessage (
        /**
         * 该参数暂不可用
         */
        val result: Type
    ) : Message() {
        @Serializable
        enum class Type(val value: Int) {
            @SerialName("1")ONE(1),
            @SerialName("2")TWO(2),
            @SerialName("3")THREE(3),
            @SerialName("4")FOUR(4),
            @SerialName("5")FIVE(5),
            @SerialName("6")SIX(6),
        }
    }
}