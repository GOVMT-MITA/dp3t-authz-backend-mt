/*
 * Copyright (c) 2020 Malta Information Technology Agency <https://mita.gov.mt>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.malta.backend.sdk.authz.ws.model;

import java.time.Instant;
import java.util.List;

import org.dpppt.malta.backend.sdk.authz.data.model.TokenIssueLog;

public class CovidCodeResponseModel {

	private int id;
	private String specimenNumber;
	private String receiveDate;
	
	private String onsetDate;
	private String transmissionRisk;
	private String authorisationCode;
	
	private String registeredAt;
	private String registeredBy;
	
	private String revokedAt;
	private String revokedBy;
	
	private String expiresAt;
	private String redeemedAt;

	private List<TokenIssueLogResponseModel> issueLogs;
	
	public List<TokenIssueLogResponseModel> getIssueLogs() {
		return issueLogs;
	}
	public void setIssueLogs(List<TokenIssueLogResponseModel> issueLogs) {
		this.issueLogs = issueLogs;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSpecimenNumber() {
		return specimenNumber;
	}
	public void setSpecimenNumber(String specimenNumber) {
		this.specimenNumber = specimenNumber;
	}
	public String getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(String receiveDate) {
		this.receiveDate = receiveDate;
	}
	public String getOnsetDate() {
		return onsetDate;
	}
	public void setOnsetDate(String onsetDate) {
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
	public String getAuthorisationCodePretty() {
		return authorisationCode.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{3})", "$1-$2-$3-$4");
	}
	public void setAuthorisationCode(String authorisationCode) {
		this.authorisationCode = authorisationCode;
	}
	public String getRegisteredAt() {
		return registeredAt;
	}
	public void setRegisteredAt(String registeredAt) {
		this.registeredAt = registeredAt;
	}
	public String getRegisteredBy() {
		return registeredBy;
	}
	public void setRegisteredBy(String registeredBy) {
		this.registeredBy = registeredBy;
	}
	public boolean isRevoked() {
		return revokedAt != null;
	}
	public String getRevokedAt() {
		return revokedAt;
	}
	public void setRevokedAt(String revokedAt) {
		this.revokedAt = revokedAt;
	}
	public String getRevokedBy() {
		return revokedBy;
	}
	public void setRevokedBy(String revokedBy) {
		this.revokedBy = revokedBy;
	}
	public String getExpiresAt() {
		return expiresAt;
	}
	public void setExpiresAt(String expiresAt) {
		this.expiresAt = expiresAt;
	}
	public boolean isRedeemed() {
		return redeemedAt != null;
	}
	public String getRedeemedAt() {
		return redeemedAt;
	}
	public void setRedeemedAt(String redeemedAt) {
		this.redeemedAt = redeemedAt;
	}
	
	
	
}
