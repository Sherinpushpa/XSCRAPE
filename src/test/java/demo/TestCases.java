package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.Assert;
import java.io.File;

import java.util.logging.Level;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases {
    ChromeDriver driver;

    Wrappers wrapper;

   
    @BeforeTest
    public void startBrowser()
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();

        wrapper = new Wrappers(driver);
    }

    @Test
    public void testCase01() throws InterruptedException {
        System.out.println("Start Test Case: testCase01");
        wrapper.openUrl("https://www.scrapethissite.com/pages/");
        Thread.sleep(1000);
        wrapper.clickElement(By.xpath("//a[contains(text(),'Hockey Teams:')]"));
        Thread.sleep(1000);
        wrapper.collectTeamData(By.xpath("//th"));
        wrapper.writeDataToJson(wrapper.teamDataList, "hockey-team-data.json");
        System.out.println("End Test Case: testCase01");
    }

    @Test
    public void testCase02() throws InterruptedException {
        System.out.println("Start Test Case: testCase02");
        wrapper.openUrl("https://www.scrapethissite.com/pages/");
        Thread.sleep(1000);
        wrapper.clickElement(By.xpath("//a[contains(text(),'Oscar Winning Films')]"));
        Thread.sleep(1000);
        wrapper.collectMovieDetails(By.xpath("//th"));
        wrapper.writeDataToJson(wrapper.movieDetailsList,"oscar-winner-data.json");
        File file = new File ("Output/" + "oscar-winner-data.json");

        Assert.assertTrue(file.exists(),"File should exists in the output folder");
        Assert.assertTrue(file.length() > 0, "File should not be empty");
        System.out.println("End Test Case: testCase02");
    }

    @AfterTest
    public void endTest()
    {
        driver.close();
        driver.quit();

    }
}