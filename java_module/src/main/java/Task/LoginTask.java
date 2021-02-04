package Task;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginTask implements Runnable{
    private final String username = "220194882";
    private final String password = "zxd960211";
    private Headers basicHeader;
    private OkHttpClient client;
    private final String url1 = "https://newids.seu.edu.cn/authserver/login?service=https://newids.seu.edu.cn/authserver/login2.jsp";
    public LoginTask() {
        basicHeader = new Headers.Builder()
                .add("sec-ch-ua", "\"Google Chrome\";v=\"87\", \" Not;A Brand\";v=\"99\", \"Chromium\";v=\"87\"")
                .add("sec-ch-ua-mobile", "?0")
                .add("Upgrade-Insecure-Requests", "1")
                .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36")
                .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .add("Sec-Fetch-Site", "none")
                .add("Sec-Fetch-Mode", "navigate")
                .add("Sec-Fetch-User", "?1")
                .add("Referer",url1)
                .add("Sec-Fetch-Dest", "document").build();
        client = new OkHttpClient().newBuilder().connectTimeout(50L, TimeUnit.SECONDS).readTimeout(60L, TimeUnit.SECONDS).followRedirects(false).build();
    }
    @Override
    public void run() {
        try {
            //第一次请求 进入登录页
            Headers header1 = new Headers.Builder().addAll(basicHeader).build();
            Request request1 = new Request.Builder().get().headers(header1)
                    .url(url1).build();
            Response response1 = client.newCall(request1).execute();

            //第二次请求 提交表单 重定向
            StringBuilder cookie=new StringBuilder();
            cookie.append("amp.portal.login=1; ");
            cookie.append("org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE=zh_CN; ");
            for (String header : response1.headers("Set-Cookie")) {
                if(header.matches("route.*")){
                    cookie.append(header+"; ");
                }else {
                    cookie.append(header.split(" ")[0]+" ");
                }
            }
            Document doc = Jsoup.parse(response1.body().string());
            String url2 = doc.getElementById("casLoginForm").attr("action");
            FormBody body2 = new FormBody.Builder()
                    .add("_eventId",doc.select("input[name=_eventId]").val())
                    .add("username",username)
                    .add("password",encryPassWord(password, doc.getElementById("pwdDefaultEncryptSalt").val()))
                    .add("lt",doc.select("input[name=lt]").val())
                    .add("execution",doc.select("input[name=execution]").val())
                    .add("rmShown",doc.select("input[name=rmShown]").val())
                    .add("dllt",doc.select("input[name=dllt]").val())
                    .build();
            response1.close();
            Request request2 = new Request.Builder().url("https://newids.seu.edu.cn" + url2).headers(basicHeader).header("Cookie",cookie.toString()).post(body2).build();
            Response response2 = client.newCall(request2).execute();

            //第三次请求 重定向
            String url3 = response2.header("Location");
            for (String header : response2.headers("Set-Cookie")) {
                cookie.append(header.split(";")[0]+"; ");
            }
            response2.close();
            Request request3 = new Request.Builder().url(url3).get().headers(basicHeader).header("Cookie",cookie.toString()).build();
            Response response3 = client.newCall(request3).execute();
            //第四次请求 重定向
            String url4 = response3.header("Location");
            System.out.println(url4);
            response3.close();
            Request request4 = new Request.Builder().url(url4).get().headers(basicHeader).header("Cookie",cookie.toString()).build();
            Response response4 = client.newCall(request4).execute();
            //第五次请求 重定向
            String url5=URLDecoder.decode(response4.header("Location"));
            System.out.println(url5);
            response4.close();
            Request request5 = new Request.Builder().url(url5).get().headers(basicHeader).header("Cookie",cookie.toString()).build();
            Response response5 = client.newCall(request5).execute();
            for (String name : response5.headers().names()) {
                System.out.println("5---"+name+"---"+response3.header(name));
            }
            System.out.println(response5.body().string());
            response5.close();
            //第六次请求 重定向
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class InnerCookieJar implements CookieJar {
        /*Map<HttpUrl, List<Cookie>> cookiesMap= new HashMap<>();*/
        List<Cookie> cookies = new LinkedList<>();
        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            /*List<Cookie> cookies=null;
            if(cookiesMap.containsKey(url)){
                cookies= cookiesMap.get(url);
            }else {
                cookies = new LinkedList<>();
            }*/
            /*System.out.println(cookies.toString());*/
            return cookies;
        }

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.name() + "---" + cookie.value());
                System.out.println(cookie.toString());
            }
            this.cookies=cookies;
        }
    }

    public static String encryPassWord(String password,String encrySalt) throws ScriptException, NoSuchMethodException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        Invocable inv = (Invocable) engine;
        /*engine.eval("load(\"C:/Users/zhuxingda/Desktop/新建文件夹 (2)/localserver_test/src/main/resources/encry.js\")");*/
        engine.eval("load(\"java_module/src/main/resources/script/encry.js\")");
        Object calculator = engine.get("calculator");
        return  (String) inv.invokeMethod(calculator, "encry", password, encrySalt);
    }
}