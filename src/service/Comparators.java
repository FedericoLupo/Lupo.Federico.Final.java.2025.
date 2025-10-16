package service;

import model.Vehiculo;
import java.util.Comparator;

public class Comparators {
    public static Comparator<Vehiculo> POR_MARCA = Comparator.comparing(Vehiculo::getMarca, String.CASE_INSENSITIVE_ORDER);
    public static Comparator<Vehiculo> POR_ANIO = Comparator.comparingInt(Vehiculo::getAnio);
    public static Comparator<Vehiculo> POR_MODELO = Comparator.comparing(Vehiculo::getModelo, String.CASE_INSENSITIVE_ORDER);
}