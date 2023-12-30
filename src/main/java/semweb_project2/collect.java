package semweb_project2;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.vocabulary.RDF;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class collect {
	
	public static void main(String args[])
	{
		JSONArray o;
		Model model = ModelFactory.createDefaultModel();
		String schema = "https://schema.org/";
		Validator validator = new Validator("shapes.ttl");
		int counter = 0;
		
		try {
			o = (JSONArray) new JSONParser().parse(new FileReader("coopcycle.json"));
			//load coopcycle member from uri
            if (args.length > 0)
            {
            	for (String arg: args)
            	{
                	JSONObject jsonObj = new JSONObject();
                	jsonObj.put("coopcycle_url", arg);
                	jsonObj.put("country", "unknown");
                	jsonObj.put("city", "unknown");
            		o.add(0, jsonObj);
            	}
            }
			//for (int i = 0; i < o.size(); i++)
			for (int i = 0; i < 3; i++)
			{
				JSONObject service = (JSONObject) o.get(i);
				//JSONObject text = (JSONObject) service.get("text");
				String ccurl = "";
				if (service.get("coopcycle_url") != null)
				{
					ccurl = (String) service.get("coopcycle_url");
				}
				else if (service.get("delivery_form_url") != null){
					ccurl = (String) service.get("delivery_form_url");
				}
				else
				{
					ccurl = (String) service.get("url");
				}
				if (ccurl == null)
				{
					continue;
				}
				
				try {
		            // Replace the URL with the actual URL of the website you want to parse
		            Document document = Jsoup.connect(ccurl).get();

		            // Select all div elements with class "restaurant-item"
		            Elements restaurantItems = document.select("div.restaurant-item>a[href]");
		            Set<String>setRI = new HashSet();
		            for (Element restaurantHref: restaurantItems)
		            {
		            	setRI.add(ccurl + restaurantHref.attr("href"));
		            }
		           

		            // Iterate over each div element
		            for (String href : setRI) {
		                // Select the 'a' element within each div and get its 'href' attribute
		                
		            	//System.out.println(href);
		            	try {
		                        Document restaurantDocument = Jsoup.connect(href).get();
		                        Elements jsonScripts = restaurantDocument.select("script[type=application/ld+json]");
		                        
		                        //System.out.println(jsonScripts.size());

		                        for (Element jsonScript : jsonScripts) {
		                        	JSONParser parser = new JSONParser();
		                        	JSONObject jsonText = (JSONObject) parser.parse(jsonScript.data());
		                            //System.out.println("JSON Object: " + jsonText);
		                            Resource address = model.createResource()
		                                    .addProperty(RDF.type, model.createResource(schema+"PostalAddress"))
		                                    .addProperty(model.createProperty(schema+"addressLocality"), (String) service.get("city"))
		                                    .addProperty(model.createProperty(schema+"addressCountry"), ((String) service.get("country")).toUpperCase());
		                            Double lat =(Double) ((JSONObject)((JSONObject) jsonText.get("address")).get("geo")).get("latitude");
		                            Double lon =(Double) ((JSONObject)((JSONObject) jsonText.get("address")).get("geo")).get("longitude");
		            				Resource location = model.createResource()
		            						                 .addProperty(RDF.type, model.createResource(schema+"Place"))
		            						                 .addProperty(model.createProperty(schema+"address"), address)
		            						                 .addProperty(model.createProperty(schema+"latitude"), model.createTypedLiteral(lat))
		            						                 .addProperty(model.createProperty(schema+"longitude"),model.createTypedLiteral(lon));
		            			
		            				
		            				String name = (String) ((JSONObject) jsonText.get("address")).get("name");
		            				String phone = (String) ((JSONObject) jsonText.get("address")).get("telephone");
		            				JSONArray openingHoursArray = (JSONArray) jsonText.get("openingHoursSpecification");

		            				// Get the price
		            				Double min = 0.0;
	            					Double delivery = 0.0;
		            				try {
			            				JSONObject ETV = (JSONObject)((JSONObject) ((JSONObject)jsonText.get("potentialAction")).get("priceSpecification")).get("eligibleTransactionVolume");
			            				min = Double.parseDouble((String) ETV.get("price"));
			            				delivery = Double.parseDouble((String) ((JSONObject)((JSONObject)jsonText.get("potentialAction")).get("priceSpecification")).get("price"));
			            				//String price = String.valueOf(min + delivery);
		            				}
		            				catch (NullPointerException exc)
		            				{
		            					System.out.println(exc);
		            					
		            				}
		            				
		            				Model oldmodel = model;
		            				
		            				Resource serv = model.createResource(href)
								             .addProperty(RDF.type, model.createResource(schema+"ProfessionalService"))
								             .addProperty(model.createProperty(schema+"location"), location)
								             .addProperty(model.createProperty(schema+"name"), name)
		            						 .addProperty(model.createProperty(schema+"telephone"), phone)
		            						 .addProperty(model.createProperty(schema+"priceRange"), model.createTypedLiteral(min+delivery));
		            				
		            				
		            				Resource[] ohSpecs = new Resource[openingHoursArray.size()*7];
		            				int countO = 0;
		            				for (Object openingHoursObject : openingHoursArray) {
		                                JSONObject openingHours = (JSONObject) openingHoursObject;
		                                List<String> days = (List<String>) openingHours.get("dayOfWeek");
		                                String opens = "1999-04-09T" + (String)openingHours.get("opens") + ":00";
		                                String closes = "1999-04-09T" + (String)openingHours.get("closes") +":00";
		                                
		                                // Format opening hours for each day
		                                for (String day : days) {
		                                	Resource ohSpec = model.createResource()
			                                		 .addProperty(RDF.type, model.createResource(schema+"OpeningHoursSpecification"))
			                                		 .addProperty(model.createProperty(schema+"opens"), model.createTypedLiteral(opens, XSDDatatype.XSDdateTime))
			                                		 .addProperty(model.createProperty(schema+"closes"), model.createTypedLiteral(closes, XSDDatatype.XSDdateTime));
			                

		                                	ohSpec.addProperty(model.createProperty(schema+"dayOfWeek"), day);
		                                	serv.addProperty(model.createProperty(schema+"openingHoursSpecification"), ohSpec);
		                                	ohSpecs[countO] = ohSpec;
		                                	countO += 1;
		                                }
		                                
		                            }
		            				
		            				Graph temp = serv.getModel().getGraph();
		            				Boolean valid = validator.IsValid(temp);
		            				if (!valid)
		            				{
		            					//https://stackoverflow.com/questions/15213476/build-a-method-to-delete-a-resource-using-jena-api
		            					model.removeAll(serv, null, (RDFNode) null);
		            				    model.removeAll(location, null, (RDFNode) null );
		            				    model.removeAll(address, null, (RDFNode) null);
		            				    for (int j = 0; j < countO; j++)
		            				    {
		            				    	model.removeAll(ohSpecs[j], null, (RDFNode) null);
		            				    }
		            				}
		            				
		            				
		                           
		                            
		            				
		            				counter += 1;
		                        }
		                    } catch (IOException e) {
		                        e.printStackTrace();
		                 }
//		            	Elements anchorElements = restaurantItem.select("a[href]");
//		                for (Element anchorElement : anchorElements) {
//		                    String href = anchorElement.attr("href");
//		                    if (href.contains("restaurant") && !href.contains("restaurants"))
//		                    {
//		                    	System.out.println(ccurl+href);
//		                    }
//		                    //System.out.println("Href: " + piece);
//		                }
		            }
		        } catch (IOException e) {
		            //e.printStackTrace();
		            continue;
		        }
				
			
						             
				
				//System.out.println(text);
									 
									 
				                     //.addProperty(model.createProperty(schema+"url"), (String) service.get("url"))
				                     //.addProperty(model.createProperty(schema+"url"), (String) service.get("facebook_url"));
				
			}
			
	        model.setNsPrefix("schema", schema);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		model.write(System.out, "Turtle") ;
		String datasetURL = "http://localhost:3030/coopcycle_dataset";
		String sparqlEndpoint = datasetURL + "/sparql";
		String sparqlUpdate = datasetURL + "/update";
		String graphStore = datasetURL + "/data";
		RDFConnection conneg = RDFConnectionFactory.connect(sparqlEndpoint,sparqlUpdate,graphStore);
		conneg.load(model); // add the content of model to the triplestore	

		
		// <https://schema.org/closes>     "2022-09-12T14:00:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>;
        //<https://schema.org/dayOfWeek>  "Saturday";
        //<https://schema.org/opens>      "2022-09-12T08:00:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>

		
	}
	

}
