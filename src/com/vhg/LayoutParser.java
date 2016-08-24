package com.vhg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LayoutParser {

	// ===========================================================
    // CodeNamer
    // ===========================================================
	
	public interface CodeNamer {
		public String nameClass(String layoutName);
		public String nameViewField(String viewId);
	}
	
	// ===========================================================
    // Fields
    // ===========================================================
	
	private File mFile;
	private Map<String, List<LayoutView>> mViews;
	private CodeNamer mCodeNamer;
	
	// ===========================================================
    // Constructor
    // ===========================================================
	
	public LayoutParser(File file) {
		mFile = file;
		mViews = new HashMap<String, List<LayoutView>>();
		mCodeNamer = new DefaultCodeNamer();
	}
	
	// ===========================================================
    // Public methods
    // ===========================================================
	
	public void setCodeNamer(CodeNamer namer) {
		mCodeNamer = namer;
	}
	
	public String getName() {
		String retVal = mFile.getName();
		retVal = retVal.substring(0, retVal.length() - 4);
		return mCodeNamer.nameClass(retVal);
	}
	
	public LayoutView parse() {
		LayoutView retVal = null;
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(mFile);
			doc.getDocumentElement().normalize();
			
			retVal = new LayoutView();
			NodeList childs = doc.getChildNodes();
			for (int i = 0; i < childs.getLength(); i++) {
				parseItem(childs.item(i), retVal);
			}
			fixDuplicateViews();
		} catch (Exception e) { retVal = null; e.printStackTrace(); }
		
		return retVal;
	}
	
	// ===========================================================
    // Private methods
    // ===========================================================
	
	private LayoutView parseItem(Node node, LayoutView parentView) {
		if (null == node || null == node.getAttributes()) {
			return null;
		}
		
		String idValue = "";
		Node id = node.getAttributes().getNamedItem("android:id");
		if (null != id) {
			idValue = id.getNodeValue().split("/")[1];
		}
		
		if (node.getNodeName().equals("include")) {
			Node layout = node.getAttributes().getNamedItem("layout");
			String layoutName = layout.getNodeValue().split("/")[1];
			File includeLayoutFile = new File(mFile.getParent() + File.separator + layoutName + ".xml");
			LayoutParser includeParser = new LayoutParser(includeLayoutFile);
			LayoutView v = includeParser.parse();
			if (null != v) {
				v.setId(idValue);
				v.setType("include." + mCodeNamer.nameClass(layoutName));
				v.setName(mCodeNamer.nameViewField(idValue));
				v.setParent(parentView);
				parentView.addChild(v);
				onViewAdded(v);
			}
			return v;
		}

		LayoutView retVal = new LayoutView();
		retVal.setType(node.getNodeName());
		retVal.setId(idValue);
		retVal.setParent(parentView);
		retVal.setName(mCodeNamer.nameViewField(idValue));
		parentView.addChild(retVal);
		onViewAdded(retVal);
		
		try {
			NodeList childs = node.getChildNodes();
			for (int i = 0; i < childs.getLength(); i++) {
				parseItem(childs.item(i), retVal);
			}
		} catch (Exception e) { retVal = null; e.printStackTrace(); }
		
		return retVal;
	}
	
	private void onViewAdded(LayoutView v) {
		if (v.getName().isEmpty()) {
			return;
		}
		List<LayoutView> views = mViews.get(v.getName());
		if (null == views) {
			views = new ArrayList<LayoutView>();
			mViews.put(v.getName(), views);
		}
		views.add(v);
	}
	
	private void fixDuplicateViews() {
		List<LayoutView> toFix = new ArrayList<LayoutView>();
		for (String name : mViews.keySet()) {
			List<LayoutView> views = mViews.get(name);
			if (views.size() > 1) {
				toFix.addAll(views);
				
			}
		}
		
		for (LayoutView v : toFix) {
			mViews.remove(v.getName());
		}
		
		if (!toFix.isEmpty()) {
			int n = 1;
			for (LayoutView v : toFix) {
				LayoutView p = getParentWithId(v.getParent());
				if (null == p) {
					v.setName(v.getName() + (n++));
				} else {
					v.setName(p.getName() + v.getName().substring(1));
					v.setId(p.getId() + ";" + v.getId());
				}
				onViewAdded(v);
			}
			
			fixDuplicateViews();
		}
	}
	
	private LayoutView getParentWithId(LayoutView v) {
		if (v.getId().isEmpty()) {
			return getParentWithId(v.getParent());
		}
		return v;
	}
	
	// ===========================================================
    // DefaultCodeNamer
    // ===========================================================
	
	private class DefaultCodeNamer implements CodeNamer {

		// ===========================================================
	    // CodeNamer methods
	    // ===========================================================
		
		@Override
		public String nameClass(String layoutName) {
			String[] tokens = layoutName.split("_");
			StringBuilder sb = new StringBuilder();
			sb.append("VH");
			for (String token : tokens) {
				sb.append(token.substring(0, 1).toUpperCase());
				sb.append(token.substring(1).toLowerCase());
			}
			return sb.toString();
		}

		@Override
		public String nameViewField(String viewId) {
			if (null == viewId || viewId.isEmpty()) {
				return "";
			}
			return "m" + getFielClassdName(viewId);
		}
		
		// ===========================================================
	    // Private methods
	    // ===========================================================
		
		private String getFielClassdName(String viewId) {
			StringBuilder sb = new StringBuilder();
			String[] tokens = viewId.split("_");
			for (String t : tokens) {
				sb.append(t.substring(0, 1).toUpperCase() + t.substring(1).toLowerCase());
			}
			return sb.toString();
		}
	}
}
