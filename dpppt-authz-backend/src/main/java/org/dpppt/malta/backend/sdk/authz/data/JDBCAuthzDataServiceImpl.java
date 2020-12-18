/*
 * Copyright (c) 2020 Malta Information Technology Agency <https://mita.gov.mt>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.malta.backend.sdk.authz.data;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.dpppt.malta.backend.sdk.authz.data.model.CovidCode;
import org.dpppt.malta.backend.sdk.authz.data.model.CovidCodesPage;
import org.dpppt.malta.backend.sdk.authz.data.model.TokenIssueLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

public class JDBCAuthzDataServiceImpl implements AuthzDataService {

	private static final Logger logger = LoggerFactory.getLogger(JDBCAuthzDataServiceImpl.class);
	private static final String PGSQL = "pgsql";
	
	private final String dbType;
	private final NamedParameterJdbcTemplate jt;
	private final DataSource dataSource;
	
	public JDBCAuthzDataServiceImpl(String dbType, DataSource dataSource) {
		this.dbType = dbType;
		this.jt = new NamedParameterJdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}
	
	@Override
	@Transactional(readOnly = false)
	public CovidCode insertCovidCode(CovidCode covidCode) {
		
		//String sql = "insert into t_covid_code (specimen_no, receive_date, onset_date, transmission_risk, auth_code, registered_at, registered_by, revoked_at, revoked_by) "
	    //        + " values (:specimen_no, :receive_date, :onset_date, :transmission_risk, :auth_code, :registered_at, :registered_by, :revoked_at, :revoked_by)";
		//jt.update(sql, params);

		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(this.dataSource)
				.withTableName("t_covid_code")
				.usingGeneratedKeyColumns("pk_covid_code_id")
				.usingColumns("specimen_no","receive_date","onset_date","transmission_risk","auth_code","registered_at", "registered_by", "expires_at");
		
		MapSqlParameterSource params = new MapSqlParameterSource();		
		params.addValue("specimen_no", covidCode.getSpecimenNumber());
		params.addValue("receive_date", covidCode.getReceiveDate());
		params.addValue("onset_date", covidCode.getOnsetDate());
		params.addValue("transmission_risk", covidCode.getTransmissionRisk());
		params.addValue("auth_code", covidCode.getAuthorisationCode());
		params.addValue("registered_at", Timestamp.from(covidCode.getRegisteredAt()));
		params.addValue("registered_by", covidCode.getRegisteredBy());
		params.addValue("expires_at", Timestamp.from(covidCode.getExpiresAt()));
		
		KeyHolder keys = simpleJdbcInsert.executeAndReturnKeyHolder(params);
		covidCode.setId((Integer) keys.getKeys().get("pk_covid_code_id"));
		
		return covidCode;
		
	}

	@Override
	public boolean authCodeExists(String authCode) {
		String sql = "select count(*) from t_covid_code where auth_code = :auth_code";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("auth_code", authCode);
		
		return jt.queryForObject(sql, params, Integer.class) != 0;
	}

	@Override
	public CovidCode get(int id) {
		
		String sql = "select * from t_covid_code where pk_covid_code_id = :pk_covid_code_id";		
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("pk_covid_code_id", id);
		
		CovidCode cc = jt.queryForObject(sql, params, new CovidCodeRowMapper());	
		
		sql = "select * from t_token_log where fk_covid_code_id = :fk_covid_code_id";
		MapSqlParameterSource params2 = new MapSqlParameterSource();
		params2.addValue("fk_covid_code_id", id);
		
		List<TokenIssueLog> logs = jt.query(sql, params2, new TokenIssueLogRowMapper());
		
		cc.setIssueLogs(logs);
		
		return cc;
		
	}

	@Override
	public CovidCode updateRevoked(int id, Instant at, String by) {
		
		String sql = "update t_covid_code set revoked_at = :revoked_at, revoked_by = :revoked_by where pk_covid_code_id = :pk_covid_code_id";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("pk_covid_code_id", id);
		params.addValue("revoked_at", Timestamp.from(at));
		params.addValue("revoked_by", by);
		
		int affected = jt.update(sql, params);
		if (affected != 1) {
			throw new IllegalStateException(String.format("CovidCode %s not found", id));
		}
		
		return get(id);
	}

	@Override
	public void updateRedeemed(int id, Instant at) {
		String sql = "update t_covid_code set redeemed_at = :redeemed_at where pk_covid_code_id = :pk_covid_code_id";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("pk_covid_code_id", id);
		params.addValue("redeemed_at", Timestamp.from(at));
		
		int affected = jt.update(sql, params);
		if (affected != 1) {
			throw new IllegalStateException(String.format("CovidCode %s not found", id));
		}
		
	}

	@Override
	public CovidCode fetchByAuthCode(String authCode) {
		String sql = "select * from t_covid_code where auth_code = :auth_code";		
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("auth_code", authCode);
		
		List<CovidCode> results =  jt.query(sql, params, new CovidCodeRowMapper());
		
		if (null == results || results.isEmpty()) {
			return null;
		} else if (results.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(1);
		} else {
			return results.get(0);			
		}
		
	}

	@Override
	public int insertTokenIssueLog(CovidCode covidCode, Instant issuedAt, UUID uuid) {

		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(this.dataSource)
				.withTableName("t_token_log")
				.usingGeneratedKeyColumns("pk_token_id")
				.usingColumns("fk_covid_code_id","uuid","issued_at");
		
		MapSqlParameterSource params = new MapSqlParameterSource();		
		params.addValue("fk_covid_code_id", covidCode.getId());
		params.addValue("uuid", uuid.toString());
		params.addValue("issued_at", Timestamp.from(issuedAt));
		
		KeyHolder keys = simpleJdbcInsert.executeAndReturnKeyHolder(params);
		
		return (Integer) keys.getKeys().get("pk_token_id");
		
	}

	@Override
	public CovidCodesPage search(String query, boolean all, int start, int size, String sort, boolean desc) {
		
		CovidCodesPage page = new CovidCodesPage();
		
		if (null == sort) sort = "specimen_no";

		MapSqlParameterSource params = new MapSqlParameterSource();		
		if (null != query && query.length() > 0) params.addValue("specimen_no", "%" + query + "%");
		
		String countSql = "select count(*)" + buildSearchQuery(query, all);		
		int total = jt.queryForObject(countSql, params, Integer.class);
		page.setTotal(total);
		if (total > 0) {	
			String sql = "select *";
			sql += buildSearchQuery(query, all);
			sql += " order by " + sort;		
			if (desc) sql += " desc";
			sql += " limit " + (size > 100 || size == 0 ? 100 : size);
			if (start > 0) {
				sql += " offset " + start;
			}
			logger.debug("Running query: " + sql);
			
			page.setCovidCodes(jt.query(sql, params, new CovidCodeRowMapper()));
		} else {
			page.setCovidCodes(new ArrayList<CovidCode>());
		}
		return page;
	}

	private String buildSearchQuery(String query, boolean all) {
		String sql = " from t_covid_code";
		if (null != query && query.length() > 0) {
			sql += " where specimen_no like :specimen_no";
		}
		if (!all) {
			sql += (null == query || query.length() == 0) ? " where" : " and";
			sql += " redeemed_at is null";			
			sql += " and revoked_at is null";
			sql += " and expires_at >= now()";
		}
		return sql;
	}

	@Override
	public boolean specimenNumberExists(String specimenNumber) {
		String sql = "select count(*) from t_covid_code where specimen_no = :specimen_no";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("specimen_no", specimenNumber);
		
		return jt.queryForObject(sql, params, Integer.class) != 0;
	}

	@Override
	public void cleanDB(Duration retentionPeriod) {
		OffsetDateTime retentionTime = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC)
				.minus(retentionPeriod);
		logger.info("Cleanup DB entries before: " + retentionTime);
		MapSqlParameterSource params = new MapSqlParameterSource("retention_time",
				Date.from(retentionTime.toInstant()));
		String sql = "delete from t_covid_code where expires_at < :retention_time";
		jt.update(sql, params);		
		
	}

	@Override
	public List<CovidCode> getAll() {
		
		String sql = "select * from t_covid_code";
		MapSqlParameterSource params = new MapSqlParameterSource();
		
		List<CovidCode> codes = jt.query(sql, params, new CovidCodeRowMapper());

		codes.forEach(c -> {
			
			String tlsql = "select * from t_token_log where fk_covid_code_id = :fk_covid_code_id";
			MapSqlParameterSource tlparams = new MapSqlParameterSource();
			tlparams.addValue("fk_covid_code_id", c.getId());
			
			List<TokenIssueLog> logs = jt.query(tlsql, tlparams, new TokenIssueLogRowMapper());
			
			c.setIssueLogs(logs);
			
		});
		
		return codes;		
		
	}

}
