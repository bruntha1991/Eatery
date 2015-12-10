package eatery;

import Jama.Matrix;
import eatery.WightingModel.EigenValues;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bruntha on 7/20/15.
 */
public class Test {
    static ArrayList<String> yourList = new ArrayList<>();
    static String filePathFoodNames = "/home/bruntha/Documents/FYP/Data/Foodlist/" +
            "Appet.txt";
    static String filePathFoodNamesNew = "/home/bruntha/Documents/FYP/Data/Foodlist/" +
            "Appet_Sorted.txt";

    static String fileToSort = "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/Test/" +
            "u_5.ann";
    static double[] ri = {0, 0, 0, 0.58, 0.90, 1.12, 1.24, 1.32, 1.41, 1.45, 1.51};


    public static void main(String[] args) {
        checkConsistency();
    }

    public static void testSynonyms() {
        WordNet wordNet = new WordNet();
        List<String> x = wordNet.getNounSynonyms("staff");

        for (int i = 0; i < x.size(); i++) {
            System.out.println(x.get(i));
        }
        System.out.println("Done");
    }

    public static void orderTags(String filePath) throws IOException {
        File fileAnnotation = new File(filePath);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;
        int count = 1;
        while ((line = br.readLine()) != null) {
            String[] dic = line.split("[ \t]");
            String lineToWrite="T"+count++ +"\t"+dic[1]+" "+dic[2]+" "+dic[3]+"\t"+dic[4];

            for (int i = 5; i < dic.length ; i++) {
             lineToWrite=lineToWrite+ " "+dic[i];
            }
            writePrintStream(lineToWrite,filePath+"N");
        }
        br.close();
        fr.close();
        System.out.println("Done sorting");
    }

    public static void checkConsistency(){
        double[][] vals = {{1, 3,1/3}, {1/3,1,3},{3,1/3,1}};
        Matrix m = new Matrix(vals);


        double eigenMax = EigenValues.getMaxEigenValue(m);
        double ci = (eigenMax - 3) / (3 - 1);
        double cr = ci * 100 / ri[3];
        System.out.println("Consistency = " + cr);
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

    public static void sortList() {
        try {
            load(filePathFoodNames);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(yourList, new MyComparator());

        for (int i = 0; i < yourList.size(); i++) {
            writePrintStream(yourList.get(yourList.size() - 1 - i), filePathFoodNamesNew);
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

    public static void writePrintStream(String line, String path) {
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
