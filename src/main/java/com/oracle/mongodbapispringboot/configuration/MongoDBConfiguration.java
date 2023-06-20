package com.oracle.mongodbapispringboot.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.connection.SslSettings;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * MongoDB settings configuration bean to reproduce mongosh tlsAllowInvalidCertificates=true option
 * as Oracle Database API for MongoDB requires tls to be enabled (i.e. tls=true or ssl=true inside the
 * connection string).
 *
 * @author loic.lefevre@oracle.com
 */
@Configuration
public class MongoDBConfiguration {
	@Value("${spring.data.mongodb.uri}")
	private String mongoDBURI;

	@Bean
	public MongoClientSettings mongoClientSettings() throws NoSuchAlgorithmException, KeyManagementException {
		final MongoClientSettings.Builder builder = MongoClientSettings.builder();

		// initialize SSLContext without certificate check
		final SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(new KeyManager[0], new TrustManager[]{new NoCertificateCheckTrustManager()}, new SecureRandom());

		// apply the new SSLContext to the MongoDB settings
		return builder.applyConnectionString(new ConnectionString(mongoDBURI))
				.applyToConnectionPoolSettings(b -> b.minSize(5))
				.uuidRepresentation(UuidRepresentation.JAVA_LEGACY)
				.applyToSslSettings(b -> b.enabled(true).context(ctx))
				.build();
	}

	/**
	 * Trust manager without any certificate check.
	 */
	private static class NoCertificateCheckTrustManager implements X509TrustManager {
		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}
