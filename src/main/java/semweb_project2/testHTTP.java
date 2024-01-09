package semweb_project2;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class testHTTP {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		OkHttpClient client = new OkHttpClient().newBuilder()
				  .build();
				MediaType mediaType = MediaType.parse("text/turtle");
				RequestBody body = RequestBody.create(mediaType, "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\nPREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\nPREFIX : <http://www.my.personal.name.space.org>\r\nPREFIX v: <http://www.todaysvocab.com>\r\nPREFIX mysw: <http://193.49.165.77:3000/semweb/jadrina>\r\n\r\n# ↓↓↓ Write your code down here ↓↓↓\r\nmysw:Warranty1 a mysw:OptionalWarranty;\r\n             v:hasDurationInYears 5;\r\n             v:hasPriceInEuros 10.90.");
				Request request = new Request.Builder()
				  .url("http://193.49.165.77:3000/semweb/de-andrade-e-silva-workspace/")
				  .method("POST", body)
				  .addHeader("Content-Type", "text/turtle")
				  .addHeader("Slug", "jadrina-warranty2")
				  .build();
				Response response = client.newCall(request).execute();

	}

}
