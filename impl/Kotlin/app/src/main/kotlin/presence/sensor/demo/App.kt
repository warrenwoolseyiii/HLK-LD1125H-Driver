package presence.sensor.demo

import kotlinx.serialization.json.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import java.util.Scanner

fun String.toJSONObject() = Json.parseToJsonElement(this).jsonObject
fun JsonObject.getDouble(key: String) = this[key]?.jsonPrimitive?.doubleOrNull

fun main() {
    if (!isServiceRunning()) {
        if (!startService()) {
            println("Failed to start the service. Exiting.")
            return
        }
    }

    val pipeFile = File(Settings.PIPE_PATH)

    val readerThread = Thread {
        while(pipeFile.exists()) {
            pipeFile.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    /*val json = line.toJSONObject()
                    val distance = json.getDouble("dist")
                    if (distance != null && distance < Settings.THRESHOLD) {
                        println("Someone is present")
                    } else {
                        println("No one is present")
                    }*/
                    // Just print the raw line
                    println(line)
                }
            }
            Thread.sleep(10)
        }
    }

    readerThread.start()

    val scanner = Scanner(System.`in`)
    while (scanner.hasNextLine()) {
        val line = scanner.nextLine()
        if (line.equals("X", true)) {
            readerThread.interrupt()
            break
        }
    }

    scanner.close()
}

fun isServiceRunning(): Boolean {
    val cmd = "pgrep -f ${Settings.SERVICE_NAME}"
    val proc = Runtime.getRuntime().exec(cmd)
    proc.waitFor(5, TimeUnit.SECONDS)
    return proc.exitValue() == 0
}

fun startService(): Boolean {
    val cmd = "${Settings.SERVICE_PATH} &"
    val proc = Runtime.getRuntime().exec(cmd)
    proc.waitFor(5, TimeUnit.SECONDS)
    return proc.exitValue() == 0
}
