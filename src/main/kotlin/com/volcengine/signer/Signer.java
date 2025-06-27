package com.volcengine.signer;

import java.net.URI;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.util.EntityUtils;

public class Signer {

  private static final TimeZone tz = TimeZone.getTimeZone("UTC");
  private static final Set<String> H_INCLUDE = new HashSet<String>();
  private static final String TIME_FORMAT_V4 = "yyyyMMdd'T'HHmmss'Z'";

  static {
    H_INCLUDE.add("Content-Type");
    H_INCLUDE.add("Content-Md5");
    H_INCLUDE.add("Host");
  }

  public void sign(HttpUriRequest request, Credentials credentials) throws Exception {
    if ("".equals(request.getURI().getPath())) {
      URI uri = request.getURI();
      URIBuilder builder = new URIBuilder(uri);
      builder.setPath("/");
      if (request instanceof HttpRequestBase) {
        ((HttpRequestBase)request).setURI(builder.build());
      } else {
        throw new NullPointerException("Path can't be empty. If you don't have path for request, please use a '/' instead.");
      }
    }

    // common headers
    request.setHeader("Host", request.getURI().getHost());
    if (request.getHeaders("Content-Type") == null) {
      request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    }

    String formatDate = getCurrentFormatDate();
    request.setHeader("X-Date", formatDate);

    MetaData meta = new MetaData();
    meta.setAlgorithm("HMAC-SHA256");
    meta.setService(credentials.getService());
    meta.setRegion(credentials.getRegion());
    meta.setDate(toDate(formatDate));

    // step 1
    String hashedCanonReq = hashedCanonicalRequestV4(request, meta);

    meta.setCredentialScope(StringUtils
        .join(new String[]{meta.getDate(), meta.getRegion(), meta.getService(), "request"}, "/"));
    // step 2
    String stringToSign = StringUtils.join(new String[]{meta.getAlgorithm(), formatDate, meta.getCredentialScope(), hashedCanonReq}, "\n");

    // step 3
    byte[] signingKey = genSigningSecretKeyV4(credentials.getSecretAccessKey(), meta.getDate(), meta.getRegion(), meta.getService());
    String signature = Hex.encodeHexString(Utils.hmacSHA256(signingKey, stringToSign));
    request.setHeader("Authorization", buildAuthHeaderV4(signature, meta, credentials));
  }

  private String hashedCanonicalRequestV4(HttpUriRequest request, MetaData meta) throws Exception {
    byte[] body = new byte[0];

    if (request instanceof HttpEntityEnclosingRequestBase) {
      HttpEntity entity = ((HttpEntityEnclosingRequestBase)request).getEntity();
      if (entity != null) {
        body = EntityUtils.toByteArray(entity);
      }
    }

    String bodyHash = Utils.hashSHA256(body);
    request.setHeader("X-Content-Sha256", bodyHash);

    List<String> signedHeaders = new ArrayList<>();
    for (Header header : request.getAllHeaders()) {
      String headerName = header.getName();
      if (H_INCLUDE.contains(headerName) || headerName.startsWith("X-")) {
        signedHeaders.add(headerName.toLowerCase());
      }
    }
    Collections.sort(signedHeaders);
    StringBuilder signedHeadersToSignStr = new StringBuilder();
    for (String h : signedHeaders) {
      String value = request.getFirstHeader(h).getValue().trim();
      if (h.equals("host")) {
        if (value.contains(":")) {
          String[] split = value.split(":");
          String port = split[1];
          if (port.equals("80") || port.equals("443")) {
            value = split[0];
          }
        }
      }
      signedHeadersToSignStr.append(h).append(":").append(value).append("\n");
    }

    meta.setSignedHeaders(StringUtils.join(signedHeaders, ";"));

    String canonicalRequest = StringUtils.join(new String[]{
        request.getMethod(),
        normUri(request.getURI().getPath()),
        normQuery(URLEncodedUtils.parse(request.getURI().getRawQuery(), Consts.UTF_8, '&')),
        signedHeadersToSignStr.toString(),
        meta.getSignedHeaders(), bodyHash},
        "\n");

    return Utils.hashSHA256(canonicalRequest.getBytes());
  }

  private byte[] genSigningSecretKeyV4(String secretKey, String date, String region, String service) throws Exception {
    byte[] kDate = Utils.hmacSHA256((secretKey).getBytes(), date);
    byte[] kRegion = Utils.hmacSHA256(kDate, region);
    byte[] kService = Utils.hmacSHA256(kRegion, service);
    return Utils.hmacSHA256(kService, "request");
  }

  private String buildAuthHeaderV4(String signature, MetaData meta, Credentials credentials) {
    String credential = credentials.getAccessKeyID() + "/" + meta.getCredentialScope();

    return meta.getAlgorithm() +
        " Credential=" + credential +
        ", SignedHeaders=" + meta.getSignedHeaders() +
        ", Signature=" + signature;
  }

  private String getCurrentFormatDate() {
    DateFormat df = new SimpleDateFormat(TIME_FORMAT_V4);
    df.setTimeZone(tz);
    return df.format(new Date());
  }

  private String toDate(String timestamp) {
    return timestamp.substring(0, 8);
  }

  private String normUri(String path) {
    return URLEncoder.encode(path).replace("%2F", "/").replace("+", "%20");
  }

  private String normQuery(List<NameValuePair> params) {
    Collections.sort(params, NameValueComparator.INSTANCE);
    String query = URLEncodedUtils.format(params, Consts.UTF_8);
    return query.replace("+", "%20");
  }

}
