package test;

import org.testng.TestNG;

public class Main {

	public static void main(String[] args) {
		TestNG testSuite = new TestNG();
		testSuite.setTestClasses(new Class[] { AddBugTask.class });
		testSuite.run();
	}

}
