package presence.sensor.demo

import kotlinx.serialization.json.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import java.util.Scanner
import java.util.Queue
import java.util.LinkedList


class SampleQueue(private val size: Int) {
    private val queue: Queue<Double> = LinkedList<Double>()

    fun add(sample: Double) {
        if (queue.size == size) {
            queue.poll()
        }
        queue.add(sample)
    }

    fun average(): Double = queue.average()
}

fun String.toJSONObject() = Json.parseToJsonElement(this).jsonObject
fun JsonObject.getDouble(key: String) = this[key]?.jsonPrimitive?.doubleOrNull

fun main() {
    if (!isServiceRunning()) {
        println("Service is not running. Attempting to start it.")
        if (!startService()) {
            println("Failed to start the service. Exiting.")
            return
        }
    }

    val pipeFile = File(Settings.PIPE_PATH)

    val readerThread = Thread {
        val sampleQueue = SampleQueue(10) // change the number to your desired window size
        var someoneIsPresent = false
        while(pipeFile.exists()) {
            pipeFile.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val json = line.toJSONObject()
                    val distance = json.getDouble("distance")
                    if (distance != null) {
                        sampleQueue.add(distance)
                        val averageDistance = sampleQueue.average()
                        if (averageDistance < Settings.THRESHOLD && !someoneIsPresent) {
                            println("Someone is present")
                            someoneIsPresent = true
                        } else if (averageDistance >= Settings.THRESHOLD && someoneIsPresent) {
                            println("No one is present")
                            someoneIsPresent = false
                        }
                    }
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
    val cmd = "${Settings.SERVICE_PATH} ${Settings.PORT_NAME} ${Settings.PIPE_PATH} &"
    println("Starting service with command: $cmd")
    Runtime.getRuntime().exec(cmd)
    println("Waiting for service to start...")

    // Wait for the service to start up
    Thread.sleep(5000)

    // Now check if the service is running
    val serviceRunning = isServiceRunning()

    println("Service started: $serviceRunning")

    return serviceRunning
}

