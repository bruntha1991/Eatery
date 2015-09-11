package eatery.synonyms;

import apiClient.ApacheHttpRestClient;
import utilities.Utility;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bruntha on 9/7/15.
 */
public class PyDictionary {


    public static ArrayList<String> findSynonyms(String word) {
        ArrayList<String> synonyms=new ArrayList<>();
        Pattern pattern = Pattern.compile("[a-zA-Z]");
        Matcher matcher = pattern.matcher(word);
        if (!matcher.find() || word.contains("\"") || word.contains("-")|| word.contains("\\")) {
            return synonyms;
        }else {
            String response=ApacheHttpRestClient.getHTTPGetResponse("http://pydictionary-geekpradd.rhcloud.com/api/synonym/" + word);
            response=response.replace("[","");
            response=response.replace("]","");
            response=response.replace("\"","");

            String[] d=response.split(",");
            for (int i = 0; i < d.length; i++) {
                synonyms.add(d[i].trim());
            }
            return synonyms;
        }
    }

    public static void main(String[] args) {
        Utility.printArrayList(PyDictionary.findSynonyms("staff"));
    }
}
