import java.io.File
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.*

fun String.toJSONObject() = Json.parseToJsonElement(this).jsonObject
fun JsonObject.getDouble(key: String) = this[key]!!.jsonPrimitive.content.toDouble()

fun main() {
    val pipePath = "/path/to/your/named/pipe" // replace with your named pipe path

    if (!isServiceRunning()) {
        if (!startService()) {
            println("Failed to start the service. Exiting.")
            return
        }
    }

    val pipeFile = File(pipePath)
    val threshold = 10.0 // replace with your desired threshold value

    pipeFile.bufferedReader().useLines { lines ->
        lines.forEach { line ->
            val json = line.toJSONObject()
            val distance = json.getDouble("dist")
            if (distance < threshold) {
                println("Someone is present")
            } else {
                println("No one is present")
            }
        }
    }
}

fun isServiceRunning(): Boolean {
    val cmd = "pgrep -f sensor_service" // replace 'sensor_service' with the actual name of your service
    val proc = Runtime.getRuntime().exec(cmd)
    proc.waitFor(5, TimeUnit.SECONDS)
    return proc.exitValue() == 0
}

fun startService(): Boolean {
    val cmd = "/path/to/sensor_service &" // replace with the actual path to your service
    val proc = Runtime.getRuntime().exec(cmd)
    proc.waitFor(5, TimeUnit.SECONDS)
    return proc.exitValue() == 0
}
