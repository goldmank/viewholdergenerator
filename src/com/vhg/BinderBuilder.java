package com.vhg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class BinderBuilder {

	// ===========================================================
    // Fields
    // ===========================================================
	
	private LayoutView mView;
	private String mClassName;
	private String mPkgName;
	private Set<String> mImports;
	private String mOutputPath;

	// ===========================================================
    // Constructor
    // ===========================================================
	
	public BinderBuilder(LayoutView view, String className, String pkgName, String outputPath) {
		mView = view;
		mClassName = className;
		mPkgName = pkgName;
		mOutputPath = outputPath + "/vh";
		new File(mOutputPath).mkdirs();
	}
	
	// ===========================================================
    // Public methods
    // ===========================================================
	
	public void build() {
		if (null == mView) {
			return;
		}
		
		String template = Template.CODE;
		
		mImports = new HashSet<>();
		mImports.add(mPkgName + ".R");
		
		StringBuilder fieldsStringBuilder = new StringBuilder();
		StringBuilder bindStringBuilder = new StringBuilder();
		buildFields(fieldsStringBuilder, bindStringBuilder, mView);	
		
		template = template.replace("$PACKAGE_NAME$", mPkgName + ".vh");
		template = template.replace("$IMPORT$", buildImportString());
		template = template.replace("$CLASS_NAME$", mClassName);
		template = template.replace("$FIELDS$", fieldsStringBuilder.toString());
		template = template.replace("$INIT$", bindStringBuilder.toString());
		
		writeTextFile(mOutputPath + File.separator + mClassName + ".java", template);
	}
	
	// ===========================================================
    // Private methods
    // ===========================================================
	
	private String buildImportString() {
		StringBuilder sb = new StringBuilder();
		Iterator<String> iter = mImports.iterator();
		while (iter.hasNext()) {
			sb.append("import ");
			sb.append(iter.next());
			sb.append(";");
			if (iter.hasNext()) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	private void buildFields(StringBuilder fields, StringBuilder binds, LayoutView v) {
		if (null != v.getId() && !v.getId().isEmpty()) {
			String fieldName = v.getName();
			String type = v.getType();
			if (type.equals("fragment")){
				return;
				//TODO: handle fragments
			}
			
			fields.append("\t");
			binds.append("\t\t");
			if (type.startsWith("include.")) {
				String className = type.substring(8);
				LayoutView view = new LayoutView();
				for (LayoutView child : v.getChilds()) {
					view.addChild(child);
				} 
				BinderBuilder bb = new BinderBuilder(view, className, mPkgName, mOutputPath);
				bb.build();
				
				appendField(fields, fieldName, className);
				appendBind(binds, v.getId(), fieldName, className);
				
				mImports.add(mPkgName + "." + className);
			} else {
				appendField(fields, fieldName, type);
				appendBind(binds, v.getId(), fieldName, type);
				for (LayoutView child : v.getChilds()) {
					buildFields(fields, binds, child);
				}
				if (type.contains(".")) {
					mImports.add(type);
				} else if (type.equals("fragment")){
					//TODO: handle fragments
				} else {
					mImports.add("android.widget." + type);
				}
			}
		} else {
			for (LayoutView child : v.getChilds()) {
				buildFields(fields, binds, child);
			}
		}
	}
	
	private void appendField(StringBuilder sb, String name, String type) {
		sb.append("public ");
		sb.append(type);
		sb.append(" ");
		sb.append(name);
		sb.append(";\n");
	}
	
	private void appendBind(StringBuilder sb, String id, String name, String type) {
		sb.append(name);
		sb.append(" = (");
		sb.append(type);
		sb.append(")v");
		
		String[] ids = id.split(";");
		for (String i : ids) {
			sb.append(".findViewById(R.id.");
			sb.append(i);
			sb.append(")");
		}
		
		sb.append(";\n");
	}
	
	private void writeTextFile(String fileName, String content) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
}
