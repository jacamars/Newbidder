package com.jacamars.dsp.rtb.blocks;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


import com.amazonaws.services.s3.model.S3Object;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;


/**
 * Implements the BloomFilter for UTF-8 strings. Builds a Guava bloom filter from file or S3 object.
 * @author Ben M. Faul
 *
 */
public class Bloom extends LookingGlass {
	BloomFilter<CharSequence> bloomFilter;
	int size;
	double fpp = 0.003; // desired false positive probability
	
	/**
	 * Constructor for the File/S3 object to Bloom filter.
	 * @param name String. The name of the bloom filter.
	 * @param file String, the file name.
	 * @throws Exception on File Errors.
	 */
	public Bloom(String name, String file) throws Exception {
		File f = new File(file);
		long size = f.length();
		bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), size,fpp);
		symbols.put(name, bloomFilter);
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		makeFilter(br,size);
	}
	

	/**
	 * Constructor for the File/S3 object to Bloom filter.
	 * @param name String. The name of the bloom filter.
	 * @param file String, the file name.
	 * @throws Exception on File Errors.
	 */
	public Bloom(String name, String fileName, long size) throws Exception {
		File f = new File(fileName);
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), size,fpp);
		symbols.put(name, bloomFilter);
		
		makeFilter(br,size);
		
		symbols.put(name, bloomFilter);
	}
	
	/**
	 * Constructor for the S3 version of the Bloom filter.
	 * @param name String. The name of the object.
	 * @param object S3Object. The object that contains the file.
	 * @throws Exception on S3 errors.
	 */
	public Bloom(String name, S3Object object, long size) throws Exception {
		bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), size,fpp);
		symbols.put(name, bloomFilter);

		InputStream objectData = object.getObjectContent();
		BufferedReader br=new BufferedReader(new InputStreamReader(objectData));
		makeFilter(br,size);
		
	}
	
	/**
	 * Reads a file or S3 object line by line and loads the filter.
	 * @param br BufferedReader. The line-by-line reader.
	 * @throws Exception on I/O errors.
	 */
	void makeFilter(BufferedReader br, long size) throws Exception {
		int i;
		long sz;
		String line;
		String  [] parts;
		
		while ((line = br.readLine()) != null) {
			parts = eatquotedStrings(line);
			for (i = 0; i < parts.length; i++) {
				parts[i] = parts[i].replaceAll("\"", "");
			}
			bloomFilter.put(parts[0].trim());
			this.size++;
		}
		br.close();
	}
	
	/**
	 * Returns the Bloom filter for your use.
	 * @return BloomFilter. The Guava bloom filter of the contents of this file.
	 */
	public BloomFilter getBloom() {
		return bloomFilter;
	}
	
	/**
	 * Check if this key is possibly in the bloom filter
	 * @param key String. The key to test for.
	 * @return boolean. Returns false if it is not in the filter. Returns true if it possibly is in there.
	 */
	public boolean isMember(String key) {
		return bloomFilter.mightContain(key);
	}
	
	/**
	 * Returns the number of elements.
	 * @return int. The number of elements in the filter.
	 */
	@Override
	public long getMembers() {
		return size;
	}

	public static void main(String [] args) throws Exception {
		Bloom b = new Bloom("junk","/home/ben/Downloads/expression-appnexus.csv");

		String content = new String(Files.readAllBytes(Paths.get("/home/ben/Downloads/expression-appnexus.csv")), StandardCharsets.UTF_8);

		String [] parts = content.split("\n");
		for (String part : parts) {
			part = part.replaceAll("\"","");
			boolean x = b.isMember(part);
			if (!x) {
				System.out.println("False negative: " + part);
			}
		}

		boolean bb = b.isMember("7669645438914899809");
		System.out.println("BB: " + bb);
	}
}

