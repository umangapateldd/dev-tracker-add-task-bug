package test;

import java.io.IOException;

import org.testng.TestNG;

public class Main {
	public static void main(String[] args) throws IOException {
		TestNG testSuite = new TestNG();
		testSuite.setTestClasses(new Class[] { AddBugTask.class });
		testSuite.run();
		
//		JLanguageTool langTool = new JLanguageTool(new BritishEnglish());
//		// comment in to use statistical ngram data:
//		//langTool.activateLanguageModelRules(new File("/data/google-ngram-data"));
//		List<RuleMatch> matches = langTool.check("5. click on save button.");
//		for (RuleMatch match : matches) {
//		  System.out.println("Potential error at characters " +
//		      match.getFromPos() + "-" + match.getToPos() + ": " +
//		      match.getMessage());
//		  System.out.println("Suggested correction(s): " +
//		      match.getSuggestedReplacements());
//		}
		
//		Potential error at characters 16-17: Use <suggestion>an</suggestion> instead of 'a' if the following word starts with a vowel sound, e.g. 'an article', 'an hour'
//		Suggested correction(s): [an]
//		Potential error at characters 31-41: Possible spelling mistake found
//		Suggested correction(s): [Hitch-hiker, Hitch hiker]
//		Potential error at characters 50-56: Did you mean <suggestion>to the</suggestion>?
//		Suggested correction(s): [to the]

	}
}
