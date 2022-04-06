package org.dpppt.malta.backend.sdk.authz.ws;

import org.dpppt.malta.backend.sdk.authz.data.AuthzDataService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class HealthCheck implements HealthIndicator {

	private AuthzDataService authzDataService;
		
	public HealthCheck(AuthzDataService authzDataService) {
		super();
		this.authzDataService = authzDataService;
	}

	@Override
    public Health health() {
        int errorCode = check(); // perform some specific health check
        if (errorCode != 0) {
            return Health.down()
              .withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }
     
    public int check() {
    	try {
    		authzDataService.search(null, Boolean.FALSE, 1, 1, null, Boolean.FALSE);
    	} catch (Exception e) {
    		return 1;
    	}
        return 0;
    }

}
