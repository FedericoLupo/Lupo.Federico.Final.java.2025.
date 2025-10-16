package service;

import java.util.List;

public interface ICrud<T> {
    void agregar(T entidad);
    List<T> listar();
    void actualizar(int index, T entidad);
    void eliminar(int index);
}