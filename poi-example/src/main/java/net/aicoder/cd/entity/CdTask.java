package net.aicoder.cd.entity;

import java.io.Serializable;

public class CdTask implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String projectCode;
	
	private String taskId;
	private String taskParentId;
	private String taskPtype;
	private String taskName;
	private String taskType;
	
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskParentId() {
		return taskParentId;
	}
	public void setTaskParentId(String taskParentId) {
		this.taskParentId = taskParentId;
	}
	public String getTaskPtype() {
		return taskPtype;
	}
	public void setTaskPtype(String taskPtype) {
		this.taskPtype = taskPtype;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

}
