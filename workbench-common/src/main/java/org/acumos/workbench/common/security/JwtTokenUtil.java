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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class JwtTokenUtil {
	 
	/**
	 * Parse the input token and populate JWTTokenVO. Avoid reparsing token again an again for any required values. 
	 * @param token
	 * 		Input token String value
	 * @param secretKey
	 * 		Secret key required to parse the token
	 * @return JWTTokenVO
	 * 		Populated with values after succefully parsing the token
	 */
	 public static JWTTokenVO getUserToken(String token, String secretKey) { 
		 JWTTokenVO jwtToken = null; 
		 
		 if(null != token) {
			 final Claims claims = getClaimsFromToken(token, secretKey);
			 if(null != claims) {
				 jwtToken = new JWTTokenVO();
				 jwtToken.setUserName(getUsernameFromToken(claims));
				 jwtToken.setExpirationDate(getExpirationDateFromToken(claims));
			 }
		 }
		 
		 return jwtToken;
	 }
	
	/**
	 * Checks if token is expired
	 * @param expiration
	 * 		Token Expiration Date
	 * @return
	 * 		true if and only if the instant of time represented by this Date object is strictly earlier than the instant represented by when; false otherwise.
	 */
	public static Boolean isTokenExpired(Date expiration) {
		if (expiration == null)
			return true;
		return expiration.before(new Date());
	}
 
	private static String getUsernameFromToken(Claims claims) {
		String username;
		try {
			username = claims.getSubject();
		} catch (Exception e) {
			username = null;
		}
		return username;
	}

	private static Claims getClaimsFromToken(String token, String secretKey) {
		Claims claims = null;
		try {
			claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}

	private static Date getExpirationDateFromToken(Claims claims) {
		Date expiration;
		try {
			expiration = claims.getExpiration();
		} catch (Exception e) {
			expiration = null;
		}
		return expiration;
	}
	
}
