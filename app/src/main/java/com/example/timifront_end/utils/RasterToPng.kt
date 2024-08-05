package com.example.timifront_end.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Base64
import android.util.Log
import com.example.timifront_end.PopUpActivity


data class RasterImageString(val data: String, val width: Int, val height: Int)

fun convertRasterToPng(raster: RasterImageString): Result<Bitmap> {
    val isAscii = raster.data.all { it.code in 0x00..0x7F }
    if (!isAscii) {
        throw IllegalArgumentException("Only ASCII characters are supported")
    }

    val bytes = Base64.decode(raster.data, Base64.DEFAULT)
    val bits = u8VecToBits(bytes)
    val image = Bitmap.createBitmap(raster.width, raster.height, Bitmap.Config.ARGB_8888)
    image.eraseColor(Color.WHITE)

    for (x in 0 until raster.width) {
        for (y in 0 until raster.height) {
            val index = (x + y * raster.width)
            if (!bits[index]) {
                continue
            }
            val color = Color.BLACK
            image.setPixel(x, y, color)
        }
    }

    return Result.success(image)
}

fun u8VecToBits(bytes: ByteArray): BooleanArray {
    val bits = BooleanArray(bytes.size * 8)
    for (i in bytes.indices) {
        val b = bytes[i].toInt() and 0xff
        for (j in 0 until 8) {
            bits[i * 8 + j] = (b and (1 shl (7 - j))) != 0
        }
    }
    return bits
}

fun getPngFromRaster(raster: RasterImageString): ByteArray {
    Log.d("HTTP", "Converting raster to PNG")
    val stream = java.io.ByteArrayOutputStream()
    val result = convertRasterToPng(raster).getOrThrow()
    val png = result.compress(Bitmap.CompressFormat.PNG, 100, stream)
    if (!png) {
        throw Exception("Failed to compress bitmap")
    }
    PopUpActivity.mutableBitmap.value = result
    return stream.toByteArray()
}