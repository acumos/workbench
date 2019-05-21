package org.acumos.workbench.pipelineservice.k8s;

public class PipelineData {
	private boolean pipelineExists;
	private String pipelineName;
	private String pipelineId;

	public boolean isPipelineExists() {
		return pipelineExists;
	}

	public void setPipelineExists(boolean pipelineExists) {
		this.pipelineExists = pipelineExists;
	}

	public String getPipelineName() {
		return pipelineName;
	}

	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

	public PipelineData() {
		// TODO Auto-generated constructor stub
	}

}
