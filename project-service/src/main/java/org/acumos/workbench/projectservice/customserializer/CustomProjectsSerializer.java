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

package org.acumos.workbench.projectservice.customserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.acumos.workbench.projectservice.vo.Identifier;
import org.acumos.workbench.projectservice.vo.Project;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class CustomProjectsSerializer extends StdSerializer<List<Project>>{

	private static final long serialVersionUID = -4320442707927808746L;

	public CustomProjectsSerializer(Class<List<Project>> t) {
		super(t);
	}

	public CustomProjectsSerializer() {
		this(null);
	}
	
	@Override
	public void serialize(List<Project> projects, JsonGenerator generator,
			SerializerProvider provider) throws IOException, JsonProcessingException {
		List<Identifier> ids = new ArrayList<Identifier>();
        for (Project project : projects) {
            ids.add(project.getProjectId());
        }
        generator.writeObject(ids);
		
	}

}
