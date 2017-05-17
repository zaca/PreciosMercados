package ar.com.concentrador.enums;

public enum ProductTypes {
	FRUTAS("Frutas", "F"), HORTALIZAS("Hortalizas", "H"), VERDURAS("Verduras", "V");

	private String descripcion;
	private String id;

	private ProductTypes(String description, String id) {
		this.descripcion = description;
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
