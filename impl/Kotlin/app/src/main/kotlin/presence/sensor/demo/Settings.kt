package presence.sensor.demo

object Settings {
    const val SERVICE_NAME = "hlk_ld1125h" // replace 'sensor_service' with the actual name of your service
    const val PIPE_PATH = "/path/to/presence_data.fifo" // replace with your named pipe path
    const val SERVICE_PATH = "/path/to/HLK-LD1125H-Driver/impl/C/build/hlk_ld1125h" // replace with the actual path to your service
    const val PORT_NAME = "/dev/ttyUSB0" // replace with the actual port name of your sensor
    const val THRESHOLD = 2.0 // replace with your desired threshold value
}
