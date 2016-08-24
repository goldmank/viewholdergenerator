package com.vhg;

import java.io.File;
import java.io.FileFilter;

public class Main {

	// ===========================================================
    // Main
    // ===========================================================
	
	public static void main(String[] args) {
		if (null == args || args.length != 3) {
			System.out.println("usage: layoutbinder.jar <package-name> <xml-file-name> <output-path>");
		} else {
			File xmlFile = new File(args[1]);
			if (xmlFile.exists()) {
				if (xmlFile.isDirectory()) {
					File[] files = xmlFile.listFiles(new FileFilter() {

						@Override
						public boolean accept(File pathname) {
							return pathname.getAbsolutePath().endsWith(".xml");
						}
						
					});
					for (File f : files) {
						handleFile(f, args[0], args[2]);
					}
				} else {
					handleFile(xmlFile, args[0], args[2]);
				}
				
			} else {
				System.out.println(xmlFile.getAbsolutePath() + " not found");
			}
		}
	}
	
	// ===========================================================
    // Private methods
    // ===========================================================
	
	private static void handleFile(File file, String pkgName, String outputPath) {
		LayoutParser parser = new LayoutParser(file);
		LayoutView view = parser.parse();
		
		BinderBuilder bb = new BinderBuilder(view, parser.getName(), pkgName, outputPath);
		bb.build();		
	}
}
