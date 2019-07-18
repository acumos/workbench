/*-
 * ===============LICENSE_START=======================================================
 * Acumos
  * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.workbench.common.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Version implements Serializable {

	private static final long serialVersionUID = 1752785438676531467L;
	private String comment;
	private String label;
	private String creationTimeStamp;
	private String modifiedTimeStamp;
	private String user;
	
	
	/**
	 * Parameterized Constructor
	 * @param comment
	 * 			Comment on Version
	 * @param label
	 * 			Version label for e.g., 1 or 0.0.1 or V1.1
	 * @param creationTimeStamp
	 * 			Creation TimeStamp
	 * @param modifiedTimeStamp
	 * 			ModifiedTimeStamp
	 * @param user
	 * 			user
	 */
	public Version(String comment, String label, String creationTimeStamp, String modifiedTimeStamp, String user) {
		super();
		this.comment = comment;
		this.label = label;
		this.creationTimeStamp = creationTimeStamp;
		this.modifiedTimeStamp = modifiedTimeStamp;
		this.user = user;
	}
	
	public Version() { 
		super();
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	

	/**
	 * @return the creationTimeStamp
	 */
	public String getCreationTimeStamp() {
		return creationTimeStamp;
	}

	/**
	 * @param creationTimeStamp the creationTimeStamp to set
	 */
	public void setCreationTimeStamp(String creationTimeStamp) {
		this.creationTimeStamp = creationTimeStamp;
	}

	/**
	 * @return the modifiedTimeStamp
	 */
	public String getModifiedTimeStamp() {
		return modifiedTimeStamp;
	}

	/**
	 * @param modifiedTimeStamp the modifiedTimeStamp to set
	 */
	public void setModifiedTimeStamp(String modifiedTimeStamp) {
		this.modifiedTimeStamp = modifiedTimeStamp;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Version other = (Version) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	
}
