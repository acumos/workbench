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

package org.acumos.workbench.notebookservice.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.acumos.workbench.common.exception.TargetServiceInvocationException;
import org.acumos.workbench.common.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.io.Files;

/**
 * Class used to add the server's certificate to the KeyStore
 * with your trusted certificates.  
 * The moment this class is required in other micro service we will move it to workbench-common
 */
@Component
public class CACertUtil {

	private static final String CACERTS = "cacerts";

	private static final String CERT_FILE_NAME = "jssecacerts";

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private ConfigurationProperties confprops;
	
	
	/**
	 * Adds the server's certificate to the KeyStore
	 * @return
	 * 		True if certificate installed and loaded successfully.  Else false if certificate is installed but not loaded.  
	 */
	@Deprecated
    public boolean installCert() {
    	boolean result = false;
		try {
			URI uri = CommonUtil.buildURI(confprops.getJupyterhubURL(), null);
			String host = uri.getHost();
			int port = uri.getPort();
			String storepass = confprops.getJupyterhubStorepass();

			char[] passphrase = storepass.toCharArray();
			String path = confprops.getOutputFolder();
			char SEP = File.separatorChar;
			path = path.endsWith(String.valueOf(SEP))? path+CERT_FILE_NAME : path + SEP + CERT_FILE_NAME;
			String jreSecurityPath = System.getProperty("java.home") + SEP + "lib" + SEP + "security";
			File file = new File(path);
			File jreSecurityDir = new File(jreSecurityPath);
			if (file.isFile() == false) {
	            file = new File(jreSecurityDir, CERT_FILE_NAME);
	            if (file.isFile() == false) {
	                file = new File(jreSecurityDir, CACERTS);
	            }
	        }
			logger.debug("Loading KeyStore " + file + "...");
			InputStream in = new FileInputStream(file);
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(in, passphrase);
			in.close();

			SSLContext context = SSLContext.getInstance("TLS");
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory
					.getDefaultAlgorithm());
			tmf.init(ks);
			X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
			SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
			context.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory factory = context.getSocketFactory();

			logger.debug("Opening connection to " + host + ":" + port + "...");
			SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
			socket.setSoTimeout(10000);
			try {
				logger.debug("Starting SSL handshake...");
				socket.startHandshake();
				socket.close();
				logger.debug("No errors, certificate is already trusted and loaded");
				result = true;
			} catch (SSLException e) {
				logger.error("Socket Exception", e);
			}

			if(!result) {
				X509Certificate[] chain = tm.chain;
				if (chain == null) {
					logger.debug("Could not obtain server certificate chain");
					throw new TargetServiceInvocationException("Could not obtain server certificate chain");
				}

				int k = 0;
				X509Certificate cert = chain[k];
				String alias = host + "-" + (k + 1);
				ks.setCertificateEntry(alias, cert);

				OutputStream out = new FileOutputStream(path);
				ks.store(out, passphrase);
				out.close();

				logger.debug(cert.toString());
				
				//Copy certificate to  
				Path src = Paths.get(path);
				Path dest = Paths.get(jreSecurityPath + SEP + CERT_FILE_NAME);
				Files.move(src.toFile(), dest.toFile());
				logger.debug("Added certificate to keystore 'jssecacerts' using alias '" + alias + "'");
			}
		} catch(Exception e) {
			logger.error("Error while installing Certificate", e);
			throw new TargetServiceInvocationException("Error while installing Certificate");
    	}
		return result;
    }
    
    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }

}
