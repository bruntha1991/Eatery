package eatery;

import java.util.Comparator;

/**
 * Created by bruntha on 8/12/15.
 */
public class MyComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        if (o1.length() > o2.length()) {
            return 1;
        } else if (o1.length() < o2.length()) {
            return -1;
        }
        return o1.compareTo(o2);
    }
}