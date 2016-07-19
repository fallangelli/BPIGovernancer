package processor;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import utils.JdbcUtils;
import utils.TransLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CheckRegionCodeProcessor {
  public static Integer ALL_CODE_COUNT = -1;
  private static Logger logger = TransLogger.getLogger(MergeProcessor.class);

  static {
    loadTotalCertCount(JdbcUtils.CHECK_DIC_CODE_TABLE_NAME);
  }

  public static boolean loadTotalCertCount(String tableName) {
    Connection conn = null;
    Statement statement = null;
    ResultSet result = null;
    String sql = "";
    try {
      conn = JdbcUtils.getOracleConnection();
      if (conn == null)
        return false;

      sql = "SELECT /*+ parallel(certificate 8) */ count(*) FROM " + tableName + " where is_valid is null ";
      statement = conn.createStatement();
      statement.setQueryTimeout(43200);
      result = statement.executeQuery(sql);
      if (result.next()) {
        ALL_CODE_COUNT = result.getInt(1);
      }

      return true;
    } catch (SQLException e) {
      logger.warning(sql);
      logger.warning(e.getMessage());
    } catch (Exception e) {
      logger.warning(sql);
      logger.warning(e.getMessage());

    } finally {
      try {
        JdbcUtils.releaseStatement(statement, result);
        JdbcUtils.releaseConn(conn);
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }
    return false;
  }


  public static String checkRegionCodeAlai(String regCode) {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      logger.severe(e.getMessage());
    }
    String retString = "";

    String result = CheckRegionCodeProcessor.get("http://hm.alai.net/index.php/Idcard/index/nb/" + regCode + "/", "UTF-8");
    if (result == null || result.length() <= 0)
      return "";

    String tmpTar = "";
    Pattern pattern = Pattern.compile("(?<=籍贯所在地区：)(.+?)(?=</td>)");
    Matcher matcherId = pattern.matcher(result);
    if (matcherId.find()) {
      tmpTar = matcherId.group(0);

      String regex = "([\u4e00-\u9fa5]+)";
      Matcher matcher1 = Pattern.compile(regex).matcher(tmpTar);
      while (matcher1.find()) {
        retString += matcher1.group(0);
      }
      if (retString.length() <= 0 || retString == null) {
        logger.info("行政区划编码不存在:" + regCode.substring(0, 6));
        return "";
      }

      logger.info("行政区划编码匹配成功 - " + regCode.substring(0, 6) + " : " + retString);
      return retString;
    } else {
      logger.info("行政区划编码不存在:" + regCode.substring(0, 6));
      return "";
    }
  }

  public static String checkRegionCodeIdcard(String regCode) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("q", regCode);
    String retString = "";

    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      logger.severe(e.getMessage());
    }

    String result = CheckRegionCodeProcessor.post("http://idcard.911cha.com/", params, "UTF-8");
    if (result == null || result.length() <= 0)
      return null;

    String tmpTar = "";
    Pattern pattern = Pattern.compile("行政区划编码不存在");
    Matcher matcherId = pattern.matcher(result);
    if (matcherId.find()) {
      logger.info("行政区划编码不存在:" + regCode.substring(0, 6));
      return "";
    }

    pattern = Pattern.compile("(?<=发证地：</span>)(.+?)(?=生　日)");
    matcherId = pattern.matcher(result);
    if (matcherId.find()) {
      tmpTar = matcherId.group(0);

      String regex = "([\u4e00-\u9fa5]+)";
      Matcher matcher1 = Pattern.compile(regex).matcher(tmpTar);
      while (matcher1.find()) {
        retString += matcher1.group(0);
      }
      logger.info("行政区划编码匹配成功:" + regCode.substring(0, 6));
      return retString;
    }
    return retString;
  }


  public static String checkRegionId5(String regCode) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("type", "3");
    params.put("checknum", regCode);
    String retString = "";

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      logger.severe(e.getMessage());
    }

    String result = CheckRegionCodeProcessor.post("http://d.id5.cn/desktop/parse.do", params, "GBK");
    if (result == null || result.length() <= 0)
      return null;

    result = result.replaceAll("\r|\n|\t|' '", "");

    String tmpTar = "";
    Pattern pattern = Pattern.compile("(?<=发证地：</td><td valign=\"bottom\">)(.+?)(?=</td>)");
    Matcher matcherId = pattern.matcher(result);
    if (matcherId.find()) {
      tmpTar = matcherId.group(0);

      String regex = "([\u4e00-\u9fa5]+)";
      Matcher matcher1 = Pattern.compile(regex).matcher(tmpTar);
      while (matcher1.find()) {
        retString += matcher1.group(0);
      }
      if (retString.compareToIgnoreCase("未知") == 0) {
        logger.info("行政区划编码不存在:" + regCode.substring(0, 6));
        return "";
      } else {
        logger.info("行政区划编码匹配成功:" + regCode.substring(0, 6) + " : " + retString);
        return retString;
      }
    } else {
      logger.info("行政区划编码不存在:" + regCode.substring(0, 6));
      return "";
    }
  }

  private static String post(String url, Map<String, String> params, String encode) {
    DefaultHttpClient httpclient = new DefaultHttpClient();
    String body = null;

    HttpPost post = postForm(url, params);

    body = invoke(httpclient, post, encode);

    httpclient.getConnectionManager().shutdown();

    return body;
  }

  private static String get(String url, String encode) {
    DefaultHttpClient httpclient = new DefaultHttpClient();
    String body = null;

    HttpGet get = new HttpGet(url);
    body = invoke(httpclient, get, encode);

    httpclient.getConnectionManager().shutdown();

    return body;
  }


  private static String invoke(DefaultHttpClient httpclient,
                               HttpUriRequest httpost, String encode) {
    int retryTimes = 10;
    int retriedTimes = 0;
    HttpResponse response = sendRequest(httpclient, httpost);

    while (response == null && retriedTimes++ < retryTimes) {
      response = sendRequest(httpclient, httpost);
    }
    if (retriedTimes > retryTimes) {
      logger.severe("重试10次仍无法获取" + httpost.getURI());
      return null;
    }


    String body = paseResponse(response, encode);
    return body;
  }

  private static String paseResponse(HttpResponse response, String encode) {
    HttpEntity entity = response.getEntity();

    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(entity.getContent(), encode));

      String tempbf;
      StringBuffer html = new StringBuffer(100);
      while ((tempbf = br.readLine()) != null) {
        html.append(tempbf + "\n");
      }
      return html.toString();
    } catch (IOException e) {
      logger.severe(e.getMessage());
    }
    return null;
  }

  private static HttpResponse sendRequest(DefaultHttpClient httpclient,
                                          HttpUriRequest httpost) {
    HttpResponse response = null;

    try {
      response = httpclient.execute(httpost);
    } catch (ClientProtocolException e) {
      logger.severe(e.getMessage());
    } catch (SocketException e) {
      logger.severe(e.getMessage());
    } catch (IOException e) {
      logger.severe(e.getMessage());
    } catch (Exception e) {
      logger.severe(e.getMessage());
    }
    return response;
  }

  private static HttpPost postForm(String url, Map<String, String> params) {

    HttpPost httpost = new HttpPost(url);
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();

    Set<String> keySet = params.keySet();
    for (String key : keySet) {
      nvps.add(new BasicNameValuePair(key, params.get(key)));
    }

    try {
      httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
    } catch (UnsupportedEncodingException e) {
      logger.severe(e.getMessage());
    }

    return httpost;
  }

  public static void main(String[] args) {
    String regCode = "140602198403021715";

    String result = CheckRegionCodeProcessor.checkRegionId5(regCode);
    logger.info(result);
//    result = CheckRegionCodeProcessor.checkRegionCodeAlai(regCode);
//    logger.info(result);
//    result = CheckRegionCodeProcessor.checkRegionCodeIdcard(regCode);
//    logger.info(result);
//
//    regCode = "140602198308021716";
//
//    result = CheckRegionCodeProcessor.checkRegionId5(regCode);
//    logger.info(result);
//    result = CheckRegionCodeProcessor.checkRegionCodeAlai(regCode);
//    logger.info(result);
//    result = CheckRegionCodeProcessor.checkRegionCodeIdcard(regCode);
//    logger.info(result);
//
//    regCode = "999999999999999999";
//
//    result = CheckRegionCodeProcessor.checkRegionCodeAlai(regCode);
//    logger.info(result);
  }
}
