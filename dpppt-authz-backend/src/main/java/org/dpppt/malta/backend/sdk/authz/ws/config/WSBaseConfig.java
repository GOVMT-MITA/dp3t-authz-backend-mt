/*
 * Copyright (c) 2020 Malta Information Technology Agency <https://mita.gov.mt>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.malta.backend.sdk.authz.ws.config;

import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;

import javax.sql.DataSource;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.dpppt.malta.backend.sdk.authz.data.AuthzDataService;
import org.dpppt.malta.backend.sdk.authz.data.JDBCAuthzDataServiceImpl;
import org.dpppt.malta.backend.sdk.authz.service.CsvDumpService;
import org.dpppt.malta.backend.sdk.authz.ws.HealthCheck;
import org.dpppt.malta.backend.sdk.authz.ws.controller.CovidCodesController;
import org.dpppt.malta.backend.sdk.authz.ws.controller.DPPPTAuthzController;
import org.dpppt.malta.backend.sdk.authz.ws.filter.ResponseWrapperFilter;
import org.dpppt.malta.backend.sdk.authz.ws.security.JwtTokenProvider;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Configuration
public abstract class WSBaseConfig implements SchedulingConfigurer, WebMvcConfigurer {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	final SignatureAlgorithm algorithm = SignatureAlgorithm.ES256;

	@Value("${ws.headers.protected:}")
	List<String> protectedHeaders;

	@Value("${ws.retentiondays: 21}")
	int retentionDays;

	@Value("${authz.jwt.allowfakes:false}")
	boolean allowFakes;
	
	public abstract DataSource dataSource();

	public abstract Flyway flyway();

	public abstract String getDbType();

	abstract String getHashFilterPublicKey();
	abstract String getHashFilterPrivateKey();

	abstract String getJwtPublicKey();
	abstract String getJwtPrivateKey();
	
	private JwtTokenProvider jwtTokenProvider() {
		return new JwtTokenProvider(getKeyPair(SignatureAlgorithm.RS256,getJwtPrivateKey(),getJwtPublicKey()));
	}
	
	@Bean
	public HealthCheck healthCheck(AuthzDataService authzDataService) {
		return new HealthCheck(authzDataService);
	}
	
	@Bean
	public DPPPTAuthzController dppptSDKController(AuthzDataService authzDataService) {
		return new DPPPTAuthzController(jwtTokenProvider(), authzDataService, allowFakes);
	}

	@Bean
	public CsvDumpService csvDumpService() {
		return new CsvDumpService();
	}

	@Bean
	public CovidCodesController covidCodesController(CsvDumpService csvDumpService, AuthzDataService authzDataService, Environment environment) {
		return new CovidCodesController(csvDumpService, authzDataService, environment);
	}
	
	@Bean
	public AuthzDataService authzDataService() {
		return new JDBCAuthzDataServiceImpl(getDbType(), dataSource());
	}
	
	@Bean
	public ResponseWrapperFilter hashFilter() {
		return new ResponseWrapperFilter(getKeyPair(SignatureAlgorithm.ES256, getHashFilterPrivateKey(), getHashFilterPublicKey()), retentionDays, protectedHeaders);
	}

	public KeyPair getKeyPair(SignatureAlgorithm algorithm, String privateKey, String publicKey) {
		if (privateKey.isEmpty() && publicKey.isEmpty()) {
			return getKeyPair(algorithm);
		}
		Security.addProvider(new BouncyCastleProvider());
		Security.setProperty("crypto.policy", "unlimited");
		return new KeyPair(loadPublicKeyFromString(algorithm, publicKey),loadPrivateKeyFromString(algorithm, privateKey));
	}

	private PrivateKey loadPrivateKeyFromString(SignatureAlgorithm algorithm, String privateKey) {
		try {
			Reader reader = new StringReader(privateKey);
			PemReader readerPem = new PemReader(reader);
			PemObject obj = readerPem.readPemObject();
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(obj.getContent());
			KeyFactory kf = KeyFactory.getInstance(algorithm.getFamilyName(), new BouncyCastleProvider());
			return (PrivateKey) kf.generatePrivate(pkcs8KeySpec);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException();
		}
	}

	private PublicKey loadPublicKeyFromString(SignatureAlgorithm algorithm, String publicKey) {
		try {
			Reader reader = new StringReader(publicKey);
			PemReader readerPem = new PemReader(reader);
			PemObject obj = readerPem.readPemObject();
			readerPem.close();
			return KeyFactory.getInstance(algorithm.getFamilyName()).generatePublic(new X509EncodedKeySpec(obj.getContent()));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException();
		}
	}

	public KeyPair getKeyPair(SignatureAlgorithm algorithm) {
		logger.warn("USING FALLBACK KEYPAIR. WONT'T PERSIST APP RESTART AND PROBABLY DOES NOT HAVE ENOUGH ENTROPY.");

		return Keys.keyPairFor(algorithm);
	}
	
}
