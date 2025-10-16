package model;

import java.io.Serializable;

public abstract class Vehiculo implements Comparable<Vehiculo>, Serializable {
    private static final long serialVersionUID = 1L;

    protected String marca;
    protected String modelo;
    protected int anio;
    protected TipoCombustible combustible;

    public Vehiculo(String marca, String modelo, int anio, TipoCombustible combustible) {
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.combustible = combustible;
    }

    public Vehiculo(String marca, String modelo, int anio) {
        this(marca, modelo, anio, TipoCombustible.NAFTA);
    }

    public Vehiculo() {
        this("-", "-", 2000, TipoCombustible.NAFTA);
    }

    public abstract double calcularAlquiler(int dias);

    public String mostrar() {
        return String.format("%s %s (%d) - %s", marca, modelo, anio, combustible);
    }

    // getters y setters
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }
    public TipoCombustible getCombustible() { return combustible; }
    public void setCombustible(TipoCombustible combustible) { this.combustible = combustible; }

    @Override
    public int compareTo(Vehiculo o) {
        return this.marca.compareToIgnoreCase(o.marca);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vehiculo)) return false;
        Vehiculo v = (Vehiculo) obj;
        return marca.equalsIgnoreCase(v.marca) && modelo.equalsIgnoreCase(v.modelo) && anio == v.anio;
    }

    @Override
    public int hashCode() {
        return (marca + modelo + anio).hashCode();
    }
}
