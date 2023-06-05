#include <stdio.h>   /* Standard input/output definitions */
#include <string.h>  /* String function definitions */
#include <unistd.h>  /* UNIX standard function definitions */
#include <fcntl.h>   /* File control definitions */
#include <errno.h>   /* Error number definitions */
#include <termios.h> /* POSIX terminal control definitions */
#include <time.h>    /* time_t, struct tm, difftime, time, mktime */
#include <stdint.h>
#include <stdbool.h>
#include <sys/stat.h>
#include <ctype.h>  // for isspace()

#define BUFFER_SIZE 256
#define MAX_SAMPLE_SIZE 128
#define FIFO_NAME "/home/budgettsfrog/presence_data.fifo"

// Function to open a serial port
int open_port(char *port) {
    int fd; // file description for the serial port

    fd = open(port, O_RDWR | O_NOCTTY | O_NDELAY);

    if (fd == -1) {
        printf("Unable to open port. \n");
    } else {
        fcntl(fd, F_SETFL, 0);
        printf("Port is open.\n");
    }

    return (fd);
}

// Function to configure a serial port
int configure_port(int fd) {
    struct termios options;

    // Get the current options for the port
    tcgetattr(fd, &options);

    // Set the baud rates to 115200
    cfsetispeed(&options, B115200);
    cfsetospeed(&options, B115200);

    // Enable the receiver and set local mode
    options.c_cflag |= (CLOCAL | CREAD);

    // Set the new options for the port
    tcsetattr(fd, TCSANOW, &options);

    return fd;
}

// Function to strip whitespace from a string and modify it in-place
void strip_whitespace(char *str) {
    char *p1 = str, *p2 = str;
    while (*p1 != 0) {
        if (!isspace(*p1)) {
            *p2++ = *p1;
        }
        p1++;
    }
    *p2 = 0;
}

// Function to parse a buffer of samples handle invalid data and write valid data to a FIFO
void parse_buffer(char *buffer) {
    static char leftover[MAX_SAMPLE_SIZE];
    char sample[MAX_SAMPLE_SIZE];
    char *start, *end;
    time_t timestamp;

    start = buffer;

    while ((end = strchr(start, '\n')) != NULL) {  // Assuming each sample ends with a newline
        strncpy(sample, leftover, sizeof(sample));
        strncat(sample, start, end - start);
        sample[sizeof(sample) - 1] = '\0';  // Ensure null-termination

        timestamp = time(NULL);

        // Parse sample
        // Assuming the data format is: <field1>,<field2>=<distance>
        char *field1 = strtok(buffer, ",");
        char *field2 = strtok(NULL, "=");
        char *distance = strtok(NULL, "=");

        if (field1 != NULL && field2 != NULL && distance != NULL) {
            // Strip any whitespace from the fields
            strip_whitespace(field1);
            strip_whitespace(field2);
            strip_whitespace(distance);

            //printf("Field1: %s\n", field1);
            //printf("Field2: %s\n", field2);
            //printf("Distance: %s\n", distance);

            // Parse a little further. Field1 is always going to be "occ" or "mov", if it's not, we have a bad sample
            // Field2 is always going to be "dis", if it's not, we have a bad sample
            // Distance is always going to be a decimal number in the format of X.XX, if it's not, we have a bad sample
            bool sample_valid = true;
            if ( (strcmp(field1, "occ") != 0 && strcmp(field1, "mov") != 0) || (strcmp(field2, "dis") != 0) || (strlen(distance) != 4) )  {
                sample_valid = false;
            }

            if(sample_valid) {
                FILE *fifo = fopen(FIFO_NAME, "w");
                if (fifo == NULL) {
                    fprintf(stderr, "Failed to open FIFO %s for writing\n", FIFO_NAME);
                } else {
                    // Write the parsed data to the FIFO in JSON format
                    fprintf(fifo, "{\"timestamp\": %ld, \"field1\": \"%s\", \"field2\": \"%s\", \"distance\": \"%s\"}\n", timestamp, field1, field2, distance);
                    fclose(fifo);
                }
            }
        }

        start = end + 1;  // Next sample starts after the newline
        leftover[0] = '\0';  // Clear the leftover buffer
    }

    // Store the leftover partial sample
    if (*start != '\0') {
        strncpy(leftover, start, sizeof(leftover));
        leftover[sizeof(leftover) - 1] = '\0';  // Ensure null-termination
    }
}

int main(int argc, char *argv[]) {
    if (argc < 2) {
        fprintf(stderr, "Usage: %s <serial_port>\n", argv[0]);
        return 1;
    }

    char *port = argv[1];
    int fd, n;
    char buffer[BUFFER_SIZE];
    char input;

    // Make stdin non-blocking
    int flags = fcntl(STDIN_FILENO, F_GETFL, 0);
    fcntl(STDIN_FILENO, F_SETFL, flags | O_NONBLOCK);

    // Create a named pipe (FIFO) if it doesn't exist yet
    if (mkfifo(FIFO_NAME, 0666) == -1) {
        if (errno != EEXIST) {
            printf("Failed to create FIFO. %s\n", strerror(errno));
            return 1;
        }
    }

    printf("Checking port %s\n", port);
    fd = open_port(port);
    if (fd != -1) {
        fd = configure_port(fd);

        while (1) {
            n = read(fd, buffer, BUFFER_SIZE);

            if (n > 0) {
                parse_buffer(buffer);
                memset(buffer, 0, sizeof(buffer));
            }

            // Sleep for 10 ms
            usleep(10000);

            if(read(STDIN_FILENO, &input, 1) < 0) {
                // No input, continue doing something else
                continue;
            }
            
            // If user enters 'X' or 'x', break the loop
            if(input == 'X' || input == 'x') {
                break;
            }
        }

        close(fd);
        unlink(FIFO_NAME);
    }

    return 0;
}
