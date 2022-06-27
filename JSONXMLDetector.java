package converter;

import converter.Enums.*;

public class JSONXMLDetector {

    static DATA_TYPE detectDataType(String inputData) {

        if (inputData.charAt(0) == '<') {
            return DATA_TYPE.XML;
        } else if (inputData.charAt(0) =='{') {
            return DATA_TYPE.JSON;
        } else {
            return DATA_TYPE.UNKNOWN;
        }

    }
}
