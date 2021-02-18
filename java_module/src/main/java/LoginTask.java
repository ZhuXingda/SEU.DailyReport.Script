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

    private final String indexUrl = "http://ehall.seu.edu.cn/new/index.html";
    private final String authUrl = "https://newids.seu.edu.cn/authserver/login?service=https://newids.seu.edu.cn/authserver/login2.jsp";
    private final String dailyReportUrl = "http://ehall.seu.edu.cn/appShow?appId=5821102911870447";
    private HttpService httpService = new HttpService(indexUrl);

    @Override
    public void run() {
        try {
            //第1次请求 进入主页
            Response response1 = httpService.getRequest(indexUrl);
            response1.close();
            //第2次请求 进入登录页
            Response response2 = httpService.getRequest(authUrl);
            //第3次请求 提交表单 重定向
            Document doc = Jsoup.parse(response2.body().string());
            response2.close();
            String url3 = "https://newids.seu.edu.cn"+doc.getElementById("casLoginForm").attr("action");
            FormBody formBody = new FormBody.Builder()
                    .add("_eventId",doc.select("input[name=_eventId]").val())
                    .add("username",username)
                    .add("password",encryPassWord(password, doc.getElementById("pwdDefaultEncryptSalt").val()))
                    .add("lt",doc.select("input[name=lt]").val())
                    .add("execution",doc.select("input[name=execution]").val())
                    .add("rmShown",doc.select("input[name=rmShown]").val())
                    .add("dllt",doc.select("input[name=dllt]").val())
                    .build();
            Response response3 = httpService.postRequest(url3, formBody);
            //第4次请求 重定向
            String url4 = response3.header("Location");
            response3.close();
            Response response4 = httpService.getRequest(url4);
            //第5次请求 重定向
            String url5 = response4.header("Location");
            Response response5 = httpService.getRequest(url5);
            response4.close();
            //第6次请求 重定向
            String utl6 = response5.header("Location");
            response5.close();
            Response response6 = httpService.getRequest(utl6);
            //第7次请求 重定向
            String url7 = response6.header("Location");
            response6.close();
            Response response7 = httpService.getRequest(url7);
            //第8次请求 进主页
            String url8 = response7.header("Location");
            response7.close();
            Response response8 = httpService.getRequest(url8);
            response8.close();
            //第9次请求 进健康上报重定向
            Response response9 = httpService.getRequest(dailyReportUrl);
            //第10次请求 进健康上报主页
            String url10 = response9.header("Location");
            response9.close();
            Response response10 = httpService.getRequest(url10);
            response10.close();
            //第11次请求 新建上报
            Response response11 = httpService.postRequest("http://ehall.seu.edu.cn/qljfwapp2/sys/lwReportEpidemicSeu/modules/dailyReport/cxwdjbxxcjsl.do", new FormBody.Builder().add("pageNumber","1").build());
            response11.close();
            //获取wid
            Response response12 = httpService.postRequest("http://ehall.seu.edu.cn/qljfwapp2/sys/lwReportEpidemicSeu/modules/dailyReport/getMyTodayReportWid.do", new FormBody.Builder().add("pageNumber","1").build());
            System.out.println(response12.body().string());
            //获取server time
            Response response13 = httpService.postRequest("http://ehall.seu.edu.cn/qljfwapp2/sys/lwReportEpidemicSeu/modules/dailyReport/getMyTodayReportWid.do", new FormBody.Builder().add("pageNumber","1").build());
            System.out.println(response13.body().string());
            //

        }catch (Exception e){
            e.printStackTrace();
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