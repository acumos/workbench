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

package org.acumos.workbench.notebookservice.config;

import java.util.Date;
import java.util.List;

import org.acumos.workbench.common.security.SecurityConstants;
import org.acumos.workbench.notebookservice.NotebookServiceApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.google.common.collect.Lists;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	
	@Bean
    public Docket swaggerSpringfoxDocket() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2)
								.apiInfo(apiInfo())
								.forCodeGeneration(true)
					            .genericModelSubstitutes(ResponseEntity.class)
					            .ignoredParameterTypes(Pageable.class)
					            .ignoredParameterTypes(java.sql.Date.class)
					            .directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
					            .directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
					            .directModelSubstitute(java.time.LocalDateTime.class, Date.class)
					            .securityContexts(Lists.newArrayList(securityContext()))
					            .securitySchemes(Lists.newArrayList(apiKey()))
					            .useDefaultResponseMessages(false);
		
		docket = docket.select()
				.apis(RequestHandlerSelectors.basePackage("org.acumos.workbench.notebookservice.controller"))
	            .paths(PathSelectors.any())
	            .build();
		return docket;
	}
	
	private ApiInfo apiInfo() {
		final String version = NotebookServiceApplication.class.getPackage().getImplementationVersion();
		ApiInfo apiInfo = new ApiInfoBuilder()
				.title("ML Workbench Notebook Service REST API") 
				.description("Methods supporting all Notebok Service APIs") 
				.version(version == null ? "version not available" : version) 
				.termsOfServiceUrl("Terms of service") 
				.contact(new Contact("Acumos Design Studio Dev Team",
						"https://acumos.readthedocs.io/", //TODO : Once docs are available need to update accordingly.
						"noreply@acumos.org")) 
				.license("Apache 2.0") 
				.licenseUrl("http://www.apache.org/licenses/LICENSE-2.0").build();
		return apiInfo;
	}
	
	private ApiKey apiKey() {
        return new ApiKey("JWT", SecurityConstants.AUTHORIZATION_HEADER_KEY, "header");
    }
	
	private SecurityContext securityContext() {
        return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .forPaths(PathSelectors.any())
            .build();
    }
	
	List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
            = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(
            new SecurityReference("JWT", authorizationScopes));
    }
}
