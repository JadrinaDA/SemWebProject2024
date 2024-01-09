package semweb_project2;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class describe {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// Get preferences and save to Linked data platform
		String serviceUrl = "http://193.49.165.77:3000/semweb/de-andrade-e-silva-workspace/";
				
		// Get preferences from user
		Scanner scan = new Scanner(System.in);  // Create a Scanner object
		System.out.println("What is your username?");
	    String username = scan.nextLine();
	    System.out.println("What is your full name?");
	    String name = scan.nextLine();
		System.out.println("What day do you want to order? Enter day of week as number:");

	    String day = scan.nextLine();  // Read user input
	    System.out.println("What time? (hh:mm)");
	    String time = scan.nextLine();
	    String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
		String userDayOfWeek = daysOfWeek[Integer.parseInt(day)];
		String userHour = time.split(":")[0];
		String userMinute = time.split(":")[1];
		System.out.println("Looking up restaurant for: " + userDayOfWeek + " at " + time);  // Output user input
		
		System.out.println("What is your latitude? (float)");
	    String templat = scan.nextLine();
	    System.out.println("What is your longitude? (float)");
	    String templon = scan.nextLine();
		Double userLat = Double.parseDouble(templat);
		Double userLon = Double.parseDouble(templon);
		
		System.out.println("What is your desired maximum distance/radius?");
	    String temprad = scan.nextLine();
		Integer radius = Integer.parseInt(temprad);
		Double cos = Math.cos(userLat * Math.PI /180);
		System.out.println(cos);
		System.out.println("What is your desired maximum price?");
		Integer userLim = Integer.parseInt(scan.nextLine());
		scan.close();
		
		// Make turtle RDF
		Model model = ModelFactory.createDefaultModel();
		String schema = "https://schema.org/";
		Resource priceSpec = model.createResource()
				.addProperty(RDF.type, model.createResource(schema+"priceSpecification"))
				.addProperty(model.createProperty(schema+"maxPrice"), model.createTypedLiteral(userLim))
				.addProperty(model.createProperty(schema+"priceCurrency"), "EUR");
		Resource geoMidpoint = model.createResource()
				.addProperty(model.createProperty(schema+"latitude"), model.createTypedLiteral(userLat))
                .addProperty(model.createProperty(schema+"longitude"),model.createTypedLiteral(userLon));
		Resource geoWithin = model.createResource()
				.addProperty(RDF.type, model.createResource(schema+"geoCircle"))
				.addProperty(model.createProperty(schema+"geoMidpoint"), geoMidpoint)
				.addProperty(model.createProperty(schema+"geoRadius"), model.createTypedLiteral(radius));
		Resource avaof = model.createResource()
				.addProperty(model.createProperty(schema+"geoWithin"), geoWithin);
		Resource seeks = model.createResource()
				.addProperty(model.createProperty(schema+"priceSpecification"), priceSpec)
				.addProperty(model.createProperty(schema+"availableAtOrFrom"), avaof)
				.addProperty(model.createProperty(schema+"dayOfWeek"), userDayOfWeek)
				.addProperty(model.createProperty(schema+"availabilityStarts"), model.createTypedLiteral(time, XSDDatatype.XSDtime));
		Resource user = model.createResource("#user"+username)
				.addProperty(RDF.type, model.createResource(schema+"Person"))
				.addProperty(model.createProperty(schema+"name"), name)
				.addProperty(model.createProperty(schema+"seeks"), seeks);
				
		
		// Put preferences in a temp file
		OutputStream out;
		try {
			out = new FileOutputStream("temp.ttl");
			model.write(out, "Turtle") ;
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Path file = Paths.get("temp.ttl");
		
		// Build request
		OkHttpClient client = new OkHttpClient().newBuilder()
				  .build();
				MediaType mediaType = MediaType.parse("text/turtle");
				RequestBody body = RequestBody.create(mediaType, file.toFile());
				Request request = new Request.Builder()
				  .url("http://193.49.165.77:3000/semweb/de-andrade-e-silva-workspace/")
				  .method("POST", body)
				  .addHeader("Content-Type", "text/turtle")
				  .addHeader("Slug", username+".ttl")
				  .build();
				Response response = client.newCall(request).execute();
		

	}

}
