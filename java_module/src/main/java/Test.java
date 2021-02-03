import Task.LoginTask;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuxingda
 * @date 2021/2/2 20:52
 */
public class Test {
    public static void main(String[] args) throws Exception{
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        threadPool.execute(new LoginTask());
    }
}
