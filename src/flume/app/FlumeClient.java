package flume.app;

import java.nio.charset.Charset;

import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

public class FlumeClient {

	public static void main(String[] args) {
		MyRpcClientFacade client = new MyRpcClientFacade();
		client.init("172.16.211.34", 41414);

		// Send 10 events to the remote Flume agent. That agent should be
		// configured to listen with an AvroSource.
		String cn = "PCI≤‚ ‘1";
		System.out.println(cn.length());
		String sampleData = "date=2013-07-26T09:12:06.000-08:00 dn=\"/C=CN/ST=01/L=00/L=00/O=00/OU=00/OU=00/CN=" + cn
				+ "\" ip=192.168.0.190 url=http://172.16.15.33:8080/error.html optType=- optResult=0";
		for (int i = 0; i < 1; i++) {
			client.sendDataToFlume(sampleData);
		}

		client.cleanUp();
		System.out.println("success !");
	}
}

class MyRpcClientFacade {
	private RpcClient client;
	private String hostname;
	private int port;

	public void init(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		this.client = RpcClientFactory.getDefaultInstance(hostname, port);
	}

	public void sendDataToFlume(String data) {
		Event event = EventBuilder.withBody(data, Charset.forName("UTF-8"));
		try {
			client.append(event);
		} catch (EventDeliveryException e) {
			client.close();
			client = null;
			client = RpcClientFactory.getDefaultInstance(hostname, port);
		}
	}

	public void cleanUp() {
		client.close();
	}

}
