# HLK-LD1125H-Driver
This project provides a software interface for the HLK-LD1125H presence sensor, a hardware component meant to detect human presence in a certain area.

## Project Structure
The project is structured as follows:
- **root directory**: The top-level directory of the project.
  - **impl**: Contains various implementations of the project. Currently, there are implementations in C and Kotlin.
  - **docs**: Contains any data sheets or other relevant information about the part.

## C Implementation
The C implementation designs a Linux service that runs in the background, accessible by other implementations for extracting sensor data.

### Overview
The C program is responsible for interfacing with the presence sensor over a serial port. The main functionality of the C program can be summarized as follows:
- It opens and configures a serial port.
- The program continuously reads data from the sensor, parsing this data into a specific format.
- The parsed data, in JSON format, is then written to a named pipe (FIFO) to make it accessible for other programs.
- The service can be stopped by sending an 'X' or 'x' input.
- The program ensures proper handling of leftover and invalid data samples.

### Building and Running the C Program
The C program can be built using the provided Makefile in the repository. Here are the steps to do this from the terminal:

```sh
# navigate to the root directory of the project
cd HLK-LD1125H-Driver

# compile the program using the makefile
make

# the compiled output, named 'hlk_ld1125h', will be inside the 'build' directory
# to run the service, navigate to the 'build' directory and execute the following command:
./hlk_ld1125h /dev/ttyS0  # replace /dev/ttyS0 with the actual serial port of the sensor
```
Please ensure that the correct serial port name is provided as a command-line argument when starting the service.

## Accessing the Sensor Data
Once the C service is up and running, it writes the sensor data to a named pipe (FIFO) located at `/home/budgettsfrog/presence_data.fifo`. This data can be accessed by other services or a command line user by reading from this named pipe. To read the data from the command line, you can use the following command:

```sh
cat /home/budgettsfrog/presence_data.fifo
```

The data is written to the FIFO in JSON format, with the following structure:
```json
{
  "timestamp": <UNIX timestamp>,
  "field1": "<occ or mov>",
  "field2": "<dis>",
  "distance": "<distance value>"
}
```
Here, the `timestamp` field represents the time when the data was written to the pipe. `field1` can be either `occ` or `mov` depending on the status of the sensor. `field2` is always `dis`, representing distance. The `distance` field contains the distance value read from the sensor, in meters.