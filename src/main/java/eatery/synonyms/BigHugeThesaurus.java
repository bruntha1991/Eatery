package eatery.synonyms;

import apiClient.JSONParser;
import apiClient.ServiceHandler;
import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import utilities.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by bruntha on 9/1/15.
 */
public class BigHugeThesaurus {
    private ServiceHandler serviceHandler;
    private org.apache.http.HttpResponse httpResponse;
    private boolean isSuccessWebService;
    private HttpEntity httpEntity;
    private InputStream is;
    private JSONParser parsing;
    private JSONObject json;

    public ArrayList<String> findNounSynonyms(String word) {

        ArrayList<String> synonyms=new ArrayList<>();

        serviceHandler = new ServiceHandler();
        httpResponse = serviceHandler.makeServiceCallJson("http://words.bighugelabs.com/api/2/73f231686d9c1a4d79edc7b97b285e1c/"+word+"/json", 1, null);

        if(httpResponse.getStatusLine().getStatusCode()==200)
        {
            isSuccessWebService=true;
        }else if(httpResponse.getStatusLine().getStatusCode()==403){
            isSuccessWebService=false;
        }else if(httpResponse.getStatusLine().getStatusCode()==404){
            isSuccessWebService=false;
        }else {
            isSuccessWebService=false;
        }

        if(isSuccessWebService)
        {
            try {
                httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
                parsing = new JSONParser();
                json = parsing.getJSONFromResponse(is);

                JSONObject noun=json.getJSONObject("noun");
                JSONArray nounSyn=noun.getJSONArray("syn");

                for (int i = 0; i < nounSyn.length(); i++) {
                    synonyms.add(nounSyn.get(i).toString());
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return synonyms;
    }

    public ArrayList<String> findVerbSynonyms(String word) {

        ArrayList<String> synonyms=new ArrayList<>();

        serviceHandler = new ServiceHandler();
        httpResponse = serviceHandler.makeServiceCallJson("http://words.bighugelabs.com/api/2/73f231686d9c1a4d79edc7b97b285e1c/"+word+"/json", 1, null);

        if(httpResponse.getStatusLine().getStatusCode()==200)
        {
            isSuccessWebService=true;
        }else if(httpResponse.getStatusLine().getStatusCode()==403){
            isSuccessWebService=false;
        }else if(httpResponse.getStatusLine().getStatusCode()==404){
            isSuccessWebService=false;
        }else {
            isSuccessWebService=false;
        }

        if(isSuccessWebService)
        {
            try {
                httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
                parsing = new JSONParser();
                json = parsing.getJSONFromResponse(is);

                JSONObject verb=json.getJSONObject("verb");
                JSONArray nounSyn=verb.getJSONArray("syn");

                for (int i = 0; i < nounSyn.length(); i++) {
                    synonyms.add(nounSyn.get(i).toString());
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return synonyms;
    }

    public static void main(String[] args) {
        BigHugeThesaurus bigHugeThesaurus=new BigHugeThesaurus();
        Utility.printArrayList(bigHugeThesaurus.findNounSynonyms("employee"));
    }
}
