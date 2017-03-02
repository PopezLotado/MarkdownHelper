package org.pzone.md.upload;

import static org.junit.Assert.*;

import org.junit.Test;

public class MarkdownHelperTest {


	@Test
	public void testFromClipboard() {
		MarkdownHelper md=new MarkdownHelper();
		md.fromClipboard();
	}

	@Test
	public void testFromFile() {
		MarkdownHelper md=new MarkdownHelper();
		md.fromFile();
	}

}
