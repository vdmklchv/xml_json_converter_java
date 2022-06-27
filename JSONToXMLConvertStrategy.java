package converter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONToXMLConvertStrategy implements NetworkDataConversionStrategy {

    String inputData;

    public JSONToXMLConvertStrategy(String inputData) {
        this.inputData = inputData;
    }

    @Override
    public String convert() {
        if (isJSONWithAttributes(this.inputData)) {
            return createXMLWithAttributes();
        } else {
            return createSimpleXML();
        }
    }

    String getKeyFromJSONWithAttributes() {
        Pattern xmlKeyPattern = Pattern.compile("\"\\w+\"\\s*:");
        Matcher xmlKeyMatcher = xmlKeyPattern.matcher(this.inputData);


        String xmlKey = "";

        if (xmlKeyMatcher.find()) {
            xmlKey = xmlKeyMatcher.group().replaceAll("[\" :]", "");
        }

        return xmlKey;
    }

    String createXMLWithAttributes() {
        Matcher jsonValueMatcher = Pattern.compile("\"#.*\\s*}").matcher(this.inputData);

        String xmlKey = getKeyFromJSONWithAttributes();
        String xmlValue = null;

        if (jsonValueMatcher.find()) {
            String[] array = jsonValueMatcher.group().replaceAll("[\"#}]", "").split("\\s*:\\s*");
            if (array.length == 2) {
                xmlValue = array[1].trim();
            }
        }

        Map<String, String> attributes = getAttributesMap();

        return constructXMLWithAttributes(attributes, xmlKey, xmlValue);
    }

    Map<String, String> getAttributesMap() {
        Map<String, String> attributesMap = new LinkedHashMap<>();

        Pattern jsonAttributePattern = Pattern.compile("@.*?\\s*[,}]");
        Matcher jsonAttributeMatcher = jsonAttributePattern.matcher(this.inputData);

        String jsonAttribute;

        while (jsonAttributeMatcher.find()) {
            jsonAttribute = jsonAttributeMatcher.group().replaceAll("[\",@]", "");
            String[] jsonAttributeArray = jsonAttribute.split("\\s*:\\s*");
            attributesMap.put(jsonAttributeArray[0], jsonAttributeArray[1]);
        }

        return attributesMap;
    }

    private String createSimpleXML() {
        Matcher jsonKeyMatcher = Pattern.compile("\\b\\w*?\\b\"\\s?:").matcher(inputData);
        Matcher jsonValueMatcher = Pattern.compile(":.*?[,}]").matcher(inputData);

        if (jsonKeyMatcher.find() && jsonValueMatcher.find()) {
            String jsonKey = jsonKeyMatcher.group().replaceAll("[\": ]", "");
            String jsonValue = jsonValueMatcher.group().replaceAll("[\"},: ]", "");

            if (jsonValue.equals("null")) {
                return constructSelfClosingTag(jsonKey);
            }

            return constructOpeningTag(jsonKey) + jsonValue + constructClosingTag(jsonKey);
        }

        return null;
    }

    private String constructOpeningTag(String input) {
        return "<" + input + ">";
    }

    private String constructClosingTag(String input) {
        return "</" + input + ">";
    }

    private String constructSelfClosingTag(String input) {
        return "<" + input + "/>";
    }

    private String constructXMLWithAttributes(Map<String, String> attributes, String key, String value) {
        String attributeString = constructXMLAttributeString(attributes);
        if ("".equals(value) || value == null || "null".equals(value)) {
            return constructSelfClosingTag(key + " " + attributeString + " ");
        }
        return constructOpeningTag(key + " " + attributeString) + value + constructClosingTag(key);
    }

    private String constructXMLAttributeString(Map<String, String> attributes) {
        int size = attributes.size();
        String[] attributeArray = new String[size];

        int counter = 0;

        for (Map.Entry<String, String> entry: attributes.entrySet()) {
            String attribute = entry.getKey() + " = \"" + entry.getValue() + "\"";
            attributeArray[counter] = attribute;
            counter++;
        }

        return String.join(" ", attributeArray);
    }

    private boolean isJSONWithAttributes(String input) {
        return Pattern.compile(":\\s*\\{").matcher(input).find();
    }
}
