package net.aicoder.cd.entity;

import java.io.Serializable;

public class CdRequirement implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String projectCode;
	
	private String storyId;
	private String productId;
	private String productName;
	private String storyBranch;
	
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	public String getStoryId() {
		return storyId;
	}
	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getStoryBranch() {
		return storyBranch;
	}
	public void setStoryBranch(String storyBranch) {
		this.storyBranch = storyBranch;
	}
}
