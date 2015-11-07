package eatery.WightingModel;

import domain.Pair;
import org.apache.commons.lang.StringUtils;
import utilities.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by bruntha on 11/4/15.
 */
public class AHP {
    Hashtable<String, Integer> aspectHashtable = new Hashtable<>();
    Hashtable<Character, ArrayList<Pair>> inputHashtableSL = new Hashtable<>();
    Hashtable<Character, ArrayList<Pair>> inputHashtableTL = new Hashtable<>();
    Hashtable<String, double[][]> outputHashtableFL = new Hashtable<>();
    String filePathToAnnFile = "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/" +
            "review_100_A_Review_last30.ann";
    ArrayList<Pair> pairFirstLevelArrayList = new ArrayList<>();


    public static void main(String args[]) {
        AHP ahp = new AHP();
        ahp.calculateWeight();
    }

    public void calculateWeight() {
        try {
            getAspectCount(filePathToAnnFile);
            Utility.printHashTable(aspectHashtable);

            System.out.println("############# FIRST LEVEL ###############");
            separateFirstLevelAspects();
            System.out.println("############# RESTAURANT ###############");
            double[][] matrix = buildMatrix(pairFirstLevelArrayList);
            Utility.printMatrix(pairFirstLevelArrayList, matrix);
            System.out.println("############# NORMALIZED ###############");
            double[][] normalizedMatrix = normalizeByColumn(matrix);
            Utility.printMatrix(pairFirstLevelArrayList, normalizedMatrix);
            System.out.println("############# WEIGHTS ###############");
            Utility.printHashTableSD(calculateWeightOfAspect(pairFirstLevelArrayList, normalizedMatrix));

            System.out.println("############# SECOND LEVEL ###############");
            separateSecondLevelAspects();
            Hashtable<String, double[][]> matrixHashtable = buildMatrixSecondLevel(inputHashtableSL);
            Utility.printMatrix(inputHashtableSL, matrixHashtable);
            System.out.println("############# NORMALIZED ###############");
            Hashtable<String, double[][]> normalisedMatrixHashtable = normalizeByColumn(matrixHashtable);
            Utility.printMatrix(inputHashtableSL, normalisedMatrixHashtable);
            System.out.println("############# WEIGHTS ###############");
            Utility.printHashTableSD(calculateWeightOfAspect(inputHashtableSL, normalisedMatrixHashtable));

            System.out.println("############# THIRD LEVEL ###############");
            separateThirdLevelAspects();
            matrixHashtable = buildMatrixSecondLevel(inputHashtableTL);
            Utility.printMatrix(inputHashtableTL, matrixHashtable);
            System.out.println("############# NORMALIZED ###############");
            normalisedMatrixHashtable = normalizeByColumn(matrixHashtable);
            Utility.printMatrix(inputHashtableTL, normalisedMatrixHashtable);
            System.out.println("############# WEIGHTS ###############");
            Utility.printHashTableSD(calculateWeightOfAspect(inputHashtableTL, normalisedMatrixHashtable));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Hashtable<String, Double> calculateWeightOfAspect(Hashtable<Character, ArrayList<Pair>> inputHashtableSL,
                                                              Hashtable<String, double[][]> normalisedMatrixHashtable) {
        Hashtable<String, Double> hashtable = new Hashtable<>();

        Set set = inputHashtableSL.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();

            char parent = (char) entry.getKey();
            ArrayList<Pair> child = (ArrayList<Pair>) entry.getValue();

            Hashtable<String, Double> sub = calculateWeightOfAspect(child,
                    normalisedMatrixHashtable.get(entry.getKey().toString()));

            Set setSub = sub.entrySet();
            Iterator itSub = setSub.iterator();
            while (itSub.hasNext()) {
                Map.Entry entrySub = (Map.Entry) itSub.next();

                hashtable.put(entrySub.getKey().toString(), (Double) entrySub.getValue());

            }
        }
        return hashtable;
    }

    private void getAspectCount(String filePath) throws IOException {
        File fileAnnotation = new File(filePath);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            String[] dic = line.split("[ \t]");
            if (dic[1].charAt(0) != 'P') {
                if (!aspectHashtable.containsKey(dic[1])) {
                    aspectHashtable.put(dic[1], 1);
                } else {
                    aspectHashtable.put(dic[1], aspectHashtable.get(dic[1]) + 1);
                }
            }

        }
        br.close();
        fr.close();

    }

    private void buildMatrixTop() {
        if (aspectHashtable.containsKey("Restaurant")) {
            System.out.println("Restaurant : " + aspectHashtable.get("Restaurant"));
        }

    }

    public void separateFirstLevelAspects() {
        Set set = aspectHashtable.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (!entry.getKey().toString().matches("Restaurant") && StringUtils.countMatches(entry.getKey().toString(), "_") == 0) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
                pairFirstLevelArrayList.add(new Pair(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString())));
                inputHashtableSL.put(entry.getKey().toString().charAt(0), new ArrayList<Pair>());
            }
        }
    }

    private double[][] buildMatrix(ArrayList<Pair> arrayList) {
        double[][] matrix = new double[arrayList.size()][arrayList.size()];

        for (int i = 0; i < arrayList.size(); i++) {
            for (int j = 0; j < arrayList.size(); j++) {
                matrix[i][j] = arrayList.get(i).getValue() / (1.00 * arrayList.get(j).getValue());
            }
        }
        return matrix;
    }

    private double[][] normalizeByColumn(double[][] doubles) {
        double normalizedValue[] = new double[doubles.length];

        for (int i = 0; i < doubles.length; i++) {
            for (int j = 0; j < doubles.length; j++) {
                normalizedValue[j] += doubles[i][j];
            }
        }

        for (int i = 0; i < doubles.length; i++) {
            for (int j = 0; j < doubles.length; j++) {
                doubles[i][j] = doubles[i][j] / normalizedValue[j];
            }
        }
        return doubles;
    }

    private Hashtable<String, double[][]> normalizeByColumn(Hashtable<String, double[][]> hashtable) {
        Hashtable<String, double[][]> result = new Hashtable<>();


        Set set = hashtable.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();

            double[][] doubles = (double[][]) entry.getValue();
            double normalizedValue[] = new double[doubles.length];

            for (int i = 0; i < doubles.length; i++) {
                for (int j = 0; j < doubles.length; j++) {
                    normalizedValue[j] += doubles[i][j];
                }
            }

            for (int i = 0; i < doubles.length; i++) {
                for (int j = 0; j < doubles.length; j++) {
                    doubles[i][j] = doubles[i][j] / normalizedValue[j];
                }
            }
            result.put(entry.getKey().toString(), doubles);
        }

        return result;
    }

    private Hashtable<String, Double> calculateWeightOfAspect(ArrayList<Pair> stringArrayList, double[][] doubles) {
        Hashtable<String, Double> hashtable = new Hashtable<>();
        double weights[] = new double[doubles.length];

        for (int i = 0; i < doubles.length; i++) {
            for (int j = 0; j < doubles.length; j++) {
                weights[i] += doubles[i][j];
            }
        }

        for (int i = 0; i < stringArrayList.size(); i++) {
            hashtable.put(stringArrayList.get(i).getKey(), weights[i]);
        }
        return hashtable;
    }

    private void separateSecondLevelAspects() {
        Set set = aspectHashtable.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (StringUtils.countMatches(entry.getKey().toString(), "_") == 1) {
                if (inputHashtableSL.containsKey(entry.getKey().toString().charAt(0))) {
                    ArrayList<Pair> pairArrayList = inputHashtableSL.get(entry.getKey().toString().charAt(0));
                    pairArrayList.add(new Pair(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString())));
                    inputHashtableSL.put(entry.getKey().toString().charAt(0), pairArrayList);
                    inputHashtableTL.put(entry.getKey().toString().charAt(0), new ArrayList<Pair>());
                }
            }
        }
    }

    private void separateThirdLevelAspects() {
        Set set = aspectHashtable.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (StringUtils.countMatches(entry.getKey().toString(), "_") == 2) {
                if (inputHashtableTL.containsKey(entry.getKey().toString().charAt(0))) {
                    ArrayList<Pair> pairArrayList = inputHashtableTL.get(entry.getKey().toString().charAt(0));
                    pairArrayList.add(new Pair(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString())));
                    inputHashtableTL.put(entry.getKey().toString().charAt(0), pairArrayList);
                }
            }
        }
    }

    private Hashtable<String, double[][]> buildMatrixSecondLevel(Hashtable<Character, ArrayList<Pair>> inputHashtableFL) {
        Hashtable<String, double[][]> outputHashtableFL = new Hashtable<>();

        Set set = inputHashtableFL.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            outputHashtableFL.put(entry.getKey().toString(), buildMatrix((ArrayList<Pair>) entry.getValue()));
        }
        return outputHashtableFL;
    }

    private Hashtable<String, double[][]> buildMatrixThirdLevel(Hashtable<Character, ArrayList<Pair>> inputHashtableFL) {
        Hashtable<String, double[][]> outputHashtableFL = new Hashtable<>();

        Set set = inputHashtableFL.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            outputHashtableFL.put(entry.getKey().toString(), buildMatrix((ArrayList<Pair>) entry.getValue()));
        }
        return outputHashtableFL;
    }
}
