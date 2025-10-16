package model;

public class Camioneta extends Vehiculo implements service.IAlquilable {
    private double capacidadCarga; // en kg

    public Camioneta(String marca, String modelo, int anio, TipoCombustible combustible, double capacidadCarga) {
        super(marca, modelo, anio, combustible);
        this.capacidadCarga = capacidadCarga;
    }

    public Camioneta(String marca, String modelo, int anio, double capacidadCarga) {
        super(marca, modelo, anio);
        this.capacidadCarga = capacidadCarga;
    }

    public Camioneta() { super(); this.capacidadCarga = 1000; }

    public double getCapacidadCarga() { return capacidadCarga; }
    public void setCapacidadCarga(double capacidadCarga) { this.capacidadCarga = capacidadCarga; }

    @Override
    public double calcularAlquiler(int dias) {
        double base = 5000.0;
        double extra = Math.min(2000, capacidadCarga / 100.0);
        return (base + extra) * dias;
    }

    @Override
    public String toString() {
        return "Camioneta - " + mostrar() + " - Carga: " + capacidadCarga + "kg";
    }
}