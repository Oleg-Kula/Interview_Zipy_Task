package net.ukr.kekos222.Parser;

/**
 * Behaviour of AliExpress's parser.
 *
 * @param <ParsedData> the data for saving
 */
public interface AliParser <ParsedData> {

    ParsedData parseData();

    void saveParsedData(ParsedData parsedData);
}
