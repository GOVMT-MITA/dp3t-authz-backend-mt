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
import java.util.UUID;

public class TokenIssueLog {

	private UUID uuid;
	private Instant issuedAt;
		
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public Instant getIssuedAt() {
		return issuedAt;
	}
	public void setIssuedAt(Instant issuedAt) {
		this.issuedAt = issuedAt;
	}
	
	
}
