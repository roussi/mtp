package com.vedri.mtp.core.country;

import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import com.google.common.base.Objects;
import com.vedri.mtp.core.currency.Currency;
import com.vedri.mtp.core.support.cassandra.ColumnUtils;

@Getter
@Setter
@ToString
public class Country {

	private String cca2;
	private String cca3;
	private String commonName;
	private String officialName;
	private Set<Currency> currencies;

	public Country() {
	}

	// fix issue with json synchronization by ignoring constructor in Jackson
	// issue was with ArrayOutOfBounds in jackson-module-paranamer
	@JsonIgnore
	public Country(String cca2, String cca3, String commonName, String officialName) {
		this.cca2 = cca2;
		this.cca3 = cca3;
		this.commonName = commonName;
		this.officialName = officialName;
	}

	public Country setupCurrencies(Set<String> currencies) {
		this.currencies = currencies.stream().map(Currency::new).collect(Collectors.toSet());
		return this;
	}
	
	public enum Fields {
		cca2, cca3, commonName, officialName, currencies;

		public final ColumnUtils.Field<Fields> F = ColumnUtils.createField(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Country country = (Country) o;
		return Objects.equal(cca2, country.cca2);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(cca2);
	}
}
