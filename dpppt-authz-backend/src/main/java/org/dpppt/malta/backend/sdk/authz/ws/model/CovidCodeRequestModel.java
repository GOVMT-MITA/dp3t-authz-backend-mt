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

public class CovidCodeRequestModel {

	private String specimenNumber;
	private String receiveDate;	
	private String onsetDate;
	private String transmissionRisk;
	
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

	
}
