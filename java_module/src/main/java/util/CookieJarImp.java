package util;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author zhuxingda
 * @date 2021/2/3 10:18
 */
public class CookieJarImp implements CookieJar{
    List<Cookie> cookies;
    @Override
    public List<Cookie> loadForRequest(HttpUrl arg0) {
        if (cookies != null) {
            return cookies;
        }
        return new ArrayList<Cookie>();
    }

    @Override
    public void saveFromResponse(HttpUrl arg0, List<Cookie> cookies) {
        System.out.println(cookies);
        this.cookies = cookies;
    }
}
