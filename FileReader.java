package converter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

    String fileName;

    public FileReader(String fileName) {
        this.fileName = fileName;
    }

    String read() {
        try {
            String contents = new String(Files.readAllBytes(Paths.get(this.fileName)));
            return contents;
        } catch (IOException e) {
            System.out.println("Couldn't read from file.");
        }
        return "";
    }
}
