package com.demo.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/2/23 4:55
 * @since 1.0
 */
@Slf4j
public class HttpClientUtil {

    private HttpClientUtil() {
        throw new IllegalStateException("util class");
    }

    public static String doGet(String url, Map<String, String> param) {
        String resultString = "";
        // 创建Httpclient对象
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            CloseableHttpResponse response;

            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (Map.Entry<String, String> entry : param.entrySet()) {
                    builder.addParameter(entry.getKey(), entry.getValue());
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
            response.close();
        } catch (IOException | URISyntaxException e) {
            log.error("doGet error", e);
        }
        return resultString;
    }

    public static String doGet(String url) {
        return doGet(url, null);
    }

    @SneakyThrows
    public static String doPost(String url, Map<String, String> param) {
        String resultString = "";
        CloseableHttpResponse response = null;
        // 创建Httpclient对象
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (Map.Entry<String, String> entry : param.entrySet()) {
                    paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");

        } catch (IOException e) {
            log.error("doPost error", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return resultString;
    }

    public static String doPost(String url) {
        return doPost(url, null);
    }

    @SneakyThrows
    public static String doPostJson(String url, String json) {
        String resultString = "";
        CloseableHttpResponse response = null;
        // 创建Httpclient对象
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (IOException e) {
            log.error("doPostJson error", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return resultString;
    }
}
