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

import java.util.Base64;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("cloud-dev")
public class WSDevConfig extends WSBaseConfig {
	
	@Value("${vcap.services.ecdsa_cs_dev.credentials.privateKey:}")
	private String hashFilterPrivateKey;
	@Value("${vcap.services.ecdsa_cs_dev.credentials.publicKey:}")
    public String hashFilterPublicKey;

	@Value("${authz.dev.jwt.privateKey:}")
	private String jwtPrivateKey;
	@Value("${authz.dev.jwt.publicKey:}")
    public String jwtPublicKey;
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

	}
    
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

    @Bean
	@Override
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).build();
	}

	@Bean
	@Override
	public Flyway flyway() {
		Flyway flyWay = Flyway.configure().dataSource(dataSource()).locations("classpath:/db/migration/hsqldb").load();
		flyWay.migrate();
		return flyWay;
	}

	@Override
	public String getDbType() {
		return "hsqldb";
	}
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**");
			}
		};
	}

}
