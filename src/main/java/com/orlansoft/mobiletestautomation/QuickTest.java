package com.orlansoft.mobiletestautomation;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuickTest {

    public AndroidDriver<MobileElement> driver;
    public WebDriverWait wait;
    public DesiredCapabilities caps = new DesiredCapabilities();
    String fileSetup = "SETUP.txt";
    String projectTest = "";
    String[] listFileTest = {};
    String type = "";

    @BeforeMethod
    public void setup() {
        loadFileSetup();
    }

    @Test
    public void basicTest() throws InterruptedException {

        if (type.equals("hybrid")){
            caps.setCapability("autoWebview",true);
        }

        int count = 1;
        for(String f : listFileTest){
            System.out.println("Starting Test [" + f + "].");
            //load file test
            loadFileTest(f);
            System.out.println("Test [" + f + "] has been completed.");
            if (listFileTest.length == count){
                break;
            }
            //load setup for the next test
            loadFileSetup();
            count++;
        }
    }

    @AfterMethod
    public void teardown() {
        driver.quit();
    }

    private void loadFileSetup(){
        try {
            FileReader fr = new FileReader("src/main/java/" + fileSetup);
            //FileReader fr = new FileReader(fileSetup);
            BufferedReader reader = new BufferedReader(fr);
            String text;
            while ((text = reader.readLine()) != null) {
                if (text.contains("Set(")){
                    setCapabilities(text);
                }
                if (text.startsWith(">endSetup")){
                    driver = new AndroidDriver<MobileElement>(new URL("http://0.0.0.0:4723/wd/hub"), caps);
                    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                    wait = new WebDriverWait(driver, 10);
                }
            }
        } catch (IOException e) {
            printError(e.getMessage());
        }
    }

    private void setCapabilities(String text) {
        String sSubstring = text.substring(text.indexOf("(") + 1, text.indexOf(")"));
        String[] sSplit = sSubstring.split(",");
        sSplit[1] = sSplit[1].replace("\"", "");
        if (text.startsWith("Set(\"type\"")){
            type = sSplit[1];
        }else if (text.startsWith("Set(\"fileTest\"")) {
            listFileTest = sSplit[1].split("\\|");
        }else if (text.startsWith("Set(\"projectTest\"")){
            projectTest = sSplit[1];
        }else if (text.startsWith("Set(\"deviceName\"")){
            caps.setCapability("deviceName", sSplit[1]);
        }else if (text.startsWith("Set(\"udid\"")){
            caps.setCapability("udid", sSplit[1]); //DeviceId from "adb devices" command
        }else if (text.startsWith("Set(\"platformName\"")){
            caps.setCapability("platformName", sSplit[1]); //DeviceId from "adb devices" command
        }else if (text.startsWith("Set(\"platformVersion\"")){
            caps.setCapability("platformVersion", sSplit[1]); //DeviceId from "adb devices" command
        }else if (text.startsWith("Set(\"skipUnlock\"")){
            caps.setCapability("skipUnlock", sSplit[1]); //DeviceId from "adb devices" command
        }else if (text.startsWith("Set(\"appPackage\"")){
            caps.setCapability("appPackage", sSplit[1]); //DeviceId from "adb devices" command
        }else if (text.startsWith("Set(\"appActivity\"")){
            caps.setCapability("appActivity", sSplit[1]); //DeviceId from "adb devices" command
        }else if (text.startsWith("Set(\"noReset\"")){
            caps.setCapability("noReset", sSplit[1]); //DeviceId from "adb devices" command
        }else if (text.startsWith("Set(\"autoGrantPermissions\"")){
            caps.setCapability("autoGrantPermissions", sSplit[1]); //permission auto approve
        }
        caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.ANDROID_UIAUTOMATOR2);
    }

    private void loadFileTest(String f) {
        String sFile;
        if (projectTest.equals("")){
            sFile = "src/main/java/" + f;
            //sFile = f;
        }else{
            sFile = "src/main/java/" + projectTest + "/" +f;
            //sFile = projectTest + "/" +f;
        }
        try {
            FileReader fr = new FileReader(sFile);
            BufferedReader reader = new BufferedReader(fr);
            String text;
            int count = 1;
            while ((text = reader.readLine()) != null) {
                if (text.startsWith("WaitUntilVisible")){
                    runWaitUntilVisible(text);
                    System.out.println("Line["+ count +"] " + text + " passed");
                }else if (text.startsWith("SetTextField")){
                    runSetTextField(text);
                    System.out.println("Line["+ count +"] " + text + " passed");
                }else if (text.startsWith("ClickButton")) {
                    if(text.contains("ClickButtonByTextView")){
                        runClickButtonByTextView(text);
                    }else{
                        runClickButton(text);
                    }
                    System.out.println("Line["+ count +"] " + text + " passed");
                }else if(text.startsWith("OpenNavDrawer") || text.startsWith("CloseNavDrawer")) {
                    runOpenCloseDrawer(text);
                    System.out.println("Line["+ count +"] " + text + " passed");
                }else if (text.startsWith("ClickNavDrawerMenu")) {
                    runClickDrawerMenu(text);
                    System.out.println("Line["+ count +"] " + text + " passed");
                }else if(text.startsWith("GoHome") || text.startsWith("GoBack") || text.startsWith("GoRecent")) {
                    runMainButton(text);
                    System.out.println("Line["+ count +"] " + text + " passed");
                }else if (text.startsWith("Enter")) {
                    runEnter();
                    System.out.println("Line[" + count + "] " + text + " passed");
                }else if (text.startsWith("WaitElementWithText")) {
                    runWaitElementWithText(text);
                    System.out.println("Line[" + count + "] " + text + " passed");
                }else if(text.startsWith("Sleep")) {
                    runSleep(text);
                    System.out.println("Line[" + count + "] " + text + " passed");
                }else{
                    if (text.startsWith(">endTest")){
                        System.out.println("##########");
                    }
                }
                count++;
            }
        } catch (IOException | InterruptedException e) {
            printError(e.getMessage());
        }
    }

    private void runSleep(String text) throws InterruptedException {
        String millis = text.substring(text.indexOf("(")+1, text.indexOf(")"));
        millis = millis.replace("\"", "");
        Thread.sleep(Long.parseLong(millis));
    }

    private void runWaitUntilVisible(String line){
        String id = line.substring(line.indexOf("(")+1, line.indexOf(")"));
        id = id.replace("\"", "");
        By view = By.id(id);
        wait.until(ExpectedConditions.visibilityOfElementLocated(view));
    }

    private void runWaitElementWithText(String line){
        try{
            line = line.substring(line.indexOf("(")+1, line.indexOf(")"));
            String[] sSplit = line.split(",");
            sSplit[0] = sSplit[0].replace("\"", "");
            WebElement element= driver.findElementByXPath(sSplit[0]);
            if (!element.getText().isEmpty()){
                sSplit[1] = sSplit[1].replace("\"", "");
                if (sSplit.length == 3){
                    WebDriverWait w;
                    System.out.println("Start Download");
                    w = new WebDriverWait(driver, 100);
                    w.until(ExpectedConditions.textToBePresentInElement(element, sSplit[1]));
                    System.out.println("Done Download");
                }else{
                    wait.until(ExpectedConditions.textToBePresentInElement(element, sSplit[1]));
                }
            }else{
                System.out.println(element.getTagName());
            }
            System.out.println(element.getText());
        }catch (Exception e){
            //System.out.println(e.getMessage());
        }
    }

    private void runSetTextField(String line){
        line = line.substring(line.indexOf("(")+1, line.indexOf(")"));
        String[] sSplit = line.split(",");
        sSplit[0] = sSplit[0].replace("\"", "");
        By view = By.id(sSplit[0]);
        sSplit[1] = sSplit[1].replace("\"", "");
        if (type.equals("hybrid")){
            driver.findElementsByXPath(sSplit[0])
                    .get(0).setValue(sSplit[1]);
        }else{
            driver.findElement(view).setValue(sSplit[1]);
        }
    }

    private void runClickButton(String line){
        try{
            String id = line.substring(line.indexOf("(")+1, line.indexOf(")"));
            id = id.replace("\"", "");
            By view = By.id(id);

            if (type.equals("hybrid")){
                driver.findElementsByXPath(id).get(0).click();
            }else{
                driver.findElement(view).click();
            }
        }catch (Exception e){
            //System.out.println(e.getMessage());
        }
    }

    private void runClickButtonByTextView(String text){
        try{
            String id = text.substring(text.indexOf("(")+1, text.indexOf(")"));
            id = id.replace("\"", "");
            driver.findElement(By.xpath("//*[@text='" + id + "']")).click();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void runOpenCloseDrawer(String text){
        if (text.contains("OpenNavDrawer")){
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.ImageButton[@content-desc=\"Open\"]"))
            ).click();
        }else{
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//android.widget.ImageButton[@content-desc=\"Close\"]"))
            ).click();
        }
    }

    private void runClickDrawerMenu(String text){
        text = text.substring(text.indexOf("(")+1, text.indexOf(")"));
        String[] sSplit = text.split(",");
        sSplit[0] = sSplit[0].replace("\"", "");
        sSplit[1] = sSplit[1].replace("\"", "");
        List<MobileElement> elements = driver
                .findElementById(sSplit[0])
                .findElements(By.className("android.view.ViewGroup"));
        for(MobileElement e : elements){
            String textDrawer = e.findElements(By.className("android.widget.TextView")).get(0).getText();
            if(textDrawer.equals(sSplit[1])){
                e.click();
                break;
            }
        }
    }

    private void runMainButton(String text){
        if (text.contains("GoBack")){
            driver.pressKey(new KeyEvent(AndroidKey.BACK));
        }else if(text.contains("GoHome")){
            driver.pressKey(new KeyEvent(AndroidKey.HOME));
        }else if(text.contains("GoRecent")){
            driver.pressKey(new KeyEvent(AndroidKey.APP_SWITCH));
        }
    }

    private void runEnter(){
        driver.pressKey(new KeyEvent(AndroidKey.ENTER));
    }

    private void printError(String text){
        System.out.println("Error translate test => " + text);
    }

}
