import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
