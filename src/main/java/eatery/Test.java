package eatery;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bruntha on 7/20/15.
 */
public class Test {
    static ArrayList<String> yourList=new ArrayList<>();
    static String filePathFoodNames = "/home/bruntha/Documents/FYP/Data/Foodlist/" +
            "Appet.txt";
    static String filePathFoodNamesNew = "/home/bruntha/Documents/FYP/Data/Foodlist/" +
            "Appet_Sorted.txt";
    public static void main(String[] args) {
        testSynonyms();
    }

    public static void testSynonyms() {
        WordNet wordNet=new WordNet();
        List<String> x=wordNet.getNounSynonyms("staff");

        for (int i = 0; i < x.size(); i++) {
            System.out.println(x.get(i));
        }
        System.out.println("Done");
    }

    public static void testLemmatizer() {
        StanfordLemmatizer stanfordLemmatizer = new StanfordLemmatizer();
        List<String> x = stanfordLemmatizer.lemmatize("cars");

        for (int i = 0; i < x.size(); i++) {
            System.out.println(x.get(i));
        }
    }

//    public static void test3() {
//        JWS ws = new JWS("./lib", "3.0");
//        Resnik res = ws.getResnik();
//        TreeMap<String, Double> scores1 = res.res(word1, word2, partOfSpeech);
//        for(Entry<String, Double> e: scores1.entrySet())
//            System.out.println(e.getKey() + "\t" + e.getValue());
//        System.out.println("\nhighest score\t=\t" + res.max(word1, word2, partOfSpeech) + "\n\n\n");
//    }

    public static void sortList()
    {
        try {
            load(filePathFoodNames);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(yourList, new MyComparator());

        for (int i = 0; i < yourList.size(); i++) {
            writePrintStream(yourList.get(yourList.size()-1-i),filePathFoodNamesNew);
        }
        System.out.println("Done sorting");
    }

    private static void load(String path) throws IOException {
        File fileAnnotation = new File(path);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            yourList.add(line);
        }
        br.close();
        fr.close();
        System.out.println("Done loading");
    }
    public static void writePrintStream(String line,String path) {
        PrintStream fileStream = null;
        File file = new File(path);

        try {
            fileStream = new PrintStream(new FileOutputStream(file, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        fileStream.println(line);
        fileStream.close();
    }
}
