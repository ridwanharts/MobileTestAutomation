package com.orlansoft.mobiletestautomation;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

public class QuickTest {

    public AndroidDriver<MobileElement> driver;
    public WebDriverWait wait;
    public DesiredCapabilities caps = new DesiredCapabilities();
    String fileSetup = "SETUP.txt";
    String projectTest = "";
    String[] listFileTest = {};

    @BeforeMethod
    public void setup() {
        loadFileSetup();
    }

    @Test
    public void basicTest() {

        caps.setCapability("autoWebview",true);
        String[] contextNames = driver.getContextHandles().toArray(new String[0]);
        for (String contextName : contextNames) {
            System.out.println(contextName); //prints out something like NATIVE_APP \n WEBVIEW_1
        }
        driver.context(contextNames[1]);


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
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileSetup);
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        try {
            String text;
            while ((text = reader.readLine()) != null) {
                if (text.contains("Set(")){
                    setCapabilities(text);
                }
                if (text.startsWith(">endSetup")){
                    driver = new AndroidDriver<MobileElement>(new URL("http://0.0.0.0:4723/wd/hub"), caps);
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
        if (text.startsWith("Set(\"fileTest\"")) {
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
    }

    private void loadFileTest(String f) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream;
        if (projectTest.equals("")){
            inputStream = classLoader.getResourceAsStream(f);
        }else{
            inputStream = classLoader.getResourceAsStream(projectTest + "/" +f);
        }
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        try {
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
                    runClickButton(text);
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
                }else if (text.startsWith("Wait")){
                    wait.wait(10);
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

    private void runWaitUntilVisible(String line){
        String id = line.substring(line.indexOf("(")+1, line.indexOf(")"));
        id = id.replace("\"", "");
        By view = By.id(id);
        wait.until(ExpectedConditions.visibilityOfElementLocated(view));
    }

    private void runSetTextField(String line){
        line = line.substring(line.indexOf("(")+1, line.indexOf(")"));
        String[] sSplit = line.split(",");
        sSplit[0] = sSplit[0].replace("\"", "");
        By view = By.id(sSplit[0]);
        sSplit[1] = sSplit[1].replace("\"", "");
        driver.findElement(view).setValue(sSplit[1]);
    }

    private void runClickButton(String line){
        String id = line.substring(line.indexOf("(")+1, line.indexOf(")"));
        id = id.replace("\"", "");
        By view = By.id(id);
        driver.findElement(view).click();
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
