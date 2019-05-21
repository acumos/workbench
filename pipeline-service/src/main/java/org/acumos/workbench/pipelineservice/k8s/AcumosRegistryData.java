package org.acumos.workbench.pipelineservice.k8s;

public class AcumosRegistryData {
	private String registryName;
	private String registryId;
	private String registryUri;
	private boolean registryConfigured;
	
	public String getRegistryName() {
		return registryName;
	}

	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}

	
	public String getRegistryId() {
		return registryId;
	}

	public void setRegistryId(String registryId) {
		this.registryId = registryId;
	}

	public String getRegistryUri() {
		return registryUri;
	}

	public void setRegistryUri(String registryUri) {
		this.registryUri = registryUri;
	}

	public boolean isRegistryConfigured() {
		return registryConfigured;
	}

	public void setRegistryConfigured(boolean registryConfigured) {
		this.registryConfigured = registryConfigured;
	}
	public AcumosRegistryData() {
		// TODO Auto-generated constructor stub
	}//end-consterutor

}
