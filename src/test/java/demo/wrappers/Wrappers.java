package demo.wrappers;

import java.util.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */
    ChromeDriver driver;
    public List<Map<String, Object>> teamDataList = new ArrayList<>();
    public List<Map<String, Object>> movieDetailsList = new ArrayList<>();

    public Wrappers(ChromeDriver driver) {
        this.driver = driver;
    }

    public void openUrl(String url) {
        driver.get(url);
    }

    public void clickElement(By locator) {
        driver.findElement(locator).click();
    }

    public void scrollIntoElement(By locator) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement scrollElement = driver.findElement(locator);
        js.executeScript("arguments[0].scrollIntoView(true);", scrollElement);
    }

    public void collectTeamData(By locator) throws InterruptedException {
        // Iterate till 4th page of the webpage
        for (int i = 1; i <= 4; i++) {
            scrollIntoElement(By.xpath("//ul[@class='pagination']/li/a"));
            // Clicking the required page number
            List<WebElement> pageNums = driver.findElements(By.xpath("//ul[@class='pagination']/li/a"));
            for (WebElement pageNum : pageNums) {
                try {
                    int num = Integer.parseInt(pageNum.getText());
                    if (num == i) {
                        System.out.println("Navigating to page: " + num);
                        pageNum.click();
                        Thread.sleep(1000);
                        break;
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            scrollIntoElement(locator);
            Thread.sleep(1000);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
            List<WebElement> headers = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
            for (WebElement header : headers) {
                if (header.getText().contains("Win %")) {
                    break;
                }
            }

            // store the data in the arraylist
            List<WebElement> winPercents = driver.findElements(By.xpath("//td[6]"));
            for (WebElement row : winPercents) {
                if (Float.parseFloat(row.getText()) < 0.40) {
                    String winPercent = row.getText();
                    String teamName = row.findElement(By.xpath(".//preceding-sibling::td[@class='name']")).getText();
                    System.out.println(teamName);
                    String year = row.findElement(By.xpath(".//preceding-sibling::td[@class='year']")).getText();
                    System.out.println(year);

                    Map <String, Object> teamData = new HashMap<>();
                    teamData.put("Epoch Time of Scrape", Instant.now().getEpochSecond());
                    teamData.put("Team Name", teamName);
                    teamData.put("Year", year);
                    teamData.put("Win %", winPercent);

                    teamDataList.add(teamData);
                }
            }
        }
    }

    public void writeDataToJson(List<Map<String, Object>> dataList, String fileName) {
        // store the data in the json file
        ObjectMapper mapper = new ObjectMapper();
        String outputFolderPath = "Output/";
        File file = new File(outputFolderPath + fileName);
        try {
            file.getParentFile().mkdirs();
            mapper.writeValue(file, dataList);
            System.out.println("Data written to " + file.getAbsolutePath() + " successfully!!");
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    //store the movie details in array list
    public void collectMovieDetails(By locator) throws InterruptedException {
        List <WebElement> listOfYears = driver.findElements(By.xpath("//div[@class='col-md-12 text-center']/a"));
        for (WebElement year : listOfYears) {
            System.out.println(year.getText());
            year.click();
            Thread.sleep(5000);

            List <WebElement> movieList = driver.findElements(By.xpath("//tr[@class='film']"));
            System.out.println(movieList.size());
            for (int i = 0; i < 5; i++) {
                WebElement movies = movieList.get(i);
                String movieTitle = movies.findElement(By.xpath(".//td[@class='film-title']")).getText();
                System.out.println(movieTitle);

                String nomination = movies.findElement(By.xpath(".//td[@class='film-nominations']")).getText();
                System.out.println(nomination);

                String awards = movies.findElement(By.xpath(".//td[@class='film-awards']")).getText();
                System.out.println(awards);
                boolean isWinner;
                try { 
                WebElement bestPicture = movies.findElement(By.xpath(".//td[@class='film-best-picture']/i"));
                isWinner = bestPicture.isDisplayed();
                System.out.println("The isWinner flag is: " + isWinner);
                } catch (Exception e) {
                    continue;
                }

                Map <String, Object> movieDetails = new HashMap<>();
                movieDetails.put("Epoch Time of Scrape", Instant.now().getEpochSecond());
                movieDetails.put("Year", year);
                movieDetails.put("Movie Title", movieTitle);
                movieDetails.put("Nominations", nomination);
                movieDetails.put("Awards", awards);
                movieDetails.put("Is Winner", isWinner);

                movieDetailsList.add(movieDetails);
            }
        }
    }

}
