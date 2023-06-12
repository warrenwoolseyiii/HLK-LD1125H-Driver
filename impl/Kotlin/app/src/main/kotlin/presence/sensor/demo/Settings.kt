package presence.sensor.demo

object Settings {
    const val SERVICE_NAME = "hlk_ld1125h" // replace 'sensor_service' with the actual name of your service
    const val PIPE_PATH = "/home/budgettsfrog/presence_data.fifo" // replace with your named pipe path
    const val SERVICE_PATH = "/mnt/c/Users/budge/Repos/HLK-LD1125H-Driver/impl/C/build/hlk_ld1125h" // replace with the actual path to your service
    const val PORT_NAME = "/dev/ttyUSB1" // replace with the actual port name of your sensor
    const val THRESHOLD = 1.0 // replace with your desired threshold value
}
