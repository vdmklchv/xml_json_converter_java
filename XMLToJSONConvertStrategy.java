package converter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLToJSONConvertStrategy implements NetworkDataConversionStrategy {

    String inputData;

    public XMLToJSONConvertStrategy(String inputData) {
        this.inputData = inputData;
    }

    @Override
    public String convert() {

        Pattern xmlTagPattern = Pattern.compile("<.*?>");
        Matcher xmlTagMatcher = xmlTagPattern.matcher(this.inputData);

        if (hasAttributes(this.inputData)) {
            Map<String, String> attributes = extractAttributes(this.inputData);
            return createJSONWithAttributes(xmlTagMatcher, attributes);
        } else {
            return createSimpleJSON(xmlTagMatcher);
        }

    }

    String createSimpleJSON(Matcher matcher) {
        String xmlKey = "";
        String xmlValue = "";
        if (matcher.find()) {
            xmlKey = matcher.group().substring(1, matcher.group().length() - 1);
            xmlValue = matcher.replaceAll("");
            if (xmlValue != null && !"".equals(xmlValue) && isStringNotNumeric(this.inputData)) {
                xmlValue = "\"" + xmlValue + "\"";
            }
        }

        if (xmlKey.endsWith("/")) {
            xmlKey = xmlKey.substring(0, xmlKey.length() - 1).trim();
            return constructSimpleJson(xmlKey, null);
        }

        return constructSimpleJson(xmlKey, xmlValue);
    }

    String createJSONWithAttributes(Matcher keyMatcher, Map<String, String> attributes) {
        String xmlKey = "";
        String xmlValue = "";

        if (keyMatcher.find()) {
            xmlKey = keyMatcher.group().substring(1, keyMatcher.group().length() - 1).split(" ")[0];
            if (xmlKey.endsWith("/")) {
                xmlKey = xmlKey.substring(0, xmlKey.length() - 1).trim();
                return constructJSONWithAttributes(attributes, xmlKey, null);
            }

            xmlValue = keyMatcher.replaceAll("");

            if (xmlValue != null && !"".equals(xmlValue) && isStringNotNumeric(this.inputData)) {
                xmlValue = "\"" + xmlValue + "\"";
            }
        }

        return constructJSONWithAttributes(attributes, xmlKey, xmlValue);
    }

    private Map<String, String> extractAttributes(String input) {
        Pattern xmlAttributePattern = Pattern.compile("\\w+\\s*=\\s*\"[-\\w]+\"");
        Matcher xmlAttributeMatcher = xmlAttributePattern.matcher(input);

        Map<String, String> attributes = new LinkedHashMap<>();
        while (xmlAttributeMatcher.find()) {
            String[] attributeArray = xmlAttributeMatcher.group().trim().split("\\s*=\\s*");
            attributes.put(attributeArray[0], attributeArray[1]);
        }
        return attributes;
    }

    private String constructSimpleJson(String key, String value) {
        return "{\"" + key + "\" : " + value + "}";
    }

    private boolean isStringNotNumeric(String input) {
        return !Pattern.compile("\\d+[.,]?\\d*").matcher(input).matches();
    }

    private boolean hasAttributes(String input) {
        return Pattern.compile("\\s\\w+\\s*=\\s*\"\\w+\"").matcher(input).find();
    }

    private String constructJSONWithAttributes(Map<String, String> attributes, String key, String value) {
        if ("".equals(value)) {
            value = null;
        }

        StringBuilder sb = new StringBuilder();

        String fullAttributeString = createFullAttributeString(attributes);

        sb.append("{ \"" + key + "\" : {").append(fullAttributeString).append(String.format(", \"#%s\" : %s} }", key, value));

        return sb.toString();
    }

    private String createJsonAttribute(String attributeKey, String attributeValue) {
        return String.format("\"@%s\" : %s", attributeKey, attributeValue);
    }

    private String createFullAttributeString(Map<String, String> attributes) {
        int size = attributes.size();
        String[] attributeArray = new String[size];

        int counter = 0;

        for (Map.Entry<String, String> entry: attributes.entrySet()) {
            String attribute = createJsonAttribute(entry.getKey(), entry.getValue());
            attributeArray[counter] = attribute;
            counter++;
        }

        return String.join(", ", attributeArray);
    }
}
