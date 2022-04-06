/*
 * Copyright (c) 2020 Malta Information Technology Agency <https://mita.gov.mt>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.malta.backend.sdk.authz.data.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

public class CovidCode {

	private Integer id;
	private String specimenNumber;
	private LocalDate receiveDate;
	
	private LocalDate onsetDate;
	private String transmissionRisk;
	private String authorisationCode;
	
	private Instant registeredAt;
	private String registeredBy;
	
	private Instant revokedAt;
	private String revokedBy;
	
	private Instant expiresAt;
	
	private Instant redeemedAt;
	
	private List<TokenIssueLog> issueLogs;
	
	public boolean isExpired() {
		Instant now = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC).toInstant();
		return now.isAfter(expiresAt);
	}	
	public boolean isRevoked() {
		return revokedAt != null;
	}	
	public boolean isRedeemed() {
		return redeemedAt != null;
	}	
	public boolean isClosed() {
		return isExpired() || isRedeemed() || isRevoked();
	}	
	
	public String getSpecimenNumber() {
		return specimenNumber;
	}
	public void setSpecimenNumber(String specimenNumber) {
		this.specimenNumber = specimenNumber;
	}
	public LocalDate getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(LocalDate receiveDate) {
		this.receiveDate = receiveDate;
	}
	public LocalDate getOnsetDate() {
		return onsetDate;
	}
	public void setOnsetDate(LocalDate onsetDate) {
		this.onsetDate = onsetDate;
	}
	public String getTransmissionRisk() {
		return transmissionRisk;
	}
	public void setTransmissionRisk(String transmissionRisk) {
		this.transmissionRisk = transmissionRisk;
	}
	public String getAuthorisationCode() {
		return authorisationCode;
	}
	public void setAuthorisationCode(String authorisationCode) {
		this.authorisationCode = authorisationCode;
	}
	public Instant getRegisteredAt() {
		return registeredAt;
	}
	public void setRegisteredAt(Instant registeredAt) {
		this.registeredAt = registeredAt;
	}
	public String getRegisteredBy() {
		return registeredBy;
	}
	public void setRegisteredBy(String registeredBy) {
		this.registeredBy = registeredBy;
	}
	public Instant getRevokedAt() {
		return revokedAt;
	}
	public void setRevokedAt(Instant revokedAt) {
		this.revokedAt = revokedAt;
	}
	public String getRevokedBy() {
		return revokedBy;
	}
	public void setRevokedBy(String revokedBy) {
		this.revokedBy = revokedBy;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Instant getExpiresAt() {
		return expiresAt;
	}
	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}
	public Instant getRedeemedAt() {
		return redeemedAt;
	}
	public void setRedeemedAt(Instant redeemedAt) {
		this.redeemedAt = redeemedAt;
	}
	public List<TokenIssueLog> getIssueLogs() {
		return issueLogs;
	}
	public void setIssueLogs(List<TokenIssueLog> issueLogs) {
		this.issueLogs = issueLogs;
	}
	
}
