package converter;

import converter.Enums.*;

public class Converter {

    String dataInput;

    public Converter(String dataInput) {
        this.dataInput = dataInput;
    }

    public void start() {
        NetworkDataConverter networkDataConverter = null;

        if (JSONXMLDetector.detectDataType(this.dataInput) == DATA_TYPE.JSON) {
            networkDataConverter = new NetworkDataConverter(new JSONToXMLConvertStrategy(this.dataInput));
        } else if (JSONXMLDetector.detectDataType(this.dataInput) == DATA_TYPE.XML) {
            networkDataConverter = new NetworkDataConverter(new XMLToJSONConvertStrategy(this.dataInput));
        } else {
            System.out.println("Unknown data type");
        }

        if (networkDataConverter != null) {
            System.out.println(networkDataConverter.convert());
        }
    }
}
