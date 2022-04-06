/*
 * Copyright (c) 2020 Malta Information Technology Agency <https://mita.gov.mt>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.malta.backend.sdk.authz.ws.controller;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class CreateTokenResult {

	private Instant issuedAt;
	private UUID uuid;
	private String token;
	public Instant getIssuedAt() {
		return issuedAt;
	}
	public UUID getUuid() {
		return uuid;
	}
	public String getToken() {
		return token;
	}
	public CreateTokenResult(Instant issuedAt, UUID uuid, String token) {
		super();
		this.issuedAt = issuedAt;
		this.uuid = uuid;
		this.token = token;
	}
	
	
}
