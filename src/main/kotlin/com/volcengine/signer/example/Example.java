package com.volcengine.signer.example;

import com.volcengine.signer.Credentials;
import com.volcengine.signer.Signer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class Example {

  public static void main(String[] args) throws Exception {
    /* create credentials */
    Credentials credentials = new Credentials();
    credentials.setAccessKeyID("ak");
    credentials.setSecretAccessKey("sk");
    credentials.setRegion("cn-north-1");
    credentials.setService("cv");

    /* create signer */
    Signer signer = new Signer();

    /* create http client */
    CloseableHttpClient httpClient = HttpClients.createDefault();

    /* prepare request */
    HttpPost request = new HttpPost();
    request.setURI(new URI("https://visual.volcengineapi.com?Action=JPCartoon&Version=2020-08-26"));
    
    request.addHeader(HttpHeaders.USER_AGENT, "volc-sdk-java/v1.0.0");
    
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    nvps.add(new BasicNameValuePair("image_base64", ""));
    request.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
    
    signer.sign(request, credentials);

    /* launch request */
    CloseableHttpResponse response = httpClient.execute(request);

    /* status code */
    System.out.println(response.getStatusLine().getStatusCode());   // 200

    /* get response body */
    HttpEntity entity = response.getEntity();
    if (entity != null) {
      String result = EntityUtils.toString(entity);
      System.out.println(result);
    }

    /* close resources */
    response.close();
    httpClient.close();
  }
}
