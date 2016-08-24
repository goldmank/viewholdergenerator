package com.vhg;

import java.util.ArrayList;
import java.util.List;

public class LayoutView {
	
	// ===========================================================
    // Fields
    // ===========================================================
	
	private String mId;
	private String mType;
	private String mName;
	private List<LayoutView> mChilds;
	private LayoutView mParent;
	
	// ===========================================================
    // Constructor
    // ===========================================================
	
	public LayoutView() {
		mChilds = new ArrayList<LayoutView>();
	}
	
	// ===========================================================
    // Public methods
    // ===========================================================
	
	public void setId(String id) {
		mId = id;
	}
	
	public void setType(String type) {
		mType = type;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public void addChild(LayoutView child) {
		mChilds.add(child);
	}
	
	public void setParent(LayoutView p) {
		mParent = p;
	}
	
	public String getId() {
		return mId;
	}
	
	public String getType() {
		return mType;
	}
	
	public String getName() {
		return mName;
	}
	
	public List<LayoutView> getChilds() {
		return mChilds;
	}
	
	public LayoutView getParent() {
		return mParent;
	}
}
