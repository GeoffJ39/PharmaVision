package hackthesix2019;
 
import java.io.IOException;
import java.util.*;
 
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
 
public class InternetConnec {
	public static void main(String[] args) {
		//getInfo("00559407");
	}
    public static List<String> getInfo(String input){
        print("Fetching Drug Information...");
        Document drugDinDoc;
       
       
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        //String din =  "00559407"; //"02243690" //dextromethorphan -- 02252554; tylenol -- 00559407; ibuprofen -- 02368072
        String din = input;
        System.out.println(din);
        List<String> medList=new LinkedList<String>();
        medList.add("dextromethorpan");
        medList.add("tylenol");
       
        List<String> activeIng = new LinkedList<String>();
        List<String> foodInteract = new LinkedList<String>();
        List<String> str = new LinkedList<String>();
 
   
        try {
            //Takes given DIN and searchs in canada database
            String dinURL  = "https://health-products.canada.ca/dpd-bdpp/index-eng.jsp";
            Connection.Response dinResp = Jsoup.connect(dinURL)
                                            //.timeout(3000)
                                            .method(Connection.Method.GET)
                                            .userAgent(USER_AGENT)
                                            .execute();
            FormElement searchForm = (FormElement) dinResp.parse()
                                                    .select("form").get(1);
            checkElem("Search form", searchForm);
            Element dinField = searchForm.select("#din").first();
           
           
            checkElem("dinField", dinField);
            dinField.val(din);
           
            Connection.Response searchActionResponse = searchForm.submit()
                                                                .cookies(dinResp.cookies())
                                                                .userAgent(USER_AGENT)
                                                                .execute();
   
            //drugDinDoc == drug page associated with given DIN
            //Now searching for drug name within page
           
            drugDinDoc = searchActionResponse.parse();
            Element drug = drugDinDoc.select("main, div.row").get(7);
            /*
             * Getting drug name
             */
            String drugName = drug.select("p.col-sm-8").text();
            System.out.println();
            print("Product Name: " + drugName);
            System.out.println();
            /*
             * Getting active ingr.
             */
            Elements active = drugDinDoc.selectFirst("main, div.table-responsive mrgn-tp-lg")
                                        .getElementsByTag("td");
            int size = active.size();
            int i = 0;
           
            while(i<size) {
                activeIng.add(active.get(i).text());
                i++;
                str.add(active.get(i).text());
                i++;
            }
           
            for(i = 0; i<activeIng.size(); i++) {
                System.out.println("Active Ing \t \tStrength");
                System.out.println(activeIng.get(i) + "\t\t" + str.get(i));
            }
        
   
/*
 *
 *
 *
 *PINK SITE BEGINS HERE -- DRUGBANK
 *
 *
 *
 */
            String drugURL = "https://www.drugbank.ca/";
            Connection.Response drugResp = Jsoup.connect(drugURL)
                                                .method(Connection.Method.GET)
                                                .userAgent(USER_AGENT)
                                                .execute();
            FormElement inputForm = (FormElement) drugResp.parse().select("form").first();
            checkElem("inputForm", inputForm);
            Element drugInput = inputForm.selectFirst("#query");
            checkElem("drugInput", drugInput);
            drugInput.val(drugName);
           
            Connection.Response drugPageResp = inputForm.submit()
                                                        .cookies(drugResp.cookies())   
                                                        .userAgent(USER_AGENT)
                                                        .execute();
            //Sets up connection to site
            String url = drugPageResp.parse().location();
           
            Connection.Response drugPage = Jsoup.connect(url)
                    .timeout(3000)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .execute();
           
            drugPage.bufferUp();
                               
            //Finds elements to search within
            Element body = drugPage.parse().select("div.content-container, "
                    + "div.card-content px-md-4 px-sm-2 pb-md-4 pb-sm-2, dl").get(3);
            checkElem("body", body);
           
            Elements foodInt = body.child(3).child(0).select("li");
            if( foodInt.eachText().size() > 0) {
                foodInteract = foodInt.eachText();
            }
            else {
                foodInteract.add("No food requirements");
            }
           
            System.out.println();
            int size2 = foodInteract.size();
            for(int j=0; j<size2; j++) {
                System.out.println(foodInteract.get(j));
            }                      
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
        //return activeIng;
		return activeIng;
    }
 
    public static void print(String string) {
        System.out.println(string);
    }
    
    public static LinkedList<String> findConflictInfo (String activeIngredient, LinkedList<String> medList){
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        List<String> interactionCodes = new LinkedList<String>();
        List<String> conflictInfo = new LinkedList<String>();
        int i;
        try {
 
            /****************************************
             *
             * LOOKING FOR DRUG INTERACTIONS
             *
             ****************************************/
            //needs activeIng
            String ingredientSearch = activeIngredient;
            if(ingredientSearch.contains(" ")) {
                ingredientSearch = activeIngredient.substring(0, ingredientSearch.indexOf(" "));
            }
           
            /*****************************************
             *
             * SUCCESSFULLY EXTRACTS ID FROM GIVEN --INGREDIENTSEARCH
             *
             *****************************************/
           
            String interactionsURL = "https://www.drugs.com/"+ingredientSearch+".html";
            Connection.Response interactPage = Jsoup.connect(interactionsURL)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .execute();
            Document interactDoc = interactPage.parse();
            Element idNums = interactDoc.getElementById("pronounce-audio").selectFirst("source");
            checkElem("idnums", idNums);
            String ogDrugID = idNums.attributes().html();
            ogDrugID = ogDrugID.substring(17, ogDrugID.indexOf("."));
           
            print("OGDrugID: "+ogDrugID);
            String conflictURL = "https://www.drugs.com/"+medList.get(0)+".html";
            
            
            Connection.Response conflictPage = Jsoup.connect(conflictURL)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .execute();
            conflictPage.bufferUp();
            Document conflictDoc = conflictPage.parse();
            Element newIDNums = conflictDoc.getElementById("pronounce-audio").selectFirst("source");
            checkElem("idnums", newIDNums);
            String newDrugID = newIDNums.attributes().html();
            newDrugID = newDrugID.substring(17, newDrugID.indexOf("."));
            interactionCodes.add(newDrugID);
            print("CONFLICT:\n");
           
            for(int k=1; k<medList.size(); k++) {
               
                conflictURL = "https://www.drugs.com/"+medList.get(k)+".html";
                conflictPage = Jsoup.connect(conflictURL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .execute();
                conflictDoc = conflictPage.parse();
                newIDNums = conflictDoc.getElementById("pronounce-audio").selectFirst("source");
                checkElem("idnums", newIDNums);
                newDrugID = newIDNums.attributes().html();
                newDrugID = newDrugID.substring(17, newDrugID.indexOf("."));
                interactionCodes.add(newDrugID);               
            }
           
            //for(int p = 0; p<interactionCodes.size(); p++) {
            //    print(interactionCodes.get(p));
            //}
            /**********************
             *
             * interactionCodes: LinkedList<String>
             * conflictInfo: LinkedList<String>
             * ogDrugID: string
             * Now populating conflictInfo linked list
             *
             **********************/
            String finalURL = "https://www.drugs.com/interactions-check.php?drug_list="+ogDrugID+","+interactionCodes.get(0);
           
            Connection.Response finalPage = Jsoup.connect(finalURL)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .execute();
           
            finalPage.bufferUp();
            Document finDoc = finalPage.parse();
            Element para = finDoc.selectFirst("p.status-box");
            String info;
            try {
                info = ( (TextNode) para.childNode(0)).text();
            }
            catch (NullPointerException exception){
                info = "No Relevant Conflicts";
            }           conflictInfo.add(info);
           
            for(i=1; i<interactionCodes.size(); i++) {
                finalURL = "https://www.drugs.com/interactions-check.php?drug_list="+ogDrugID+","+interactionCodes.get(i);
               
                finalPage = Jsoup.connect(finalURL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .execute();
               
                finDoc = finalPage.parse();
                para = finDoc.selectFirst("p.status-box");
                try {
                    info = ((TextNode) para.childNode(0)).text();
                }
                catch (NullPointerException exception){
                    info = "No Relevant Conflicts";
                }
                conflictInfo.add(info);
            }
           
            for(i=0; i<conflictInfo.size(); i++) {
            	System.out.print(interactionCodes.get(i));
            	System.out.print(": ");
            	System.out.println(medList.get(i));
                print(conflictInfo.get(i));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }  
        return (LinkedList) conflictInfo;
    }
    
    public static void findConflictInfoNew (String activeIng, List<String> medList) {
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
        List<String> interactionCodes = new LinkedList<String>();
        List<String> activeIngCodes = new LinkedList<String>();
        List<String> ingrFoodInteractions = new LinkedList<String>();
        List<String> renamed = new LinkedList<String>(); //substring list of active ingredients
        List<String> conflictInfo = new LinkedList<String>();
        int i;
        try {
 
            /****************************************
             *
             * LOOKING FOR DRUG INTERACTIONS
             *
             ****************************************/
            //needs activeIng
             String rename = activeIng;
             if(rename.contains(" ")){
                   rename = rename.substring(0, activeIng.indexOf(" "));
        
            }
           
   
            /*****************************************
             *
             * EXTRACTS ID FROM EACH INGREDIENT
             * renamed -- ingrList
             * activeIngCodes -- id codes
             * ingrFoodInteractions -- for the messages
             *
             *****************************************/
           
            String interactionsURL = "https://www.drugs.com/"+renamed.get(0)+".html";
            Connection.Response interactPage = Jsoup.connect(interactionsURL)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .execute();
           
            interactPage.bufferUp();
            Document interactDoc = interactPage.parse();
            Element idNums = interactDoc.getElementById("pronounce-audio").selectFirst("source");
            checkElem("idnums", idNums);
            String ogDrugID = idNums.attributes().html();
            ogDrugID = ogDrugID.substring(17, ogDrugID.indexOf("."));
            activeIngCodes.add(ogDrugID);
           
            for(i=1; i<renamed.size(); i++) {
                interactionsURL = "https://www.drugs.com/"+renamed.get(i)+".html";
                interactPage = Jsoup.connect(interactionsURL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .execute();
                interactDoc = interactPage.parse();
                idNums = interactDoc.getElementById("pronounce-audio").selectFirst("source");
                checkElem("idnums", idNums);
                ogDrugID = idNums.attributes().html();
                ogDrugID = ogDrugID.substring(17, ogDrugID.indexOf("."));
                activeIngCodes.add(ogDrugID);
            }  
           
            /************************
             *
             * BELOW: FINDING FOOD INTERACTIONS
             * FOR EACH ACTIVE INGREDIENT
             *
             * renamed -- ingrList
             * activeIngCodes -- id codes
             * ingrFoodInteractions -- for the messages
             *
             ************************/
           
           
            String conflictURL = "https://www.drugs.com/"+medList.get(0)+".html";
            Connection.Response conflictPage = Jsoup.connect(conflictURL)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .execute();
            conflictPage.bufferUp();
            Document conflictDoc = conflictPage.parse();
            Element newIDNums = conflictDoc.getElementById("pronounce-audio").selectFirst("source");
            checkElem("idnums", newIDNums);
            String newDrugID = newIDNums.attributes().html();
            newDrugID = newDrugID.substring(17, newDrugID.indexOf("."));
            interactionCodes.add(newDrugID);
            print("CONFLICT:\n");
           
            for(int k=1; k<medList.size(); k++) {
               
                conflictURL = "https://www.drugs.com/"+medList.get(k)+".html";
                conflictPage = Jsoup.connect(conflictURL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .execute();
                conflictDoc = conflictPage.parse();
                newIDNums = conflictDoc.getElementById("pronounce-audio").selectFirst("source");
                checkElem("idnums", newIDNums);
                newDrugID = newIDNums.attributes().html();
                newDrugID = newDrugID.substring(17, newDrugID.indexOf("."));
                interactionCodes.add(newDrugID);               
            }
           
            for(int p = 0; p<interactionCodes.size(); p++) {
                print(interactionCodes.get(p));
            }
            /**********************
             *
             *
             * interactionCodes: LinkedList<String>
             * conflictInfo: LinkedList<String>
             * ogDrugID: string
             * Now populating conflictInfo linked list
             *
             **********************/
            String finalURL = "https://www.drugs.com/interactions-check.php?drug_list="+ogDrugID+","+interactionCodes.get(0);
           
            Connection.Response finalPage = Jsoup.connect(finalURL)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .execute();
           
            finalPage.bufferUp();
            Document finDoc = finalPage.parse();
            Element para;
            Element warningBox;
           
            String info;
            try {
                para = finDoc.select("div.interactions-reference, p").get(2).child(1);
                info = para.text();
            }
            catch (NullPointerException exception){
                info = "No Relevant Conflicts";
            }           conflictInfo.add(info);
           
            for(i=1; i<interactionCodes.size(); i++) {
                finalURL = "https://www.drugs.com/interactions-check.php?drug_list="+ogDrugID+","+interactionCodes.get(i);
               
                finalPage = Jsoup.connect(finalURL)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .execute();
               
                finDoc = finalPage.parse();
                para = finDoc.selectFirst("p.status-box");
                try {
                    info = ((TextNode) para.childNode(0)).text();
                }
                catch (NullPointerException exception){
                    info = "No Relevant Conflicts";
                }
                conflictInfo.add(info);
            }
           
            for(i=0; i<conflictInfo.size(); i++) {
                print(conflictInfo.get(i));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }  
        //return (LinkedList) conflictInfo;
    }
   
    public boolean isAllergic(List<String> allgList, List<String> ingr) {
        for(int i = 0; i<ingr.size(); i++) {
            for(int j = 0; j<allgList.size(); j++) {
                if( ingr.get(i) == allgList.get(j))
                    return false;
            }
        }
        return true;
    }
   
    public static void checkElem(String name, Element elem) {
        if (elem == null) {
            throw new RuntimeException("Unable to find "+ name);
        }
    }
}