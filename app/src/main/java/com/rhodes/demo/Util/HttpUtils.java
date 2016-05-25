package com.rhodes.demo.Util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * TODO 网络连接工具类
 *
 * @version: V1.0
 */
public class HttpUtils {

    private static String TAG = "HttpUtils";

    private HttpUtils() {
    }

    /**
     * TODO 使用get方式连接网络获取返回值，
     *
     * @param path    连接的完整url
     * @return String json数据 包含是否成功等 订单号，渠道跳转url 登录是否过期
     */
    public static String doGetRequest(String path) {
        Logger.log(TAG, "method doGetRequest() called.", "url=" + path);
        int    retry  = 0;
        String result = "";
        while (retry < 2) {
            try {
                if (!StringUtils.isEmpty(path)) {

                    URL url = new URL(path);

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setReadTimeout(10000);
                    httpURLConnection.setDoOutput(true);
                    ByteArrayOutputStream by           = new ByteArrayOutputStream();
                    InputStream           inputStream  = null;
                    int                   responseCode = httpURLConnection.getResponseCode();
                    Logger.log(TAG, "doGetRequest()", "responseCode=" + responseCode);
                    if (responseCode == 200) {
                        inputStream = httpURLConnection.getInputStream();
                        for (int b = 0; (b = inputStream.read()) != -1; )
                            by.write(b);
                        by.flush();
                        byte bb[] = by.toByteArray();
                        result = new String(bb, 0, bb.length);

                        Logger.log(TAG, "doGetRequest", "url=" + path, "response=" + result);
                        return result;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                retry++;
                continue;
            }
            break;
        }
        return result;
    }

    public static String doPost(String strURL, String params) {
        try {
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream(), "UTF-8"); // utf-8编码
            out.append(params);
            out.flush();
            out.close();
            // 读取响应
            int         length = (int) connection.getContentLength();// 获取长度
            InputStream is     = connection.getInputStream();
            if (length != -1) {
                byte[] data    = new byte[length];
                byte[] temp    = new byte[512];
                int    readLen = 0;
                int    destPos = 0;
                while ((readLen = is.read(temp)) > 0) {
                    System.arraycopy(temp, 0, data, destPos, readLen);
                    destPos += readLen;
                }
                String result = new String(data, "UTF-8"); // utf-8编码
                System.out.println(result);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ""; // 自定义错误信息
    }

    /**
     * 统计
     *
     * @param strURL
     * @param message
     * @return
     */
    public static String doPostStat(String strURL, String message, String imei) {
        int retry = 0;
        while (retry < 2) {
            try {
                URL               url        = new URL(strURL);// 创建连接
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestMethod("POST"); // 设置请求方式
                connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
                connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
                String version = "1";
                String topic   = "papa_gamelogin";
                connection.setRequestProperty("Version", version); // 设置版本
                connection.setRequestProperty("Topic", topic); // 主题
                String dataJson = "[{\"key\":\"" + imei + "\",\"message\":" + message + "}]";
                connection.setRequestProperty("Authorization", MD5Utils.stringToMD5("9c708c7fce87abaa544b221898769baa" + topic + version + "9c708c7fce87abaa544b221898769baa" + dataJson)); // key

                connection.connect();
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
                out.append(dataJson);
                out.flush();
                out.close();
                // 读取响应
                int         length = (int) connection.getContentLength();// 获取长度
                InputStream is     = connection.getInputStream();
                if (length != -1) {
                    byte[] data    = new byte[length];
                    byte[] temp    = new byte[512];
                    int    readLen = 0;
                    int    destPos = 0;
                    while ((readLen = is.read(temp)) > 0) {
                        System.arraycopy(temp, 0, data, destPos, readLen);
                        destPos += readLen;
                    }
                    String result = new String(data, "UTF-8"); // utf-8编码
                    System.out.println(result);
                    return result;
                }
            } catch (Exception e) {
                e.printStackTrace();
                retry++;
                continue;
            }
            break;
        }

        return ""; // 自定义错误信息
    }

//    /**
//     * TODO post请求获取resouceid。兑换比例游戏币名称等。jsON数据
//     *
//     * @param path    基本url
//     * @param isDebug 是否是调试模式
//     * @param args    传入的参数
//     * @return JSON返回的参数
//     */
//    public static String doPostRequestLogin(String path, boolean isDebug, Map<String, String> args) {
//        String params = encryptText(args);
//        args.clear();
//        try {
//            args.put("params", params);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        Logger.log(TAG, "doPostRequest", "url=" + path);
//        String result = "";
//        int retry = 0;
//        while (retry<2){
//            try {
//                Logger.log(TAG, "doPostRequest", "retry=" + retry);
//                if (!StringUtils.isEmpty(path)) {
//                    Set<Map.Entry<String, String>>      entries  = null;
//                    Iterator<Map.Entry<String, String>> iterator = null;
//                    if (StringUtils.isEmpty(path))
//                        return null;
//                    if (args == null || args.size() == 0)
//                        return null;
//                    entries = args.entrySet();
//                    iterator = entries.iterator();
//                    Map.Entry<String, String> entry;
//                    List<BasicNameValuePair>  pairs = new ArrayList<BasicNameValuePair>();
//                    while (iterator.hasNext()) {
//                        entry = iterator.next();
//                        BasicNameValuePair pair1 = new BasicNameValuePair(entry.getKey(), entry.getValue());
//                        pairs.add(pair1);
//                    }
//
//                    HttpEntity entity = null;
//                    // 创建客户端对象
//                    HttpClient client = new DefaultHttpClient();
//                    // 设置连接属性
//                    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
//                    client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
//                    // 创建请求对象
//                    HttpUriRequest request = null;
//                    request = new HttpPost(path);
//
//                    if (pairs != null && !pairs.isEmpty()) {
//                        UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(
//                                pairs,"utf-8");
//                        ((HttpPost) request).setEntity(reqEntity);
//                    }// 执行请求获得响应对象
//                    HttpResponse response = client.execute(request);
//                    // 判断响应码获取 实体对象
//                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                        entity = response.getEntity();
//                    }
//                    if (entity != null) {
//                        // 本地页数加1
//                        // 获得响应实体
//                        InputStream is = entity.getContent();
//                        BufferedReader reader = new BufferedReader(
//                                new InputStreamReader(is, "UTF-8"));
//                        StringBuilder builder = new StringBuilder();
//                        String        line    = null;
//                        while ((line = reader.readLine()) != null) {
//                            // 读取响应数据
//                            builder.append(line);
//                        }
//                        result = builder.toString().trim();
//
//                    }
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                RPCClient.sendPapaError(PPayCenter.getmContext(),"path="+path+";"+StringUtils.getErrorInfo(e));
//                retry++;
//                result = "-1";
//                continue;
//            }
//            break;
//        }
//
//        Logger.log(TAG, "doPostRequest", "url=" + path, "response=" + result);
//
//        return result;
//    }

    /**
     * TODO post请求获取resouceid。兑换比例游戏币名称等。jsON数据
     *
     * @param path    基本url
     * @param isDebug 是否是调试模式
     * @param args    传入的参数
     * @return JSON返回的参数
     */
    public static String doPostRequestOld(String path, boolean isDebug, Map<String, String> args) {
       /* String params = encryptText(args);
        args.clear();
        try {
            args.put("params", URLEncoder.encode(params,"utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        Logger.log(TAG, "doPostRequest", "url=" + path);
        String result = "";
        int    retry  = 0;
        while (retry < 2) {
            try {
                Logger.log(TAG, "doPostRequest", "retry=" + retry);
                if (!StringUtils.isEmpty(path)) {
                    Set<Map.Entry<String, String>>      entries  = null;
                    Iterator<Map.Entry<String, String>> iterator = null;
                    if (StringUtils.isEmpty(path))
                        return null;
                    if (args == null || args.size() == 0)
                        return null;
                    entries = args.entrySet();
                    iterator = entries.iterator();
                    Map.Entry<String, String> entry;
                    List<BasicNameValuePair>  pairs = new ArrayList<BasicNameValuePair>();
                    while (iterator.hasNext()) {
                        entry = iterator.next();
                        BasicNameValuePair pair1 = new BasicNameValuePair(entry.getKey(), entry.getValue());
                        pairs.add(pair1);
                    }

                    HttpEntity entity = null;
                    // 创建客户端对象
                    HttpClient client = new DefaultHttpClient();
                    // 设置连接属性
                    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
                    client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
                    // 创建请求对象
                    HttpUriRequest request = null;
                    request = new HttpPost(path);

                    if (pairs != null && !pairs.isEmpty()) {
                        UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(
                                pairs, "utf-8");
                        ((HttpPost) request).setEntity(reqEntity);
                    }// 执行请求获得响应对象
                    HttpResponse response = client.execute(request);
                    // 判断响应码获取 实体对象
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        entity = response.getEntity();
                    }
                    if (entity != null) {
                        // 本地页数加1
                        // 获得响应实体
                        InputStream is = entity.getContent();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(is, "UTF-8"));
                        StringBuilder builder = new StringBuilder();
                        String        line    = null;
                        while ((line = reader.readLine()) != null) {
                            // 读取响应数据
                            builder.append(line);
                        }
                        result = builder.toString().trim();

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                retry++;
                result = "-1";
                continue;
            }
            break;
        }

        Logger.log(TAG, "doPostRequest", "url=" + path, "response=" + result);

        return result;
    }

    /**
     * TODO post请求获取resouceid。兑换比例游戏币名称等。jsON数据
     *
     * @param path    基本url
     * @param isDebug 是否是调试模式
     * @param args    传入的参数
     * @return JSON返回的参数
     */
    public static String doPostRequest(String path, boolean isDebug, Map<String, String> args) {
        Logger.log(TAG, "doPostRequest", "url=" + path);
        String result = "";
        int    retry  = 0;
        while (retry < 2) {
            try {
                Logger.log(TAG, "doPostRequest", "retry=" + retry);
                if (!StringUtils.isEmpty(path)) {
                    Set<Map.Entry<String, String>>      entries  = null;
                    Iterator<Map.Entry<String, String>> iterator = null;
                    if (StringUtils.isEmpty(path))
                        return null;
                    if (args == null || args.size() == 0)
                        return null;
                    entries = args.entrySet();
                    iterator = entries.iterator();
                    Map.Entry<String, String> entry;
                    List<BasicNameValuePair>  pairs = new ArrayList<BasicNameValuePair>();
                    while (iterator.hasNext()) {
                        entry = iterator.next();
                        BasicNameValuePair pair1 = new BasicNameValuePair(entry.getKey(), entry.getValue());
                        pairs.add(pair1);
                    }
                    int        timeOut = 30 * 1000;
                    HttpParams param   = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(param, timeOut);
                    HttpConnectionParams.setSoTimeout(param, 5000);
                    HttpConnectionParams.setTcpNoDelay(param, true);
                    SchemeRegistry registry = new SchemeRegistry();
                    registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                    registry.register(new Scheme("https", TrustAllSSLSocketFactory.getDefault(), 443));
                    ClientConnectionManager manager = new ThreadSafeClientConnManager(param, registry);
                    HttpEntity              entity  = null;
                    // 创建客户端对象
                    HttpClient client = new DefaultHttpClient(manager, param);
                    // 设置连接属性
//                    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
//                    client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
                    // 创建请求对象
                    HttpUriRequest request = null;
                    request = new HttpPost(path);

                    if (pairs != null && !pairs.isEmpty()) {
                        UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(
                                pairs, "utf-8");
                        ((HttpPost) request).setEntity(reqEntity);
                    }// 执行请求获得响应对象
                    HttpResponse response = client.execute(request);
                    // 判断响应码获取 实体对象
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        entity = response.getEntity();
                    }
                    if (entity != null) {
                        // 本地页数加1
                        // 获得响应实体
                        InputStream is = entity.getContent();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(is, "UTF-8"));
                        StringBuilder builder = new StringBuilder();
                        String        line    = null;
                        while ((line = reader.readLine()) != null) {
                            // 读取响应数据
                            builder.append(line);
                        }
                        result = builder.toString().trim();

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                retry++;
                result = "-1";
                continue;
            }
            break;
        }

        Logger.log(TAG, "doPostRequest", "url=" + path, "response=" + result);

        return result;
    }

    /**
     * method can upload file
     *
     * @param path
     * @param isDebug
     * @param args
     * @return
     */
    public static String doPostRequest1(String path, boolean isDebug, Map<String, Object> args) {
        Logger.log(TAG, "doPostRequest1", "url=" + path);
        int    retry  = 0;
        String result = "";
        while (retry < 2) {
            try {
                if (!StringUtils.isEmpty(path)) {
                    Logger.log(TAG, "retry=" + retry);
                    MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                    Set<Map.Entry<String, Object>>      entries  = null;
                    Iterator<Map.Entry<String, Object>> iterator = null;
                    if (StringUtils.isEmpty(path))
                        return null;
                    if (args == null || args.size() == 0)
                        return null;

                    entries = args.entrySet();
                    iterator = entries.iterator();
                    Map.Entry<String, Object> entry;
                    while (iterator.hasNext()) {
                        entry = iterator.next();
                        String key = entry.getKey();
                        Object val = entry.getValue();
                        if (val instanceof String) {
                            reqEntity.addPart(key, new StringBody((String) val, Charset.forName("UTF-8")));
                        } else if (val instanceof File) {
                            reqEntity.addPart(key, new FileBody((File) val));
                        }
                    }

                    HttpEntity entity = null;
                    // 创建客户端对象
                    HttpClient client = new DefaultHttpClient();
                    // 设置连接属性
                    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
                    client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
                    // 创建请求对象
                    HttpPost request = new HttpPost(path);
//                request.setHeader("Content-type", "multipart/form-data");
                    request.setEntity(reqEntity);
                    // 执行请求获得响应对象
                    HttpResponse response = client.execute(request);
                    // 判断响应码获取 实体对象
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        entity = response.getEntity();
                    }
                    if (entity != null) {
                        // 本地页数加1
                        // 获得响应实体
                        InputStream    is      = entity.getContent();
                        BufferedReader reader  = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        StringBuilder  builder = new StringBuilder();
                        String         line    = null;
                        while ((line = reader.readLine()) != null) {
                            // 读取响应数据
                            builder.append(line);
                        }
                        result = builder.toString().trim();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                retry++;
                result = "-1";
                continue;
            }
            break;
        }

        Logger.log(TAG, "doPostRequest1", "url=" + path, "response=" + result);

        return result;
    }


    public static InputStream getStreamFromURL(String imageURL) {
        InputStream in = null;
        try {
            URL               url        = new URL(imageURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            in = connection.getInputStream();

        } catch (Exception e) {
//TODOAuto-generatedcatchblock
            e.printStackTrace();
        }
        return in;

    }

    //region impl
    public final static  String  FORUM_ROOT_URL = "http://anv3.frapi.papa91.com";            //线上

    private static String genForumPath(String action, Map<String, String> args) {
        return genHttpPath(FORUM_ROOT_URL + action, args);
    }

    public static String doGetRequest(String action, Map<String, String> args) {
        String ret = null;
        try {
            String url      = genForumPath(action, args);
            String response = HttpUtils.doGetRequest(url);

            ret = response;
//            Logger.log(TAG, "doGetRequest", "url=" + url, "response=" + response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static String doGetRequest1(String action, Object... args) {
        String ret = null;
        try {
            String url      = genForumPath(action, genArgs(action, args));
            String response = HttpUtils.doGetRequest(url);

            ret = response;
//            Logger.log(TAG, "doGetRequest1", "url=" + url, "response=" + response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static String doPostRequest(String action, Map<String, String> args) {
        String ret = null;

        try {
            String url      = genForumPath(action, null);
            String response = HttpUtils.doPostRequest(url, args);

            ret = response;
//            Logger.log(TAG, "doPostRequest", "url=" + url, "response=" + response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * @param action
     * @param args   support multipart entity
     * @return
     */
    public static String doPostRequest1(String action, Map<String, Object> args) {
        String ret = null;

        try {
            String url      = genForumPath(action, null);
            String response = HttpUtils.doPostRequest1(url, args);

            ret = response;
//            Logger.log(TAG, "doPostRequest1", "url=" + url, "response=" + response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
    //endregion

    //region common
    private static Map<String, String> genArgs(String action, Object... args) {
        if (args == null) return null;
        HashMap<String, String> ret = new HashMap<String, String>();

        if (StringUtils.isEmpty(action) && !action.contains("?")) return ret;
        String p = action.split("\\?")[1];

        String[] params = null;
        if (p.contains("&")) {
            params = p.split("&");
            if (params.length == args.length)
                for (int i = 0; i < params.length; i++) {
                    String s = params[i];

                    String[] kv  = s.split("=");
                    String   key = kv[0] + "";
                    String   val = "";
                    if (kv.length > 1) {
                        val = args[i] + "";
                    }

                    ret.put(key, val);
                }

        } else {
            if (p.contains("=")) {
                String[] kv  = p.split("=");
                String   key = kv[0];
                String   val = "";
                if (kv.length > 1 && args.length > 0) {
                    val = (String) args[0];
                }

                ret.put(key, val);
            }
        }


        return ret;
    }

    private static String genHttpPath(String url, Map<String, String> args) {
        StringBuffer sb = new StringBuffer();
        sb.append(url);
        if (args == null || args.size() == 0) return sb.toString().trim();

        Set<Map.Entry<String, String>>      entries  = null;
        Iterator<Map.Entry<String, String>> iterator = null;

        entries = args.entrySet();
        iterator = entries.iterator();
        java.util.Map.Entry<String, String> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            String key     = entry.getKey();
            String val     = entry.getValue();
            String replace = "{" + key + "}";
            if (!sb.toString().trim().contains(replace)) continue;
            int start = sb.indexOf(replace);
            int end   = start + replace.length();
            sb.replace(start, end, val);
        }

        return sb.toString().trim();
    }
    //endregion
}
