package eatery.implicitAspect;

import domain.Ann;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruntha on 12/25/15.
 */
public class PercentageCalculation {
    List anns;
    int noOfSentence = 0;
    int noOf_1_Aspect = 0;
    int noOf_2_Aspect = 0;
    int noOf_3_Aspect = 0;
    int currentCount = 0;
    int lastAnnIndex = 0;
    String[] annFile = {
            "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/Test/u_14 Ann_Sorted.txt",
            "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/Test/u_1 Ann_Sorted.txt",
            "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/Test/u_5 Ann_Sorted.txt",
            "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/Test/u_11 Ann_Sorted.txt",
    };

    //    String[] annFile = {"/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/Test/ta.txt"};
//    String[] reviewFile = {"/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/Test/t.txt"};
    String[] reviewFile = {
            "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/Test/u_14.txt",
            "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/Test/u_1.txt",
            "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/Test/u_5.txt",
            "/home/bruntha/Documents/Softwares/brat-v1.3_Crunchy_Frog/data/Eatery/Test/u_11.txt",
    };

    public static void main(String[] args) {
        PercentageCalculation percentageCalculation = new PercentageCalculation();
        percentageCalculation.calculate();
//        percentageCalculation.toString();
    }

    @Override
    public String toString() {
        return "PercentageCalculation{" +
                "noOfSentence=" + noOfSentence +
                ", noOf_1_Aspect=" + noOf_1_Aspect +
                ", noOf_2_Aspect=" + noOf_2_Aspect +
                ", noOf_3_Aspect=" + noOf_3_Aspect +
                '}';
    }

    public void calculate() {
        try {
            for (int i = 0; i < annFile.length; i++) {
                currentCount = 0;
                lastAnnIndex = 0;
                anns = new ArrayList();
                readAnnFile(annFile[i]);
                System.out.println("ann size " + anns.size());
                calculatePercentage(reviewFile[i]);
            }

            System.out.println("No of sentences " + noOfSentence);
            System.out.println("No of 1 aspect in one sentence " + noOf_1_Aspect);
            System.out.println("No of 2 aspect in one sentence " + noOf_2_Aspect);
            System.out.println("No of 3 aspect in one sentence " + noOf_3_Aspect);

            System.out.println("Percentage of 1 implicit aspect occurrence sentences " + noOf_1_Aspect * 100.0 / noOfSentence * 1.0 + "%");
            System.out.println("Percentage of 2 implicit aspect occurrence sentences " + noOf_2_Aspect * 100.0 / noOfSentence * 1.0 + "%");
            System.out.println("Percentage of 3 implicit aspect occurrence sentences " + noOf_3_Aspect * 100.0 / noOfSentence * 1.0 + "%");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readAnnFile(String filePath) throws IOException {
        File fileAnnotation = new File(filePath);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            String[] annotations = line.split("[ \t]");
            String word = line.substring(line.lastIndexOf('\t') + 1);
            if (!annotations[1].matches("Food") && !annotations[1].matches("F_FoodItem")) {
                if (annotations[1].startsWith("P"))
                    anns.add(new Ann(Integer.parseInt(annotations[2]), Integer.parseInt(annotations[3]), annotations[1], word));
            }

        }
        br.close();
        fr.close();
    }

    public void calculatePercentage(String filePath) throws IOException {
        File fileAnnotation = new File(filePath);
        FileReader fr = new FileReader(fileAnnotation);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            String[] sentences = line.split("\\.");
            for (int i = 0; i < sentences.length; i++) {
                currentCount += sentences[i].length();
                noOfSentence++;
                updateAspectCount(sentences[i], currentCount);
            }
        }
        br.close();
        fr.close();
    }

    public void updateAspectCount(String sentence, int countMax) {
        int countAspect = 0;
//        System.out.println(sentence);
        for (int i = lastAnnIndex; i < anns.size(); i++) {
            Ann ann = (Ann) anns.get(i);
            if (ann.getEndIndex() <= countMax) {
                System.out.println(ann.getApsect() + "\t" + ann.getWord());
                countAspect++;
            } else {
                lastAnnIndex = i;
                break;
            }
        }
//        System.out.println(countAspect);
        switch (countAspect) {
            case 1:
                noOf_1_Aspect++;
                break;
            case 2:
                noOf_2_Aspect++;
                break;
            case 3:
                noOf_3_Aspect++;
                break;
            default:
        }
    }

    public boolean isImplicit(Ann ann) {
        if (ann.getApsect().equalsIgnoreCase(ann.getWord()))
            return false;
        else if (ann.getApsect().toLowerCase().contains(ann.getWord().toLowerCase()))
            return false;
        else if (ann.getApsect().matches("A_LocatedArea") && ann.getWord().matches("location"))
            return false;
        else
            return true;
    }
}
