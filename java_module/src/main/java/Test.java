import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuxingda
 * @date 2021/2/2 20:52
 */
public class Test {
    public static void main(String[] args) throws IOException, ScriptException, NoSuchMethodException {
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(50L, TimeUnit.SECONDS).readTimeout(60L, TimeUnit.SECONDS).build();
        Request request1 = new Request.Builder()
                .url("https://newids.seu.edu.cn/authserver/login?goto=https://seicwxbz.seu.edu.cn/cas-we-can/cas-login-callback/77c56821-9f04-4575-b271-1e332e809986")
                .get()
                .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"87\", \" Not;A Brand\";v=\"99\", \"Chromium\";v=\"87\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .addHeader("Sec-Fetch-Site", "none")
                .addHeader("Sec-Fetch-Mode", "navigate")
                .addHeader("Sec-Fetch-User", "?1")
                .addHeader("Sec-Fetch-Dest", "document")
                .addHeader("Cookie", "oute=a602807a3e96cbc0514f5c82e0ded0c7; org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE=zh_CN; JSESSIONID=u61h3GdTnVr2jsXDwXayS-IQdXJYulz4b7dzdmufy9gEXC7ydqxA!-359806008")
                .build();
        Response response = client.newCall(request1).execute();
        String responseBody = response.body().string();
        Document doc = Jsoup.parse(responseBody);

        client = new OkHttpClient().newBuilder().followRedirects(false).connectTimeout(50L, TimeUnit.SECONDS).readTimeout(60L, TimeUnit.SECONDS).build();
        String url = doc.getElementById("casLoginForm").attr("action");

        FormBody body = new FormBody.Builder()
                .add("_eventId",doc.select("input[name=_eventId]").val())
                .add("username","220194882")
                .add("password",encryPassWord("zxd960211", doc.getElementById("pwdDefaultEncryptSalt").val()))
                .add("lt",doc.select("input[name=lt]").val())
                .add("execution",doc.select("input[name=execution]").val())
                .add("rmShown",doc.select("input[name=rmShown]").val())
                .add("dllt",doc.select("input[name=dllt]").val())
                .build();
        Request request2 = new Request.Builder()
                .url("https://newids.seu.edu.cn"+url)
                .post(body)
                .header("sec-ch-ua", "\"Google Chrome\";v=\"87\", \" Not;A Brand\";v=\"99\", \"Chromium\";v=\"87\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-User", "?1")
                .header("Sec-Fetch-Dest", "document")
                .build();
        Response response2 = client.newCall(request2).execute();
        String location = Arrays.stream(response2.header("Location").split("=")).reduce((last, present)->last=present).get();
        /*for (String name : response2.headers().names()) {
            System.out.println(name+"-----"+response2.header(name));
        }
        System.out.println(IOUtils.toString(response2.body().byteStream()));
        System.out.println(location);*/
    }

    public static String encryPassWord(String password,String encrySalt) throws ScriptException, NoSuchMethodException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        Invocable inv = (Invocable) engine;
        engine.eval("load(\"C:/Users/zhuxingda/Desktop/新建文件夹 (2)/localserver_test/src/main/resources/encry.js\")");
        Object calculator = engine.get("calculator");
        return  (String) inv.invokeMethod(calculator, "encry", password, encrySalt);
    }
}
