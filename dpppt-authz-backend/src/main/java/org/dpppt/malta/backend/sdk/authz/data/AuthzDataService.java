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

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.dpppt.malta.backend.sdk.authz.data.model.CovidCode;
import org.dpppt.malta.backend.sdk.authz.data.model.CovidCodesPage;

public interface AuthzDataService {

	
	public CovidCode insertCovidCode(CovidCode covidCode);
	
	public CovidCode get(int id);
	
	public CovidCode fetchByAuthCode(String authCode);
	
	public CovidCode updateRevoked(int id, Instant at, String by);
	
	public CovidCodesPage search(String query, boolean all, int start, int size, String sort, boolean desc);
	
	public boolean authCodeExists(String authCode);

	public boolean specimenNumberExists(String specimenNumber);
	
	void updateRedeemed(int id, Instant at);
	
	public int insertTokenIssueLog(CovidCode covidCode, Instant issuedAt, UUID uuid);
	
	void cleanDB(Duration retentionPeriod);
	
	List<CovidCode> getAll();
	
}
