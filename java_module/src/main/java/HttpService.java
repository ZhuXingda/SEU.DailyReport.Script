import okhttp3.*;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuxingda
 * @date 2021/2/5 14:34
 */
public class HttpService {
    private final String username = "220194882";
    private final String password = "zxd960211";
    private Headers basicHeader;
    private OkHttpClient client;
    private HashMap<String,String> cookiesMap = new HashMap<>();

    public HttpService(String url) {
        basicHeader = new Headers.Builder()
                .add("sec-ch-ua", "\"Google Chrome\";v=\"87\", \" Not;A Brand\";v=\"99\", \"Chromium\";v=\"87\"")
                .add("sec-ch-ua-mobile", "?0")
                .add("Upgrade-Insecure-Requests", "1")
                .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36")
                .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .add("Sec-Fetch-Site", "none")
                .add("Sec-Fetch-Mode", "navigate")
                .add("Sec-Fetch-User", "?1")
                .add("Referer",url)
                .add("Sec-Fetch-Dest", "document").build();
        client = new OkHttpClient().newBuilder().connectTimeout(50L, TimeUnit.SECONDS).readTimeout(60L, TimeUnit.SECONDS).followRedirects(false).build();
        cookiesMap.put("amp","amp.portal.login=1; ");
        cookiesMap.put("spring","org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE=zh_CN; ");
    }

    public Response getRequest(String url,String...cookieNames){
        Request request = new Request.Builder().get().headers(basicHeader).url(url).header("Cookie", parseCookies(cookieNames)).build();
        return executeRequest(request);
    }

    public Response postRequest(String url,FormBody formBody,String...cookieNames){
        Request request = new Request.Builder().post(formBody).headers(basicHeader).url(url).header("Cookie", parseCookies(cookieNames)).build();
        return executeRequest(request);
    }

    private String parseCookies(String...cookieNames){
        String cookie;
        if (cookieNames.length>0){
            StringBuilder cookieBuilder = new StringBuilder("");
            for (String name : cookieNames) {
                cookieBuilder.append(cookiesMap.get(name));
            }
            cookie = cookieBuilder.toString();
        }else {
            cookie = getAllCookies();
        }
        return cookie;
    }

    private Response executeRequest(Request request){
        try {
            Response response = client.newCall(request).execute();
            for (String newCookie : response.headers("Set-Cookie")) {
                addCookie(newCookie);
            }
            return response;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String getAllCookies(){
        if(cookiesMap.size()>0){
            StringBuilder result = new StringBuilder();
            for (String name : cookiesMap.keySet()) {
                result.append(cookiesMap.get(name));
            }
            return result.toString();
        }
        return "";
    }

    private void addCookie(String cookie_raw){
        cookiesMap.put(cookie_raw.split(";")[0].split("=")[0],cookie_raw.split(";")[0] + "; ");
    }
}
