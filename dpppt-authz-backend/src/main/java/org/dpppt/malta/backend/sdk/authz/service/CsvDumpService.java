package org.dpppt.malta.backend.sdk.authz.service;

import java.io.StringWriter;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.comparators.ComparableComparator;
import org.apache.commons.collections4.comparators.ComparatorChain;
import org.apache.commons.collections4.comparators.FixedOrderComparator;
import org.apache.commons.collections4.comparators.NullComparator;
import org.dpppt.malta.backend.sdk.authz.data.model.CovidCode;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

public class CsvDumpService {

	private static String[] order = new String[] {
			"ID",
			"SPECIMEN_NUMBER",
			"RECEIVE_DATE",
			"ONSET_DATE",
			"TRANSMISSION_RISK",
			"AUTHORISATION_CODE",
			"REGISTERED_AT",
			"REGISTERED_BY",
			"REVOKED_AT",
			"REVOKED_BY",
			"EXPIRES_AT",
			"REDEEMED_AT",
			"ISSUED_AT_1",
			"ISSUED_AT_2",
			"ISSUED_AT_3",
	};

	public String toCsv(List<CovidCode> res, ZoneOffset offset) throws Exception {
		
		final StringWriter writer = new StringWriter();
		
		HeaderColumnNameMappingStrategy<CovidCodeOutputLine> strategy = new HeaderColumnNameMappingStrategy<>();
		strategy.setType(CovidCodeOutputLine.class);
		strategy.setColumnOrderOnWrite(literalComparator());
		StatefulBeanToCsv<CovidCodeOutputLine> beanToCsv = new StatefulBeanToCsvBuilder<CovidCodeOutputLine>(writer)
				.withMappingStrategy(strategy)
				.withApplyQuotesToAll(true)
				.build();

		final DateTimeFormatter ldf = DateTimeFormatter.ISO_LOCAL_DATE;
		final DateTimeFormatter tsf = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(offset));
		
		beanToCsv.write(res.stream().map(code -> {
			
			CovidCodeOutputLine out = new CovidCodeOutputLine();
			out.id = String.valueOf(code.getId());
			out.specimen_number = code.getSpecimenNumber();
			out.receive_date = ldf.format(code.getReceiveDate());	
			out.onset_date = ldf.format(code.getOnsetDate());
			out.transmission_risk = code.getTransmissionRisk();
			out.authorisation_code = code.getAuthorisationCode().replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{3})", "$1-$2-$3-$4");	
			out.registered_at = tsf.format(asUTC(code.getRegisteredAt()));
			out.registered_by = code.getRegisteredBy();	
			if (null != code.getRevokedAt()) out.revoked_at = tsf.format(asUTC(code.getRevokedAt()));
			if (null != code.getRevokedBy()) out.revoked_by = code.getRevokedBy();
			out.expires_at = tsf.format(asUTC(code.getExpiresAt()));
			if (null != code.getRedeemedAt()) out.redeemed_at = tsf.format(asUTC(code.getRedeemedAt()));
			if (null != code.getIssueLogs()) {
				out.issued_at_1 = code.getIssueLogs().size() >= 1 ? tsf.format(asUTC(code.getIssueLogs().get(0).getIssuedAt())) : null;
				out.issued_at_2 = code.getIssueLogs().size() >= 2 ? tsf.format(asUTC(code.getIssueLogs().get(1).getIssuedAt())) : null;
				out.issued_at_3 = code.getIssueLogs().size() >= 3 ? tsf.format(asUTC(code.getIssueLogs().get(2).getIssuedAt())) : null;
			}
			return out;
		}));
		
		return writer.toString();		
		
	}

	private OffsetDateTime asUTC(Instant instant) {
		return OffsetDateTime.ofInstant(instant, ZoneId.from(ZoneOffset.UTC));
	}

	private Comparator<String> literalComparator() {
		List<String> predefinedList = List.of(order); 
		FixedOrderComparator<String> fixedComparator = new FixedOrderComparator<>(predefinedList); 
		fixedComparator.setUnknownObjectBehavior(FixedOrderComparator.UnknownObjectBehavior.AFTER); 
		Comparator<String> c = new ComparatorChain<>(List.of(fixedComparator, new NullComparator<>(false), new ComparableComparator<>()));
		return c;
	}
	
}
