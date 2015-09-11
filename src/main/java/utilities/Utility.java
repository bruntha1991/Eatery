package utilities;

import java.util.ArrayList;

/**
 * Created by bruntha on 9/7/15.
 */
 public class Utility {
    public static void printArrayList(ArrayList<String> strings) {
        for (int i = 0; i < strings.size(); i++) {
            System.out.println(strings.get(i));
        }
    }
}
