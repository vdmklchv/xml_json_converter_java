package converter;

public class NetworkDataConverter {
    NetworkDataConversionStrategy strategy;

    public NetworkDataConverter(NetworkDataConversionStrategy strategy) {
        this.strategy = strategy;
    }

    String convert() {
        return strategy.convert();
    }
}
