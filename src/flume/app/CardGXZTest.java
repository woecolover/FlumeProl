package flume.app;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class CardGXZTest {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httpost = new HttpPost(
					"http://localhost:8080/eIDOrgService/icbc/cardGXZ?s=00");

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			nvps.add(new BasicNameValuePair("eIDRequest",
					"[D]D43=15000001&D46=3"));

			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			HttpResponse response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();
			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				System.out
						.println(header.getName() + " = " + header.getValue());
			}

			System.out.println("releaseQeID form get: "
					+ response.getStatusLine());
			String result = EntityUtils.toString(entity);
			System.out.println(result);

		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

}
