package net.ukr.kekos222.Parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Realization of AliParser Interface
 *
 * To access JSON uses the same URL with GET-request
 * as the 'flashdeals' page.
 */

public class AliParserImplementation implements AliParser<ArrayNode> {


    private static final String SOURCE_URL =
            "https://gpsfront.aliexpress.com/getRecommendingResults.do?widget_id=5547572&platform=pc&limit=10&phase=1";

    /**
     * Converts 10 Strings from 'getData' method in 'ArrayNode' object
     * and returns it
     *
     * uses 'Jackson' library
     */
    public ArrayNode parseData() {

        ArrayNode result = new ArrayNode(null);
        String postback = "";

        for (int i = 0; i < 100; i += 10) {
            String response = getData(SOURCE_URL + "&offset=" + i + postback);

            try {
                JsonNode jsonTree = new ObjectMapper().readTree(response);

                if (postback.length() == 0) {
                    postback = "&postback=" + jsonTree.get("postback").asText();
                }

                ArrayNode jsonNode = jsonTree.withArray("results");
                result.addAll(jsonNode);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * Saves data from node as a '.csv' file in the root directory
     *
     * Uses 'Jackson' library
     *
     * @param parsedData node with parsed data
     */
    public void saveParsedData(ArrayNode parsedData) {

        CsvSchema.Builder builder = CsvSchema.builder();
        parsedData.get(0).fieldNames().forEachRemaining(builder::addColumn);
        CsvSchema schema = builder.build().withColumnSeparator(';').withHeader();
        CsvMapper mapper = new CsvMapper();

        try (FileWriter fileWriter = new FileWriter("ali_100_products.csv", false)) {
            mapper.writerFor(JsonNode.class)
                    .with(schema)
                    .writeValuesAsArray(fileWriter)
                    .writeAll(parsedData)
                    .flush();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Support method that requests 'JSON' page
     * and returns it as a 'String'
     *
     * @param sourceUrl the URL with full GET-request
     */
    private String getData(String sourceUrl) {
        try {
            URL url = new URL(sourceUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            String readLine;
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
