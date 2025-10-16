package service;

import model.*;
import exceptions.ArchivoException;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GestionVehiculos<T extends Vehiculo> implements ICrud<T>, Iterable<T>, Serializable {
    private static final long serialVersionUID = 2L;

    private List<T> lista = new ArrayList<>();

    @Override
    public void agregar(T entidad) { lista.add(entidad); }

    @Override
    public List<T> listar() { return Collections.unmodifiableList(lista); }

    @Override
    public void actualizar(int index, T entidad) { lista.set(index, entidad); }

    @Override
    public void eliminar(int index) { lista.remove(index); }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int i = 0;
            public boolean hasNext() { return i < lista.size(); }
            public T next() { return lista.get(i++); }
        };
    }

    // Ordenamientos
    public void ordenarPorAnio() { lista.sort(Comparator.comparingInt(Vehiculo::getAnio)); }
    public void ordenarPorModelo() { lista.sort(Comparator.comparing(Vehiculo::getModelo, String.CASE_INSENSITIVE_ORDER)); }

    // Filtrado con wildcards
    @SuppressWarnings("unchecked")
    public <U extends T> List<U> filtrarExtends(Predicate<? super U> pred) {
        return lista.stream().filter(v -> pred.test((U)v)).map(v -> (U)v).collect(Collectors.toList());
    }

    public boolean existeMatchSuper(Predicate<? super T> pred) {
        return lista.stream().anyMatch(pred);
    }

    public void aplicarCambio(Consumer<? super T> consumer) {
        lista.forEach(consumer);
    }

    public void serializarBinario(String path) throws ArchivoException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(lista);
        } catch (IOException e) {
            throw new ArchivoException("Error al serializar: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void deserializarBinario(String path) throws ArchivoException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Object obj = ois.readObject();
            this.lista = (List<T>) obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new ArchivoException("Error al deserializar: " + e.getMessage());
        }
    }

    public void exportarJSON(String path) throws ArchivoException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("[");
            for (int i = 0; i < lista.size(); i++) {
                Vehiculo v = lista.get(i);
                String tipo = v.getClass().getSimpleName();
                String extra = "";
                if (v instanceof Auto) extra = "\"puertas\": " + ((Auto) v).getPuertas();
                if (v instanceof Moto) extra = "\"cilindrada\": " + ((Moto) v).getCilindrada();
                if (v instanceof Camioneta) extra = "\"capacidadCarga\": " + ((Camioneta) v).getCapacidadCarga();

                pw.printf("  { \"tipo\": \"%s\", \"marca\": \"%s\", \"modelo\": \"%s\", \"anio\": %d, \"combustible\": \"%s\", %s }",
                        tipo, v.getMarca(), v.getModelo(), v.getAnio(), v.getCombustible(), extra);

                if (i < lista.size() - 1) pw.println(",");
                else pw.println();
            }
            pw.println("]");
        } catch (IOException e) {
            throw new ArchivoException("Error exportando JSON: " + e.getMessage());
        }
    }

    public void exportarCSV(String path) throws ArchivoException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("tipo,marca,modelo,anio,combustible,atributoExtra");
            for (T v : lista) {
                String tipo = v.getClass().getSimpleName();
                String extra = "";
                if (v instanceof Auto) extra = String.valueOf(((Auto) v).getPuertas());
                if (v instanceof Moto) extra = String.valueOf(((Moto) v).getCilindrada());
                if (v instanceof Camioneta) extra = String.valueOf(((Camioneta) v).getCapacidadCarga());
                pw.printf("%s,%s,%s,%d,%s,%s%n", tipo, v.getMarca(), v.getModelo(), v.getAnio(), v.getCombustible(), extra);
            }
        } catch (IOException e) {
            throw new ArchivoException("Error exportando CSV: " + e.getMessage());
        }
    }

    public void exportarTxtFiltrado(String path, String encabezado, Predicate<? super T> pred) throws ArchivoException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println(encabezado);
            pw.println("----------------------------------------");
            filtrarExtends(v -> pred.test(v)).forEach(v -> pw.println(v.toString()));
        } catch (IOException e) {
            throw new ArchivoException("Error exportando TXT: " + e.getMessage());
        }
    }
}
