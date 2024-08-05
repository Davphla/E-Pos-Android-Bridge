package com.example.timifront_end.service.HttpServer

import android.content.Context
import android.util.Log
import com.example.timifront_end.MainActivity
import com.example.timifront_end.PopUpActivity
import com.example.timifront_end.data.source.local.TokenData
import com.example.timifront_end.utils.RasterImageString
import com.example.timifront_end.utils.getPngFromRaster
import com.hp.jipp.encoding.IppPacket
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.KtorCIO
import org.http4k.server.asServer
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader


fun sendData(email: String, stream: ByteArray, token: String): okhttp3.Response {
    Log.d("HTTP", "Sending data to server")
    val client = OkHttpClient()

    val body = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("timiAddress", email)
        .addFormDataPart(
            "file", "ticket.png", MultipartBody.create(
                okhttp3.MediaType.parse("image/png"),
                stream
            )
        )
        .build()

    Log.d("Token", token)
    if (token == "") {
        throw Exception("No token")
    }

    val request = okhttp3.Request.Builder()
        .url("https://api.mytimi.fr/shop/ticket")
        .addHeader("Authorization", "Bearer $token")
        .post(body)
        .build()
    Log.d("HTTP", "Sending request: $request")
    Log.d("HTTP", "Request body: ${request.body().toString()}")
    val res = client.newCall(request).execute()
    Log.d("HTTP", "Response: ${res.code()} ${res.message()}")
    return res
}

private fun decodeXml(xml: String): RasterImageString {
    val factory = XmlPullParserFactory.newInstance()
    factory.isNamespaceAware = true
    val parser = factory.newPullParser()

    parser.setInput(StringReader(xml))
    var eventType = parser.eventType
    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG) {
            if (parser.name == "image") {
                val width = parser.getAttributeValue(null, "width")
                val height = parser.getAttributeValue(null, "height")
                Log.d("XML", "Image width: $width, height: $height")
                val image = parser.nextText()
                return RasterImageString(image, width.toInt(), height.toInt())
            }
        }
        eventType = parser.next()
    }
    return RasterImageString("", 0, 0)
}

fun checkAndSendData(image: RasterImageString, getToken: () -> String): Response {
    val imageByteArray = getPngFromRaster(image)

    while (true) {
        MainActivity.mutable_email.value = ""
        while (MainActivity.mutable_email.value == "") {
        }
        PopUpActivity.mutableError.value = ""
        val email = MainActivity.mutable_email.value
        Log.d("XML", "Email is $email")
        PopUpActivity.mutableResponse.intValue = 1
        val res = sendData(email, imageByteArray, getToken())
        if (res.isSuccessful)
            break;
        Log.d("Send data", "Not successful ${res.message()}")
        PopUpActivity.mutableResponse.intValue = 0
        PopUpActivity.mutableError.value = "Error : Wrong email"
    }
    PopUpActivity.mutableResponse.intValue = 2
    return Response(Status.OK)
        .header("Content-Type", "text/html")
        .body("""<div><response success=true code="200"/></div>""")
}

fun parseIppRequest(ippRequest: String): Map<String, String> {
    val attributes = mutableMapOf<String, String>()

    ippRequest.split("\n").forEach { line ->
        val match = Regex("^([^=]+)=(.*)$").find(line)
        if (match != null) {
            val attributeName = match.groupValues[1]
            val attributeValue = match.groupValues[2]
            attributes[attributeName] = attributeValue
        }
    }

    return attributes
}


val responseBody = """
    {
        "attributes": {
            "ipp-versions-supported": "1.1",
        "printer-make-and-model": "My Printer Model",
        "printer-info": "My Printer Info",
        "printer-dns-sd-name": "My Printer DNS-SD Name",
        "printer-name": "My Printer",
        "printer-location": "Local Printer",
        "printer-uuid": "my-printer-uuid",
        "printer-uri-supported": "ipp://localhost:8080/custom",
        "uri-security-supported": "none",
        "uri-authentication-supported": "none",
        "color-supported": "true",
        "copies-supported": "1-99",
        "document-format-supported": "application/pdf",
        "media-col-default": "iso-a4",
        "media-default": "iso-a4",
        "media-left-margin-supported": "true",
        "media-right-margin-supported": "true",
        "media-top-margin-supported": "true",
        "media-bottom-margin-supported": "true",
        "media-supported": "iso-a4,iso-a3,iso-b5",
        "media-type-supported": "plain",
        "output-bin-supported": "face-up",
        "print-color-mode-supported": "monochrome",
        "print-quality-supported": "draft,normal",
        "printer-output-tray": "tray1",
        "printer-resolution-supported": "300dpi,600dpi",
        "sides-supported": "one-sided",
        "printer-device-id": "my-printer-device-id",
        "epcl-version-supported": "1.0",
        "pclm-raster-back-side": "normal",
        "pclm-strip-height-preferred": "1024",
        "pclm-compression-method-preferred": "gzip",
        "pclm-source-resolution-supported": "300dpi",
        "pwg-raster-document-sheet-back": "normal",
        "document-format-details-supported": "application/pdf",
        "media-ready": "true",
        "media-col-ready": "true",
        "print-scaling-supported": "true",
        "print-scaling-default": "100%"
        }
    }
    """.trimIndent()

suspend fun createServer(context: Context) {
    val tokenData = TokenData(context = context)

    fun readToken(): String {
        return runBlocking {
            tokenData.readToken().first()
        }
    }

    val app = routes(
        "/odoo/cgi-bin/epos/service.cgi" bind Method.POST to { req: Request ->
            Log.d("HTTP", "Received request : ${req.bodyString()}")
            if (readToken() == "") {
                createStaticNotification(
                    context,
                    "Failed to send data",
                    "Please connect to the application"
                )
                Response(Status.FORBIDDEN)
            }
            val data = req.bodyString()
            val image = decodeXml(data)
            Log.d("XML", "Decoded xml received $image")
            createIntentNotification(
                context,
                "Input client's email",
                "Tap to open the application."
            )
            Log.d("XML", "Waiting for email...")
            try {
                checkAndSendData(image) { readToken() }
            } catch (e: Exception) {
                Log.d("HTTP", "Failed to send data : " + e.message)
                PopUpActivity.mutableError.value = e.message.toString()
                createStaticNotification(context, "Failed to send data", e.message.toString())
                PopUpActivity.mutableResponse.intValue = 0
                Response(Status.INTERNAL_SERVER_ERROR)
            }

        },
        "/custom" bind Method.POST to { req: Request ->
            Log.d("HTTP", "Received request : ${req.bodyString()}")
            val ippRequest = req.bodyString()
            val parsedAttributes = parseIppRequest(ippRequest)
            println(parsedAttributes)

            Response(Status.OK)
                .header("Content-Type", "application/ipp")
                .body(responseBody)
        }
    )

    app.asServer(KtorCIO(8080)).start()

}