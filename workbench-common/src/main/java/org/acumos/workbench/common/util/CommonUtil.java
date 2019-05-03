package org.acumos.workbench.common.util;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

public class CommonUtil {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	/**
	 * Builds the rest API URI
	 * @param url
	 * 		URL of the rest API to be invoked.
	 * @param uriParams
	 * 		URI Path variable key-value map.  Map<String, String>
	 * @return URI
	 * 		Return UIR constructed based on the input parameters.
	 */
	public static URI buildURI(String url, Map<String, String> uriParams) { 
		logger.debug("buildURI() Begin");
		URI resultURI = null;
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
		if(null != uriParams) { 
			resultURI = uriBuilder.buildAndExpand(uriParams).encode().toUri();
		} else {
			resultURI = uriBuilder.build().encode().toUri();
		}
		logger.debug("buildURI() End");
		return resultURI;
	}
}
