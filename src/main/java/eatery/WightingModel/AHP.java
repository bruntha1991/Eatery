package eatery.WightingModel;

import domain.Pair;
import org.apache.commons.lang.StringUtils;
import utilities.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AHP {
    Hashtable<String, Integer> aspectHashtable = new Hashtable<>();
    Hashtable<Character, ArrayList<Pair>> inputHashtableSL = new Hashtable<>();
    Hashtable<Character, ArrayList<Pair>> inputHashtableTL = new Hashtable<>();
    String[] filePathToAnnFileArray = {"/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/u_1.ann",
            "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/u_2.ann",
            "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/u_3.ann",
            "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/u_4.ann",
    };
    ArrayList<Pair> pairFirstLevelArrayList = new ArrayList<>();


    public static void main(String args[]) {
        AHP ahp = new AHP();
        ahp.calculateWeight();
    }

    public void calculateWeight() {
        try {
            getAspectCount(filePathToAnnFileArray);
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
            Hashtable<String, Double> weights = calculateWeightOfAspect(pairFirstLevelArrayList, normalizedMatrix);
            Utility.printHashTableSD(weights);
            System.out.println("############# NORMALIZED WEIGHTS ###############");
            Utility.printHashTableSD(normalizeWeights(weights));

            System.out.println("############# SECOND LEVEL ###############");
            separateSecondLevelAspects();
            Hashtable<String, double[][]> matrixHashTable = buildMatrixSecondLevel(inputHashtableSL);
            Utility.printMatrix(inputHashtableSL, matrixHashTable);
            System.out.println("############# NORMALIZED ###############");
            Hashtable<String, double[][]> normalisedMatrixHashtable = normalizeByColumn(matrixHashTable);
            Utility.printMatrix(inputHashtableSL, normalisedMatrixHashtable);
            System.out.println("############# WEIGHTS ###############");
            Hashtable<String, Double> weightsL2 = calculateWeightOfAspect(inputHashtableSL, normalisedMatrixHashtable);
            Utility.printHashTableSD(inputHashtableSL, weightsL2);
            System.out.println("############# NORMALIZED WEIGHTS ###############");
            Utility.printHashTableSD(inputHashtableSL, normalizeWeights(inputHashtableSL, weightsL2));

            System.out.println("############# THIRD LEVEL ###############");
            separateThirdLevelAspects();
            matrixHashTable = buildMatrixSecondLevel(inputHashtableTL);
            Utility.printMatrix(inputHashtableTL, matrixHashTable);
            System.out.println("############# NORMALIZED ###############");
            normalisedMatrixHashtable = normalizeByColumn(matrixHashTable);
            Utility.printMatrix(inputHashtableTL, normalisedMatrixHashtable);
            System.out.println("############# WEIGHTS ###############");
            weights=calculateWeightOfAspect(inputHashtableTL, normalisedMatrixHashtable);
            Utility.printHashTableSD(inputHashtableTL,weights);
            System.out.println("############# NORMALIZED WEIGHTS ###############");
            Utility.printHashTableSD(inputHashtableTL, normalizeWeights(inputHashtableTL, weights));

//            Utility.printHashTableSD(normalizeWeights(weights));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Hashtable<String, Double> normalizeWeights(Hashtable<Character, ArrayList<Pair>> inputHashtableSL, Hashtable<String, Double> weightsL2) {
        Hashtable<String, Double> hashtableResult = new Hashtable<>();

        Set set1 = inputHashtableSL.entrySet();
        Iterator it1 = set1.iterator();

        while (it1.hasNext()) {
            Map.Entry entry1 = (Map.Entry) it1.next();

            Set set = weightsL2.entrySet();
            Iterator it = set.iterator();
            System.out.println();
            Hashtable<String, Double> hashtable = new Hashtable<>();
            Hashtable<String, Double> hashtableTemp = new Hashtable<>();

            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if (entry1.getKey().toString().matches(entry.getKey().toString().substring(0, 1))) {
                    hashtable.put(entry.getKey().toString(), (Double) entry.getValue());
                }
            }

            hashtableTemp=normalizeWeights(hashtable);
            Set set2 = hashtableTemp.entrySet();
            Iterator it2 = set2.iterator();
            while (it2.hasNext()) {
                Map.Entry entry = (Map.Entry) it2.next();
                hashtableResult.put(entry.getKey().toString(), (Double) entry.getValue());
            }
        }
        return hashtableResult;
    }

    private Hashtable<String, Double> calculateWeightOfAspect(Hashtable<Character, ArrayList<Pair>> inputHashTable,
                                                              Hashtable<String, double[][]> normalisedMatrixHashtable) {
        Hashtable<String, Double> hashTable = new Hashtable<>();

        Set set = inputHashTable.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();

            ArrayList<Pair> child = (ArrayList<Pair>) entry.getValue();

            Hashtable<String, Double> sub = calculateWeightOfAspect(child,
                    normalisedMatrixHashtable.get(entry.getKey().toString()));

            Set setSub = sub.entrySet();
            Iterator itSub = setSub.iterator();
            while (itSub.hasNext()) {
                Map.Entry entrySub = (Map.Entry) itSub.next();

                hashTable.put(entrySub.getKey().toString(), (Double) entrySub.getValue());

            }
        }
        return hashTable;
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

    private void getAspectCount(String[] filePath) throws IOException {
        for (int i = 0; i < filePath.length; i++) {
            getAspectCount(filePath[i]);
        }
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
            if (!(entry.getKey().toString().matches("Restaurant") || entry.getKey().toString().matches("Restaurants")) && StringUtils.countMatches(entry.getKey().toString(), "_") == 0) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
                pairFirstLevelArrayList.add(new Pair(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString())));
//                inputHashtableSL.put(entry.getKey().toString().substring(0,1), new ArrayList<Pair>());
                inputHashtableSL.put(entry.getKey().toString().charAt(0), new ArrayList<Pair>());
            }
        }

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            entry.getKey().toString().charAt(0);
        }
    }

//    private void addChild(Hashtable<Character, ArrayList<Pair>> inputHashtable) {
//
//        pairFirstLevelArrayList.add(new Pair(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString())));
//
//    }

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

    private Hashtable<String, Double> normalizeWeights(Hashtable<String, Double> hashtable) {
        Hashtable<String, Double> hashtableResult = new Hashtable<>();
        double total = 0;
        Set set = hashtable.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            total += (double) entry.getValue();
        }

        Iterator it2 = set.iterator();
        while (it2.hasNext()) {
            Map.Entry entry2 = (Map.Entry) it2.next();
            hashtableResult.put(entry2.getKey().toString(), ((double) entry2.getValue()) / total);
        }

        return hashtableResult;
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
//                    inputHashtableSL.put(entry.getKey().toString().substring(0,1), pairArrayList);
                    inputHashtableSL.put(entry.getKey().toString().charAt(0), pairArrayList);
                    int index=entry.getKey().toString().indexOf("-",2);
//                    inputHashtableTL.put(entry.getKey().toString().substring(0, 1), new ArrayList<Pair>());
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
//                    inputHashtableTL.put(entry.getKey().toString().substring(0, 1), pairArrayList);
                }
            }
        }
    }

    private Hashtable<String, double[][]> buildMatrixSecondLevel(Hashtable<Character, ArrayList<Pair>> inputHashtable) {
        Hashtable<String, double[][]> outputHashtableFL = new Hashtable<>();

        Set set = inputHashtable.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            outputHashtableFL.put(entry.getKey().toString(), buildMatrix((ArrayList<Pair>) entry.getValue()));
        }
        return outputHashtableFL;
    }


}
