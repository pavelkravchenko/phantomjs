import com.google.common.io.Files;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileOutputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        // start the proxy
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start(0);

        // get the Selenium proxy object
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "phantomjs");
        capabilities.setJavascriptEnabled(true);
        capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
        capabilities.setBrowserName("chrome");
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, "--webdriver-loglevel=DEBUG");
        WebDriver driver = new PhantomJSDriver(capabilities);
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        proxy.newHar("phantomjs.org");
//        driver.get("http://ukr.net");
        driver.get("http://phantomjs.org");
        File scr = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Har har = proxy.getHar();
        System.out.printf(har.toString());
        FileOutputStream fo = new FileOutputStream("test.har");
        har.writeTo(fo);
//        Files.copy(scr, new File("screenshot.jpg"));
        proxy.stop();
        driver.close();
        driver.quit();
    }
}
//////////
/////////
///////////