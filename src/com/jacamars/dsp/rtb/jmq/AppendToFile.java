package com.jacamars.dsp.rtb.jmq;


//JavaFileAppendFileWriterExample.java
//Created by <a href="http://alvinalexander.com" title="http://alvinalexander.com">http://alvinalexander.com</a>

import java.io.*;

public class AppendToFile {

	public static void item(String fileName, StringBuilder sb)
			throws Exception {

		BufferedWriter bw = null;
		bw = new BufferedWriter(new FileWriter(fileName, true));
		bw.append(sb);
		bw.flush();
		bw.close();
	}

} 
