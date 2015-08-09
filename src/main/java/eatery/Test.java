package eatery;

import java.util.List;

/**
 * Created by bruntha on 7/20/15.
 */
public class Test {
    public static void main(String[] args) {
        WordNet wordNet = new WordNet();
        List<String> synonyms = wordNet.getNounSynonyms("spoon");

        for (int i = 0; i < synonyms.size(); i++) {
            System.out.println(synonyms.get(i));
        }
    }
}
