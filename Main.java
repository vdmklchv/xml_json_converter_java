package converter;

public class Main {

    public static void main(String[] args) {

        Screen screen = new Screen();

        // String dataInput = screen.getDataInput();
        FileReader fr = new FileReader("test.txt");
        String dataInput = fr.read();

        Converter converter = new Converter(dataInput);

        converter.start();

    }
}
