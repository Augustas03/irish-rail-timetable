package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.StringReader;

public class IrishRailTrainTimes {


    private static final String STATION_CODE = "DGHDA"; // Drogheda station
    private static final String STATION_NAME = "Drogheda"; // Used for html
    private static final int LOOK_AHEAD_MINUTES = 90; // Max look-ahead
    private static final String OUTPUT_FILE = "train_times.html"; // Output file name
    private static final String API_URL = "http://api.irishrail.ie/realtime/realtime.asmx/getStationDataByCodeXML_WithNumMins?StationCode=%s&NumMins=%d&format=xml";


    public static void main(String[] args) {
        try {
            // Fetch train data
            System.out.println("Fetching train data for station: " + STATION_CODE);
            List<TrainInfo> trains = fetchTrainData();

            // Generate HTML file
            System.out.println("Generating HTML file...");
            generateHtmlFile(trains);

            System.out.println("Done! Open " + OUTPUT_FILE + " in your browser to view train times.");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<TrainInfo> fetchTrainData() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        // Create HTTP client
        HttpClient client = HttpClient.newHttpClient();

        // Create HTTP request
        String url = String.format(API_URL, STATION_CODE, LOOK_AHEAD_MINUTES);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // Send request and get response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch data: HTTP " + response.statusCode());
        }

        // Parse XML response
        return parseTrainData(response.body());
    }

    private static List<TrainInfo> parseTrainData(String xmlData) throws ParserConfigurationException, SAXException, IOException {
        List<TrainInfo> trainList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlData)));

        document.getDocumentElement().normalize();

        NodeList trainNodes = document.getElementsByTagName("objStationData");

        for (int i = 0; i < trainNodes.getLength(); i++) {
            Element trainElement = (Element) trainNodes.item(i);

            TrainInfo train = new TrainInfo();
            train.origin = getElementValue(trainElement, "Origin");
            train.destination = getElementValue(trainElement, "Destination");
            train.expectedArrival = getElementValue(trainElement, "Exparrival");
            train.expectedDeparture = getElementValue(trainElement, "Expdepart");
            train.duein = getElementValue(trainElement, "Duein");
            train.status = getElementValue(trainElement, "Status");
            train.direction = getElementValue(trainElement, "Direction");

            trainList.add(train);
        }

        return trainList;
    }

    private static String getElementValue(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "N/A";
    }

    private static void generateHtmlFile(List<TrainInfo> trains) throws IOException {
        // Get current timestamp
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html lang=\"en\">\n");
            writer.write("<head>\n");
            writer.write("    <meta charset=\"UTF-8\">\n");
            writer.write("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
            writer.write("    <title>Train Times - " + STATION_NAME + "</title>\n");
            writer.write("    <style>\n");
            writer.write("        body { font-family: Arial, sans-serif; margin: 20px; }\n");
            writer.write("        h1 { color: #2c3e50; }\n");
            writer.write("        table { border-collapse: collapse; width: 100%; margin-top: 20px; }\n");
            writer.write("        th, td { text-align: left; padding: 12px; border-bottom: 1px solid #ddd; }\n");
            writer.write("        th { background-color: #3498db; color: white; }\n");
            writer.write("        tr:nth-child(even) { background-color: #f2f2f2; }\n");
            writer.write("        .status-ontime { color: green; font-weight: bold; }\n");
            writer.write("        .status-late { color: red; font-weight: bold; }\n");
            writer.write("        .footer { margin-top: 30px; font-size: 0.8em; color: #7f8c8d; }\n");
            writer.write("    </style>\n");
            writer.write("</head>\n");
            writer.write("<body>\n");
            writer.write("    <h1>Irish Rail Train Times - " + STATION_NAME + "</h1>\n");
            writer.write("    <p>Last updated: " + timestamp + "</p>\n");

            if (trains.isEmpty()) {
                writer.write("    <p>No trains scheduled at this station in the next " + LOOK_AHEAD_MINUTES + " minutes.</p>\n");
            } else {
                writer.write("    <table>\n");
                writer.write("        <thead>\n");
                writer.write("            <tr>\n");
                writer.write("                <th>Due In</th>\n");
                writer.write("                <th>Origin</th>\n");
                writer.write("                <th>Destination</th>\n");
                writer.write("                <th>Expected Arrival</th>\n");
                writer.write("                <th>Expected Departure</th>\n");
                writer.write("                <th>Status</th>\n");
                writer.write("                <th>Direction</th>\n");
                writer.write("            </tr>\n");
                writer.write("        </thead>\n");
                writer.write("        <tbody>\n");

                for (TrainInfo train : trains) {
                    writer.write("            <tr>\n");
                    writer.write("                <td>" + train.duein + " mins</td>\n");
                    writer.write("                <td>" + train.origin + "</td>\n");
                    writer.write("                <td>" + train.destination + "</td>\n");
                    writer.write("                <td>" + train.expectedArrival + "</td>\n");
                    writer.write("                <td>" + train.expectedDeparture + "</td>\n");

                    String statusClass = train.status.toLowerCase().contains("on time") ? "status-ontime" : "status-late";
                    writer.write("                <td class=\"" + statusClass + "\">" + train.status + "</td>\n");

                    writer.write("                <td>" + train.direction + "</td>\n");
                    writer.write("            </tr>\n");
                }

                writer.write("        </tbody>\n");
                writer.write("    </table>\n");
            }

            writer.write("    <div class=\"footer\">\n");
            writer.write("        <p>Data provided by Irish Rail Realtime API</p>\n");
            writer.write("    </div>\n");
            writer.write("</body>\n");
            writer.write("</html>");
        }
    }
}