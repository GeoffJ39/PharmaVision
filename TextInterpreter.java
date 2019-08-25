package hackthesix2019;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.TextDetection;

//import org.apache.commons.lang3.math.NumberUtils;

public class TextInterpreter {
	public static void main(String[] args) throws Exception {
		print(getText("chloe advil.jpg"));
	}
	
	public static String getText(String imgPath) throws Exception {
		String text = "Fuck off";
		String din = "-1";
		
		System.setProperty("aws.accessKeyId", "AKIAZTV6LSMNHTQV6WFK");
		System.setProperty("aws.secretKey", "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");
		
		AmazonRekognition rk = AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		byte[] bytes = Files.readAllBytes(new File(imgPath).toPath());
		File n = new File(text);
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		 
		Image img = new Image().withBytes(byteBuffer);
		DetectTextRequest req = new DetectTextRequest();
		req.withImage(img);
		
		DetectTextResult res = rk.detectText(req);
		
		for ( TextDetection d : res.getTextDetections() ) {
		    text = d.getDetectedText();
		    text = text.replace(" ", "");
		    if (text.contains("DIN") && text.length() == 11) {
		    	text = text.replace("DIN", "");
		    	print("Found DIN and Number");
		    	din = text;
		    } else if (Pattern.matches("[a-zA-Z]+", text) == false && text.length() > 2 && text.length() == 8) {
	    	    print("Found Number");
	    	    din = text;
	    	}
		    //float width = d.getGeometry().getBoundingBox().getWidth();
		}
		//print(res.toString());	
		return din;
	}
	
	public static void print(String text) {
		System.out.println(text);
	}
}


