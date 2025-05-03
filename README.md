# Irish Rail Single Station Train Times Viewer

A simple Java application that displays real-time train information for a specific Irish Rail station.

## Overview

This application fetches current train schedule data from the Irish Rail API for a single, predefined station and generates a static HTML file that displays the information in a clean, tabular format.

## Sample Output

![Sample Train Times HTML Output](train_times_sample.png)

## Features

- Displays train times for a single configured station
- Shows upcoming trains for the next 90 minutes (configurable)
- Information includes:
  - Minutes until arrival
  - Origin and destination stations
  - Expected arrival and departure times
  - Train status (on time/delayed)
  - Direction of travel
- Color-coded status indicators (green for on-time, red for delayed)
- Simple, responsive design that works on all devices
- Generates a static HTML file that can be viewed in any browser

## Requirements

- Java Development Kit (JDK) 11 or higher
- Internet connection (to fetch data from the Irish Rail API)

## Configuration

You can modify the following constants at the top of the `SingleStationTrainTimes.java` file:

```java
// Set your desired station code here
private static final String STATION_CODE = "ENFLD"; // Enfield station

// You can change the station name here (will be displayed in the HTML)
private static final String STATION_NAME = "Enfield";

// Set the look-ahead period in minutes (default 90)
private static final int LOOK_AHEAD_MINUTES = 90;

// Output file name
private static final String OUTPUT_FILE = "train_times.html";
```

## Usage

1. Compile the Java file:
   ```
   javac SingleStationTrainTimes.java
   ```

2. Run the application:
   ```
   java SingleStationTrainTimes
   ```

3. Open the generated `train_times.html` file in your web browser to view the train timetable.

## Finding Station Codes

If you want to change the station, you'll need the station code. Station codes can be found at:
http://api.irishrail.ie/realtime/

Common station codes:
- ENFLD - Enfield
- HSTON - Houston
- CNLLY - Connolly
- PERSE - Pearse
- MHIDE - Malahide
- HOWTH - Howth

## Refreshing Data

To refresh the data with the latest train times, simply run the application again. It will regenerate the HTML file with current information.

## API Information

This application uses the Irish Rail Realtime API:
http://api.irishrail.ie/realtime/realtime.asmx/getStationDataByCodeXML_WithNumMins

## License

This project is released under the MIT License. Feel free to modify and distribute as needed.
