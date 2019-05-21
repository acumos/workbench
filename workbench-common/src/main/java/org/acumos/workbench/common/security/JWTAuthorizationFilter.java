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

package org.acumos.workbench.common.security;

import static org.acumos.workbench.common.security.SecurityConstants.*;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private String secretKey;
	private UserService userService;
	
	public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}
	
	public JWTAuthorizationFilter(AuthenticationManager authenticationManager, String secretKey, ICommonDataServiceRestClient cdsClient) {
		super(authenticationManager);
		logger.debug("JWTAuthorizationFilter() begin");
		this.secretKey = secretKey;
		this.userService = new UserService(cdsClient);
		logger.debug("JWTAuthorizationFilter() end");
	}
	
	/**
	 * Method is called internally and should not be accessible directly using class instance.
	 * 
	 */
	@Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
		logger.debug("doFilterInternal() begin");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String authToken = null;
		authToken = httpRequest.getHeader(AUTHORIZATION_HEADER_KEY);
		logger.debug("AUTHORIZATION_HEADER_KEY : " + authToken);
		if (authToken == null) {
			authToken = httpRequest.getHeader(JWT_TOKEN_HEADER_KEY);
			logger.debug("JWT_TOKEN_HEADER_KEY : " + authToken);
		}
		if (authToken == null) {
			authToken = request.getParameter(JWT_TOKEN_HEADER_KEY);
			logger.debug("JWT_TOKEN_HEADER_KEY : " + authToken);
		}
		if (authToken != null) {
			authToken = authToken.replace(TOKEN_PASS_KEY, "");
			logger.debug("TOKEN_PASS_KEY : " + authToken);
			JWTTokenVO jwtTokenVO = JwtTokenUtil.getUserToken(authToken, secretKey);
			if (jwtTokenVO != null
					&& !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
				//validate token 
				if (validateToken(jwtTokenVO, secretKey)) {
					MLPUser mlpUser = userService.findUserByUsername(jwtTokenVO.getUserName());
					//TODO : Need to implement role base authority 
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							new AuthenticatedUser(mlpUser), authToken, new ArrayList<>());
					authentication.setDetails(new WebAuthenticationDetailsSource()
							.buildDetails(httpRequest));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}
        chain.doFilter(request, response);
        logger.debug("doFilterInternal() End");
    }

	private boolean validateToken(JWTTokenVO jwtTokenVO, String secretKey) {
		logger.debug("validateToken() Begin");
		Boolean isVallidToken = false;
		if (jwtTokenVO != null) {
			// check token expired or not
			if (!JwtTokenUtil.isTokenExpired(jwtTokenVO.getExpirationDate())) {
				String userName = jwtTokenVO.getUserName();
				MLPUser mlpUser = userService.findUserByUsername(userName);
				if (mlpUser != null) {
					String authTokenFromDB = mlpUser.getAuthToken();
					if (authTokenFromDB != null) {
						JWTTokenVO jwtTokenVOFromDB = JwtTokenUtil.getUserToken(authTokenFromDB, secretKey);
						MLPUser mlpUserFromDB = userService.findUserByUsername(jwtTokenVOFromDB.getUserName());
						if (mlpUserFromDB != null) {
							if (mlpUserFromDB.getUserId().equals(mlpUser.getUserId())) {
								isVallidToken = true;
							}
						}
					}
				}
			}
		}
		logger.debug("validateToken() End");
		return isVallidToken;
	}
}
