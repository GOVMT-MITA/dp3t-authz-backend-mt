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

import java.util.List;

public class CovidCodesPage {

	private int total;
	private List<CovidCode> covidCodes;
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<CovidCode> getCovidCodes() {
		return covidCodes;
	}
	public void setCovidCodes(List<CovidCode> covidCodes) {
		this.covidCodes = covidCodes;
	}
}
