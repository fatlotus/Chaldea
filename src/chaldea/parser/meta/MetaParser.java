package chaldea.parser.meta;

import chaldea.runtime.anno.*;
import chaldea.runtime.ChaldeaValue;

@ChaldeaType("Parser")
public class MetaParser {
	@Method("test")
	public static void test() {
		System.err.println("Running Parser test...");
	}
}