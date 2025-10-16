package model;

public class Moto extends Vehiculo implements service.IAlquilable {
    private int cilindrada;

    public Moto(String marca, String modelo, int anio, TipoCombustible combustible, int cilindrada) {
        super(marca, modelo, anio, combustible);
        this.cilindrada = cilindrada;
    }

    public Moto(String marca, String modelo, int anio, int cilindrada) {
        super(marca, modelo, anio);
        this.cilindrada = cilindrada;
    }

    public Moto() { super(); this.cilindrada = 150; }

    public int getCilindrada() { return cilindrada; }
    public void setCilindrada(int cilindrada) { this.cilindrada = cilindrada; }

    @Override
    public double calcularAlquiler(int dias) {
        double base = 1500.0;
        double extra = cilindrada > 200 ? 300 : 0;
        return (base + extra) * dias; 
    }

    @Override
    public String toString() {
        return "Moto - " + mostrar() + " - Cilindrada: " + cilindrada;
    }
}