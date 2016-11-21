package flume.sinks;

import java.util.ArrayList;
import java.util.List;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParamsNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class MySink extends AbstractSink implements Configurable {
	private String myProp = "";
	private String jbossurl = "";

	@Override
	public void configure(Context context) {
		String myProp = context.getString("myProp", "defaultValue");
		String jbossurl = context.getString("jbossurl", "defaultValue");
		this.myProp = myProp;
		this.jbossurl = jbossurl;
	}

	@Override
	public void start() {
		System.out.println("MySink start ****************************");
	}

	@Override
	public void stop() {
		System.out.println("MySink stop ****************************");
	}

	@Override
	public Status process() throws EventDeliveryException {
		Status status = null;
		Channel ch = getChannel();
		Transaction txn = ch.getTransaction();
		txn.begin();
		try {
			Event event = ch.take();
			String msg = new String(event.getBody(), "UTF-8");
			System.out.println("body:" + msg);
			sendRequest(msg);
			txn.commit();
			status = Status.READY;
		} catch (Throwable t) {
			txn.rollback();
			status = Status.BACKOFF;
			if (t instanceof Error) {
				throw (Error) t;
			}
		} finally {
			txn.close();
		}
		return status;
	}

	private void sendRequest(String msg) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			//			HttpPost httpost = new HttpPost(
			//					"http://172.16.211.5:8080/SSLGW-LogApp/FlumeLogServlet");
			HttpPost httpost = new HttpPost(jbossurl);
			System.out.println("jbossurl:" + jbossurl);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("log", msg));
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			HttpResponse response = httpclient.execute(httpost);
			// HttpEntity entity = response.getEntity();
			// Header[] headers = response.getAllHeaders();
			// for (Header header : headers) {
			// System.out
			// .println(header.getName() + " = " + header.getValue());
			// }
			// System.out.println("releaseQeID form get: "
			// + response.getStatusLine());
			// String result = EntityUtils.toString(entity);
			// System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}

	}
}
