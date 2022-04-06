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

import java.util.List;

public class CovidCodesPageResponseModel {

	private List<CovidCodeResponseModel> covidCodes;
	private int total;
	
	
	public CovidCodesPageResponseModel(List<CovidCodeResponseModel> covidCodes, int total) {
		super();
		this.covidCodes = covidCodes;
		this.total = total;
	}
	
	public List<CovidCodeResponseModel> getCovidCodes() {
		return covidCodes;
	}
	public int getTotal() {
		return total;
	}
	
}
