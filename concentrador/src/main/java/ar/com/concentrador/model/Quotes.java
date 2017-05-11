package ar.com.concentrador.model;

import java.math.BigDecimal;
import java.util.Date;

public class Quotes {
	
	private String mercado;
	private String codeExtractor;
	private String code;
	private String description;	
	private Date date;
	private BigDecimal minValue;
	private BigDecimal maxValue;
	private String source;
	private String packageDes;
	private String value;
	
	public String getMercado() {
		return mercado;
	}
	public void setMercado(String mercado) {
		this.mercado = mercado;
	}
	public String getCodeExtractor() {
		return codeExtractor;
	}
	public void setCodeExtractor(String codeExtractor) {
		this.codeExtractor = codeExtractor;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public BigDecimal getMinValue() {
		return minValue;
	}
	public void setMinValue(BigDecimal minValue) {
		this.minValue = minValue;
	}
	public BigDecimal getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(BigDecimal maxValue) {
		this.maxValue = maxValue;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getPackageDes() {
		return packageDes;
	}
	public void setPackageDes(String packageDes) {
		this.packageDes = packageDes;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((codeExtractor == null) ? 0 : codeExtractor.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((maxValue == null) ? 0 : maxValue.hashCode());
		result = prime * result + ((mercado == null) ? 0 : mercado.hashCode());
		result = prime * result + ((minValue == null) ? 0 : minValue.hashCode());
		result = prime * result + ((packageDes == null) ? 0 : packageDes.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Quotes other = (Quotes) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (codeExtractor == null) {
			if (other.codeExtractor != null)
				return false;
		} else if (!codeExtractor.equals(other.codeExtractor))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (maxValue == null) {
			if (other.maxValue != null)
				return false;
		} else if (!maxValue.equals(other.maxValue))
			return false;
		if (mercado == null) {
			if (other.mercado != null)
				return false;
		} else if (!mercado.equals(other.mercado))
			return false;
		if (minValue == null) {
			if (other.minValue != null)
				return false;
		} else if (!minValue.equals(other.minValue))
			return false;
		if (packageDes == null) {
			if (other.packageDes != null)
				return false;
		} else if (!packageDes.equals(other.packageDes))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Quotes [mercado=" + mercado + ", codeExtractor=" + codeExtractor + ", code=" + code + ", description="
				+ description + ", date=" + date + ", minValue=" + minValue + ", maxValue=" + maxValue + ", source="
				+ source + ", packageDes=" + packageDes + ", value=" + value + "]";
	}
	
	
	
}
