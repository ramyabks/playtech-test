import static org.junit.jupiter.api.Assertions.*;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

class PlayTech {

	private static WebDriver driver;
	private static Properties locators;
	private static FileWriter fileWriter;

	@BeforeAll
	static void setupClass() throws IOException {
		WebDriverManager.chromedriver().setup();
		locators = new Properties();
		FileInputStream fis = new FileInputStream("src/test/resources/locators.properties");
		locators.load(fis);

		fileWriter = new FileWriter("src/test/resources/test-output.txt");

		driver = new ChromeDriver();
		driver.manage().window().setSize(new Dimension(1200, 800));
	}

	@BeforeEach
	void setupTest() {
		driver.get("https://www.playtechpeople.com/");
		assertEquals(driver.getTitle(), "Home - Playtech People");
		clickIfExists(By.id(locators.getProperty("cookie_button")));
	}

	@Test
	@Order(1)
	void playTechLocationsTest() throws InterruptedException, IOException {
		driver.findElement(By.linkText(locators.getProperty("locations_link"))).click();
		List<WebElement> locations = driver.findElements(By.cssSelector(locators.getProperty("header_locations")));
		fileWriter.write("\n\n\n\nLocations \n");
		for (WebElement location : locations) {
			System.out.println(location.getText());
			fileWriter.write("Location: " + location.getText() + "\n");
		}
		assertEquals(locations.size(), 16);
		fileWriter.write("\n\n\n\n\n");
	}

	@Test
	@Order(2)
	void playTechCasino() throws InterruptedException, IOException {
		driver.findElement(By.linkText(locators.getProperty("life_at_playtech_link"))).click();
		driver.findElement(By.linkText(locators.getProperty("who_we_are_link"))).click();

		WebElement casino = driver.findElement(By.xpath(locators.getProperty("casino_text_xpath")));
		System.out.println(casino.getText());
		assertEquals(casino.getText(),
				"The world’s largest and most diverse online casino content developers with 8 content studios.");

		fileWriter.write("Casino description \n" + casino.getText());
	}

	@Test
	@Order(3)
	void playTechJobs() throws InterruptedException, IOException {
		driver.findElement(By.partialLinkText(locators.getProperty("all_jobs_link"))).click();
		driver.findElement(By.xpath(locators.getProperty("select_location_xpath"))).click();
		driver.findElement(By.xpath(locators.getProperty("estonia_option_xpath"))).click();
		driver.findElement(By.cssSelector(locators.getProperty("search_button_css"))).click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(
				ExpectedConditions.presenceOfElementLocated(By.cssSelector(locators.getProperty("estonia_jobs_css"))));
		fileWriter.write("Estonia Jobs \n");
		List<WebElement> jobs = driver.findElements(By.cssSelector(locators.getProperty("estonia_jobs_css")));
		for (WebElement job : jobs) {
			System.out.println(job.getAttribute("href"));
			fileWriter.write("Job Link: " + job.getAttribute("href") + "\n");
		}
		fileWriter.write("\n\n\n\n\n");
	}

	@AfterAll
	static void teardown() throws IOException {
		if (driver != null) {
			driver.quit();
		}
		if (fileWriter != null) {
			fileWriter.close();
		}
	}

	private void clickIfExists(By by) {
		try {
			WebElement element = driver.findElement(by);
			if (element.isDisplayed() && element.isEnabled()) {
				element.click();
			}
		} catch (NoSuchElementException e) {
			System.out.println("Element not found: " + by);
		}
	}

}
