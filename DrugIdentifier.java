package hackthesix2019;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class DrugIdentifier {
	public static void main(String[] args) throws Exception {
		LinkedList<String> allergies = new LinkedList<String>();
		List<String> drugIngredients = new LinkedList<String>();
		LinkedList<String> currentMedication = new LinkedList<String>();
		String drugIngredient = "";
		
		
		System.out.println("Type 'done' when finished adding allergies");
		while (true){
			Scanner sc = new Scanner(System.in);
			String allergy = sc.next();
			System.out.println(allergy);
			if (allergy.equals("done") == true) {
				break;
			} else {
				allergies.add(allergy);	
				
			}
		}

		System.out.println("Type 'done' when finished adding medication");
		while (true){
			Scanner sc = new Scanner(System.in);
			String medicineName = sc.next();
			System.out.println(medicineName);
			if (medicineName.equals("done") == true) {
				break;
			} else {
				currentMedication.add(medicineName);		
			}
		}

		String din = TextInterpreter.getText("C:/Users/Richa/eclipse-workspace/hackthesix2019/Tylenol.png");
		
		if (din.equals("-1") == true) {
			System.out.println("Unable to find DIN from image");
		} else {
			drugIngredients = InternetConnec.getInfo(din);
			//drugIngredient = drugIngredients.get(0);
		}
		
		din = "02335948";
		
		for (int i = 0; i < drugIngredients.size(); i++) {
			drugIngredient = drugIngredients.get(i);
			InternetConnec.findConflictInfoNew(drugIngredient, currentMedication);
		}
		
		//InternetConnec.findConflictInfo(drugIngredient, currentMedication);
		
		//C:\Users\Richa\eclipse-workspace\hackthesix2019\ReactineInfo.jpg"
		/*
		System.out.println("Printing allergies");
		for (int i = 0; i < allergies.size(); i++) {
			System.out.println(allergies.get(i));
		}
		
		System.out.println("Printing Active Drug Components");
		for (int i = 0; i < drugIngredients.size(); i++) {
			System.out.println(drugIngredients.get(i));
			System.out.println("one down!");
		}
		*/
	}
}
