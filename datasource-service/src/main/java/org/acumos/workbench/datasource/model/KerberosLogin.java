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

package org.acumos.workbench.datasource.model;

import java.io.Serializable;

import org.acumos.workbench.common.vo.DataSourceMetadata;

public class KerberosLogin implements Serializable{
	
	private static final long serialVersionUID = 2430065064574946086L;
	
	String kerberosLoginUser;
	String kerberosKeyTabContent;
	String kerberosRealms;
	String kerberosKdc;
	String kerbersoAdminServer;	
	String kerberosPasswordServer;
	String kerberosDomainName;
	private DataSourceMetadata metaData; 
	String kerberosKeyTabFileName;
	String kerberosConfigFileName;
	
	/**
	 * @return the kerberosLoginUser
	 */
	public String getKerberosLoginUser() {
		return kerberosLoginUser;
	}
	/**
	 * @param kerberosLoginUser the kerberosLoginUser to set
	 */
	public void setKerberosLoginUser(String kerberosLoginUser) {
		this.kerberosLoginUser = kerberosLoginUser;
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
	 * @return the kerbersoAdminServer
	 */
	public String getKerbersoAdminServer() {
		return kerbersoAdminServer;
	}
	/**
	 * @param kerbersoAdminServer the kerbersoAdminServer to set
	 */
	public void setKerbersoAdminServer(String kerbersoAdminServer) {
		this.kerbersoAdminServer = kerbersoAdminServer;
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
	 * @return the kerberosDomainName
	 */
	public String getKerberosDomainName() {
		return kerberosDomainName;
	}
	/**
	 * @param kerberosDomainName the kerberosDomainName to set
	 */
	public void setKerberosDomainName(String kerberosDomainName) {
		this.kerberosDomainName = kerberosDomainName;
	}
	/**
	 * @return the metaData
	 */
	public DataSourceMetadata getMetaData() {
		return metaData;
	}
	/**
	 * @param metaData the metaData to set
	 */
	public void setMetaData(DataSourceMetadata metaData) {
		this.metaData = metaData;
	}
	/**
	 * @return the kerberosKeyTabFileName
	 */
	public String getKerberosKeyTabFileName() {
		return kerberosKeyTabFileName;
	}
	/**
	 * @param kerberosKeyTabFileName the kerberosKeyTabFileName to set
	 */
	public void setKerberosKeyTabFileName(String kerberosKeyTabFileName) {
		this.kerberosKeyTabFileName = kerberosKeyTabFileName;
	}
	/**
	 * @return the kerberosConfigFileName
	 */
	public String getKerberosConfigFileName() {
		return kerberosConfigFileName;
	}
	/**
	 * @param kerberosConfigFileName the kerberosConfigFileName to set
	 */
	public void setKerberosConfigFileName(String kerberosConfigFileName) {
		this.kerberosConfigFileName = kerberosConfigFileName;
	}
	
	

}
