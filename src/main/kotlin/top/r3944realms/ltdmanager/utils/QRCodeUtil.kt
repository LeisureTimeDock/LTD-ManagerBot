package top.r3944realms.ltdmanager.utils

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitArray
import com.google.zxing.common.BitMatrix
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.imageio.ImageIO

object QRCodeUtil {
    private const val CHARSET = "utf-8"
    private const val FORMAT = "png"
    @Throws(IOException::class, WriterException::class)
    fun generateQRCode(string: String?): InputStream {
        return generateQRCode(string, 256, 256)
    }

    @Throws(IOException::class, WriterException::class)
    fun generateQRCode(text: String?, width: Int, height: Int): ByteArrayInputStream {
        val hints: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = CHARSET

        // 创建二维码编码器
        val bitMatrix: BitMatrix =
            MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints)

        // 将BitMatrix转换为BufferedImage
        val image = toBufferedImage(bitMatrix)

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, FORMAT, outputStream)

        return ByteArrayInputStream(outputStream.toByteArray()) // 返回 ByteArrayInputStream
    }

    fun toBufferedImage(matrix: BitMatrix): BufferedImage {
        val width: Int = matrix.width
        val height: Int = matrix.height
        val image = BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY)
        val onColor = -0x1000000
        val offColor = -0x1
        val rowPixels = IntArray(width)
        var row: BitArray = BitArray(width)
        for (y in 0 until height) {
            row = matrix.getRow(y, row)
            for (x in 0 until width) {
                rowPixels[x] = if (row.get(x)) onColor else offColor
            }
            image.setRGB(0, y, width, 1, rowPixels, 0, width)
        }
        return image
    }
}