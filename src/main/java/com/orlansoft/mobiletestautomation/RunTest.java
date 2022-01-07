package com.orlansoft.mobiletestautomation;

import org.testng.TestNG;

public class RunTest {
    public static void main(String[] args) {
        TestNG testSuite = new TestNG();
        testSuite.setTestClasses(new Class[] { QuickTest.class });
        //testSuite.addListener(new PDFReportListener());
        testSuite.setDefaultSuiteName("Mobile Test");
        //testSuite.setDefaultTestName("Mobile Numbering Test");
        //testSuite.setOutputDirectory("/home/orlansoft-web6/Desktop/Automation Testing/Result");
        testSuite.run();
    }
}
