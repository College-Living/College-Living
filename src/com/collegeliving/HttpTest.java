package com.collegeliving;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpTest {

	public HttpTest() {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost("149.47.177.209/test.php");
		try {
			post.setEntity(new StringEntity("test"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		post.addHeader("Content-type", "application/json");
		post.addHeader("Content-length", post.getEntity().getContentLength()+"");
		HttpResponse response = null;
		try {
			response = httpClient.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();
		System.out.printf("%s", entity.toString());
	}
	
	public static void main(String args[]) {
		HttpTest test = new HttpTest();
	}
	
}
