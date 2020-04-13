/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.workbench.datasource.vo;

import java.io.Serializable;

public class KerberosConfigInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7012771619597485647L;
	
	private String domainName;
	private String kerberosKdc;
	private String kerberosKeyTabContent;
	private String kerberosPasswordServer;
	private String kerberosRealms;
	private String kerberosAdminServer;
	private String configFileContents;
	/**
	 * @return the domainName
	 */
	public String getDomainName() {
		return domainName;
	}
	/**
	 * @param domainName the domainName to set
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	/**
	 * @return the kerberosKdc
	 */
	public String getKerberosKdc() {
		return kerberosKdc;
	}
	/**
	 * @param kerberosKdc the kerberosKdc to set
	 */
	public void setKerberosKdc(String kerberosKdc) {
		this.kerberosKdc = kerberosKdc;
	}
	/**
	 * @return the kerberosKeyTabContent
	 */
	public String getKerberosKeyTabContent() {
		return kerberosKeyTabContent;
	}
	/**
	 * @param kerberosKeyTabContent the kerberosKeyTabContent to set
	 */
	public void setKerberosKeyTabContent(String kerberosKeyTabContent) {
		this.kerberosKeyTabContent = kerberosKeyTabContent;
	}
	/**
	 * @return the kerberosPasswordServer
	 */
	public String getKerberosPasswordServer() {
		return kerberosPasswordServer;
	}
	/**
	 * @param kerberosPasswordServer the kerberosPasswordServer to set
	 */
	public void setKerberosPasswordServer(String kerberosPasswordServer) {
		this.kerberosPasswordServer = kerberosPasswordServer;
	}
	/**
	 * @return the kerberosRealms
	 */
	public String getKerberosRealms() {
		return kerberosRealms;
	}
	/**
	 * @param kerberosRealms the kerberosRealms to set
	 */
	public void setKerberosRealms(String kerberosRealms) {
		this.kerberosRealms = kerberosRealms;
	}
	/**
	 * @return the kerberosAdminServer
	 */
	public String getKerberosAdminServer() {
		return kerberosAdminServer;
	}
	/**
	 * @param kerberosAdminServer the kerberosAdminServer to set
	 */
	public void setKerberosAdminServer(String kerberosAdminServer) {
		this.kerberosAdminServer = kerberosAdminServer;
	}
	/**
	 * @return the configFileContents
	 */
	public String getConfigFileContents() {
		return configFileContents;
	}
	/**
	 * @param configFileContents the configFileContents to set
	 */
	public void setConfigFileContents(String configFileContents) {
		this.configFileContents = configFileContents;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("domainName : ").append(domainName).append(", kerberosKdc : ").append(kerberosKdc)
				.append(", kerberosAdminServer : ").append(kerberosAdminServer).append(", kerberosPasswordServer : ")
				.append(kerberosPasswordServer).append(", kerberosRealms : ").append(kerberosRealms)
				.append(", kerberosKeyTabContent : ").append(kerberosKeyTabContent.length());

		return sb.toString();
	}
	
	

}
