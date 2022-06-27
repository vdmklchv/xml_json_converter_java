package converter;

import java.util.Scanner;

public class Screen {
    Scanner scanner = new Scanner(System.in);

    String getDataInput() {
         return scanner.nextLine();
    }

}
