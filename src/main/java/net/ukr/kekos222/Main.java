package net.ukr.kekos222;

import com.fasterxml.jackson.databind.node.ArrayNode;
import net.ukr.kekos222.Parser.AliParser;
import net.ukr.kekos222.Parser.AliParserImplementation;

public class Main {
    public static void main(String[] args) {

        AliParser<ArrayNode> aliParser =
                new AliParserImplementation();

        ArrayNode data = aliParser.parseData();

        aliParser.saveParsedData(data);



    }
}
