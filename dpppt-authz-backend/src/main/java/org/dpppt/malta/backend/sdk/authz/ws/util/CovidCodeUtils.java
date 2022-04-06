package org.dpppt.malta.backend.sdk.authz.ws.util;

import java.security.SecureRandom;

public abstract class CovidCodeUtils {

	public static String generateRandom12DigitCode() {
		SecureRandom rand = new SecureRandom();
		String res = "";
		
		StringBuilder authCode = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			authCode.append(String.format("%03d", rand.nextInt(1000)));
		}
		res = authCode.toString();
		
		return res;
		
	}

}
