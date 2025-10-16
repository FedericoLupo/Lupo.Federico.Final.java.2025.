package model;

public class Auto extends Vehiculo implements service.IAlquilable {
    private int puertas;

    public Auto(String marca, String modelo, int anio, TipoCombustible combustible, int puertas) {
        super(marca, modelo, anio, combustible);
        this.puertas = puertas;
    }

    public Auto(String marca, String modelo, int anio, int puertas) {
        super(marca, modelo, anio);
        this.puertas = puertas;
    }

    public Auto() { super(); this.puertas = 4; }

    public int getPuertas() { return puertas; }
    public void setPuertas(int puertas) { this.puertas = puertas; }

    @Override
    public double calcularAlquiler(int dias) {
        double base = 3000.0;
        double extra = puertas > 4 ? 500 : 0;
        return (base + extra) * dias;
    }

    @Override
    public String toString() {
        return "Auto - " + mostrar() + " - Puertas: " + puertas;
    }
}
