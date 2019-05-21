package org.acumos.workbench.pipelineservice.k8s;

public class BucketData {
	private String bucketName;
	private String bucketId;
	private boolean bucketExists;

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getBucketId() {
		return bucketId;
	}

	public void setBucketId(String bucketId) {
		this.bucketId = bucketId;
	}

	public boolean isBucketExists() {
		return bucketExists;
	}

	public void setBucketExists(boolean bucketExists) {
		this.bucketExists = bucketExists;
	}

	public BucketData() {
		// TODO Auto-generated constructor stub
	}

}
