package Task;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import util.CookieJarImp;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoginTask implements Runnable{
    private final String username = "220194882";
    private final String password = "zxd960211";
    private Headers basicHeader;
    private OkHttpClient client;

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
                .add("Sec-Fetch-Dest", "document").build();
        client = new OkHttpClient().newBuilder().connectTimeout(50L, TimeUnit.SECONDS).readTimeout(60L, TimeUnit.SECONDS).followRedirects(false).cookieJar(new InnerCookieJar()).build();
    }

    @Override
    public void run() {
        Headers header1 = new Headers.Builder().addAll(basicHeader).add("Cookie", "oute=a602807a3e96cbc0514f5c82e0ded0c7; org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE=zh_CN; JSESSIONID=u61h3GdTnVr2jsXDwXayS-IQdXJYulz4b7dzdmufy9gEXC7ydqxA!-359806008").build();
        Request request1 = new Request.Builder().get().headers(header1)
                .url("https://newids.seu.edu.cn/authserver/login?goto=https://seicwxbz.seu.edu.cn/cas-we-can/cas-login-callback/1dc319b1-f635-450f-86d5-1733104f796f").build();
        try {
            Document doc = Jsoup.parse(client.newCall(request1).execute().body().string());
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
            Request request2 = new Request.Builder().post(body2).url("https://newids.seu.edu.cn" + url2).headers(basicHeader).build();
            Response response2 = client.newCall(request2).execute();
            String url3 = response2.header("Location");
            Request request3 = new Request.Builder().get().url(url3).headers(basicHeader).build();
            System.out.println(request3.url());
            Response response3 = client.newCall(request3).execute();
            System.out.println(response3.body().string());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class InnerCookieJar implements CookieJar {
        List<Cookie> cookies;
        @Override
        public List<Cookie> loadForRequest(HttpUrl arg0) {
            if (cookies != null) {
                System.out.println("load cookie---"+cookies);
                return cookies;
            }
            return new ArrayList<Cookie>();
        }

        @Override
        public void saveFromResponse(HttpUrl arg0, List<Cookie> cookies) {
            System.out.println("save cookie---"+cookies);
            this.cookies = cookies;
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