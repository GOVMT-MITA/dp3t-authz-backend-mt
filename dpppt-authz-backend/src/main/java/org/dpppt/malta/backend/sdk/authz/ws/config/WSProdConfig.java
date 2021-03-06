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

import java.time.Duration;
import java.util.Base64;
import java.util.Properties;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@Profile("cloud-prod")
public class WSProdConfig extends WSBaseConfig {
	@Value("${vcap.services.ecdsa_cs_prod.credentials.privateKey}")
	private String hashFilterPrivateKey;
	@Value("${vcap.services.ecdsa_cs_prod.credentials.publicKey}")
    public String hashFilterPublicKey;

	@Value("${authz.prod.jwt.privateKey}")
	private String jwtPrivateKey;
	@Value("${authz.prod.jwt.publicKey}")
    public String jwtPublicKey;
	
	@Value("${datasource.username}")
	String dataSourceUser;

	@Value("${datasource.password}")
	String dataSourcePassword;

	@Value("${datasource.url}")
	String dataSourceUrl;

	@Value("${datasource.driverClassName}")
	String dataSourceDriver;

	@Value("${datasource.failFast}")
	String dataSourceFailFast;

	@Value("${datasource.maximumPoolSize}")
	String dataSourceMaximumPoolSize;

	@Value("${datasource.maxLifetime}")
	String dataSourceMaxLifetime;

	@Value("${datasource.idleTimeout}")
	String dataSourceIdleTimeout;

	@Value("${datasource.connectionTimeout}")
	String dataSourceConnectionTimeout;

    @Override
    String getHashFilterPrivateKey() {
        return new String(Base64.getDecoder().decode(hashFilterPrivateKey));
    }
    @Override
    String getHashFilterPublicKey() {
        return new String(Base64.getDecoder().decode(hashFilterPublicKey));
    }

    @Override
    String getJwtPrivateKey() {
        return new String(Base64.getDecoder().decode(jwtPrivateKey));
    }
    @Override
    String getJwtPublicKey() {
        return new String(Base64.getDecoder().decode(jwtPublicKey));
    }

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		HikariConfig config = new HikariConfig();
		Properties props = new Properties();
		props.put("url", dataSourceUrl);
		props.put("user", dataSourceUser);
		props.put("password", dataSourcePassword);
		config.setDataSourceProperties(props);
		config.setDataSourceClassName(dataSourceDriver);
		config.setMaximumPoolSize(Integer.parseInt(dataSourceMaximumPoolSize));
		config.setMaxLifetime(Integer.parseInt(dataSourceMaxLifetime));
		config.setIdleTimeout(Integer.parseInt(dataSourceIdleTimeout));
		config.setConnectionTimeout(Integer.parseInt(dataSourceConnectionTimeout));
		return new HikariDataSource(config);
	}

	@Bean
	@Override
	public Flyway flyway() {
		Flyway flyWay = Flyway.configure().dataSource(dataSource()).locations("classpath:/db/migration/pgsql").load();
		flyWay.migrate();
		return flyWay;
	}

	@Override
	public String getDbType() {
		return "pgsql";
	}
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addFixedRateTask(new IntervalTask(() -> {
			logger.info("Start DB cleanup");
			authzDataService().cleanDB(Duration.ofDays(retentionDays));
			logger.info("DB cleanup up");
		}, 60 * 60 * 1000L));
	}

}
