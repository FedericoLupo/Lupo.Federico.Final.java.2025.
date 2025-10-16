package ui.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;
import service.GestionVehiculos;
import exceptions.ArchivoException;
import ui.App;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class MainController {

    // controls
    @FXML private ChoiceBox<String> cbTipo;
    @FXML private TextField tfMarca;
    @FXML private TextField tfModelo;
    @FXML private TextField tfAnio;
    @FXML private ChoiceBox<TipoCombustible> cbCombustible;
    @FXML private TextField tfExtra;
    @FXML private Label lblExtra;

    // Buttons
    @FXML private Button btnAgregar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnListar;
    @FXML private Button btnAplicarCambio;
    @FXML private Button btnExportarJSON;
    @FXML private Button btnExportarCSV;
    @FXML private Button btnSerializar;
    @FXML private Button btnDeserializar;
    @FXML private Button btnExportarTxtFiltrado;
    @FXML private Button btnFiltrar;
    @FXML private Button btnCalcularAlquiler;

    // Filter
    @FXML private TextField tfFiltroMarca;

    // Table
    @FXML private TableView<Vehiculo> tabla;
    @FXML private TableColumn<Vehiculo, String> colTipo;
    @FXML private TableColumn<Vehiculo, String> colMarca;
    @FXML private TableColumn<Vehiculo, String> colModelo;
    @FXML private TableColumn<Vehiculo, Integer> colAnio;
    @FXML private TableColumn<Vehiculo, TipoCombustible> colCombustible;
    @FXML private TableColumn<Vehiculo, String> colExtra;

    // Gestor
    private GestionVehiculos<Vehiculo> gestor = App.gestor;

    @FXML
    public void initialize() {
        // Inicializar choiceboxes
        cbTipo.setItems(FXCollections.observableArrayList("Auto", "Moto", "Camioneta"));
        cbTipo.setValue("Auto");

        cbCombustible.setItems(FXCollections.observableArrayList(TipoCombustible.values()));
        cbCombustible.setValue(TipoCombustible.NAFTA);

        // Columnas de la tabla (uso reflection / getters en modelos)
        colTipo.setCellValueFactory(cell -> {
            Vehiculo v = cell.getValue();
            return javafx.beans.property.SimpleStringProperty.stringExpression(
                    new javafx.beans.property.SimpleStringProperty(v.getClass().getSimpleName())
            );
        });
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colCombustible.setCellValueFactory(new PropertyValueFactory<>("combustible"));
        colExtra.setCellValueFactory(cell -> {
            Vehiculo v = cell.getValue();
            String extra = "";
            if (v instanceof Auto) extra = String.valueOf(((Auto) v).getPuertas());
            else if (v instanceof Moto) extra = String.valueOf(((Moto) v).getCilindrada());
            else if (v instanceof Camioneta) extra = String.valueOf(((Camioneta) v).getCapacidadCarga());
            return javafx.beans.property.SimpleStringProperty.stringExpression(
                    new javafx.beans.property.SimpleStringProperty(extra)
            );
        });

        // Listener para cambiar etiqueta del campo "extra" según tipo
        cbTipo.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if ("Auto".equals(newV)) lblExtra.setText("Puertas:");
            else if ("Moto".equals(newV)) lblExtra.setText("Cilindrada:");
            else lblExtra.setText("Carga(kg):");
        });

        // Asociar handlers a botones
        btnAgregar.setOnAction(e -> onAgregar());
        btnListar.setOnAction(e -> refrescarTabla());
        btnEliminar.setOnAction(e -> onEliminar());
        btnActualizar.setOnAction(e -> onActualizar());
        btnAplicarCambio.setOnAction(e -> onAplicarCambio());
        btnExportarJSON.setOnAction(e -> onExportarJSON());
        btnExportarCSV.setOnAction(e -> onExportarCSV());
        btnSerializar.setOnAction(e -> onSerializar());
        btnDeserializar.setOnAction(e -> onDeserializar());
        btnFiltrar.setOnAction(e -> onFiltrar());
        btnExportarTxtFiltrado.setOnAction(e -> onExportarTxtFiltrado());
        btnCalcularAlquiler.setOnAction(e -> onCalcularAlquiler());

        // Al hacer doble click en fila, cargar datos en formulario para editar
        tabla.setRowFactory(tv -> {
            TableRow<Vehiculo> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    Vehiculo v = row.getItem();
                    cargarFormulario(v);
                }
            });
            return row;
        });

        refrescarTabla();
    }

    private void cargarFormulario(Vehiculo v) {
        cbTipo.setValue(v.getClass().getSimpleName());
        tfMarca.setText(v.getMarca());
        tfModelo.setText(v.getModelo());
        tfAnio.setText(String.valueOf(v.getAnio()));
        cbCombustible.setValue(v.getCombustible());
        if (v instanceof Auto) tfExtra.setText(String.valueOf(((Auto) v).getPuertas()));
        else if (v instanceof Moto) tfExtra.setText(String.valueOf(((Moto) v).getCilindrada()));
        else if (v instanceof Camioneta) tfExtra.setText(String.valueOf(((Camioneta) v).getCapacidadCarga()));
    }

    private void onAgregar() {
        try {
            Vehiculo v = crearVehiculoDesdeFormulario();
            gestor.agregar(v);
            showInfo("Agregado", "Vehículo agregado correctamente.");
            limpiarFormulario();
            refrescarTabla();
        } catch (IllegalArgumentException ex) {
            showError("Error de entrada", ex.getMessage());
        } catch (Exception ex) {
            showError("Error", ex.getMessage());
        }
    }

    private void onActualizar() {
        Vehiculo sel = tabla.getSelectionModel().getSelectedItem();
        int idx = tabla.getSelectionModel().getSelectedIndex();
        if (sel == null) { showError("Seleccionar", "Seleccione un vehículo en la tabla para actualizar."); return; }
        try {
            Vehiculo nuevo = crearVehiculoDesdeFormulario();
            gestor.actualizar(idx, nuevo);
            showInfo("Actualizado", "Vehículo actualizado correctamente.");
            refrescarTabla();
        } catch (IllegalArgumentException ex) {
            showError("Error de entrada", ex.getMessage());
        } catch (Exception ex) {
            showError("Error", ex.getMessage());
        }
    }

    private void onEliminar() {
        int idx = tabla.getSelectionModel().getSelectedIndex();
        if (idx < 0) { showError("Seleccionar", "Seleccione un vehículo en la tabla para eliminar."); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar el vehículo seleccionado?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText("Confirmar eliminación");
        Optional<ButtonType> res = a.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            gestor.eliminar(idx);
            refrescarTabla();
            showInfo("Eliminado", "Vehículo eliminado.");
        }
    }

    private void onAplicarCambio() {
        gestor.aplicarCambio(v -> v.setAnio(v.getAnio() + 1));
        refrescarTabla();
        showInfo("Aplicado", "Se incrementó el año en +1 para todos los vehículos.");
    }

    private void onExportarJSON() {
        try {
            String path = "vehiculos.json";
            gestor.exportarJSON(path);
            showInfo("Exportado", "JSON exportado a " + new File(path).getAbsolutePath());
        } catch (ArchivoException ex) {
            showError("Error exportando JSON", ex.getMessage());
        }
    }

    private void onExportarCSV() {
        try {
            String path = "vehiculos.csv";
            gestor.exportarCSV(path);
            showInfo("Exportado", "CSV exportado a " + new File(path).getAbsolutePath());
        } catch (ArchivoException ex) {
            showError("Error exportando CSV", ex.getMessage());
        }
    }

    private void onSerializar() {
        try {
            String path = "vehiculos.dat";
            gestor.serializarBinario(path);
            showInfo("Serializado", "Lista serializada a " + new File(path).getAbsolutePath());
        } catch (ArchivoException ex) {
            showError("Error serializando", ex.getMessage());
        }
    }

    private void onDeserializar() {
        try {
            String path = "vehiculos.dat";
            gestor.deserializarBinario(path);
            refrescarTabla();
            showInfo("Cargado", "Datos cargados desde " + new File(path).getAbsolutePath());
        } catch (ArchivoException ex) {
            showError("Error cargando", ex.getMessage());
        }
    }

    private void onFiltrar() {
        String term = tfFiltroMarca.getText();
        if (term == null || term.isBlank()) { refrescarTabla(); return; }
        Predicate<Vehiculo> pred = v -> v.getMarca().toLowerCase().contains(term.toLowerCase());
        List<Vehiculo> res = gestor.filtrarExtends(pred);
        tabla.setItems(FXCollections.observableArrayList(res));
    }

    private void onExportarTxtFiltrado() {
        try {
            String term = tfFiltroMarca.getText();
            if (term == null || term.isBlank()) {
                showError("Filtro vacío", "Ingrese una marca o parte de ella para filtrar y exportar.");
                return;
            }
            Predicate<Vehiculo> pred = v -> v.getMarca().toLowerCase().contains(term.toLowerCase());
            String encabezado = "Listado de Vehículos filtrados por: marca contiene \"" + term + "\"";
            String path = "vehiculos_filtrados.txt";
            gestor.exportarTxtFiltrado(path, encabezado, pred);
            showInfo("Exportado", "TXT exportado a " + new File(path).getAbsolutePath());
        } catch (ArchivoException ex) {
            showError("Error exportando TXT", ex.getMessage());
        }
    }
    
    private void onCalcularAlquiler() {
        Vehiculo seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            showError("Seleccionar", "Seleccione un vehículo en la tabla para calcular el alquiler.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setHeaderText("Calcular Alquiler");
        dialog.setContentText("Ingrese cantidad de días:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int dias = Integer.parseInt(result.get().trim());
                if (dias <= 0) throw new NumberFormatException("Debe ser mayor a 0");

                if (seleccionado instanceof service.IAlquilable ||
                    seleccionado instanceof model.Auto ||
                    seleccionado instanceof model.Moto ||
                    seleccionado instanceof model.Camioneta) {
                    double precio = seleccionado.calcularAlquiler(dias);
                    showInfo("Precio de alquiler", String.format("Vehículo: %s%nDías: %d%nPrecio total: $ %.2f",
                            seleccionado.mostrar(), dias, precio));
                } else {
                    showError("No alquilable", "Este vehículo no implementa IAlquilable.");
                }
            } catch (NumberFormatException ex) {
                showError("Entrada inválida", "Ingrese un número entero positivo de días.");
            } catch (Exception ex) {
                showError("Error", ex.getMessage());
            }
        }
    }
    
    private Vehiculo crearVehiculoDesdeFormulario() {
        String tipo = cbTipo.getValue();
        String marca = tfMarca.getText();
        String modelo = tfModelo.getText();
        String anioStr = tfAnio.getText();
        TipoCombustible comb = cbCombustible.getValue();
        String extra = tfExtra.getText();

        if (marca == null || marca.isBlank()) throw new IllegalArgumentException("Marca vacía.");
        if (modelo == null || modelo.isBlank()) throw new IllegalArgumentException("Modelo vacío.");
        int anio;
        try { anio = Integer.parseInt(anioStr); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Año inválido."); }

        switch (tipo) {
            case "Auto":
                int puertas = 4;
                try { puertas = Integer.parseInt(extra); } catch (Exception ignored) {}
                return new Auto(marca, modelo, anio, comb, puertas);
            case "Moto":
                int cil = 150;
                try { cil = Integer.parseInt(extra); } catch (Exception ignored) {}
                return new Moto(marca, modelo, anio, comb, cil);
            case "Camioneta":
                double carga = 1000;
                try { carga = Double.parseDouble(extra); } catch (Exception ignored) {}
                return new Camioneta(marca, modelo, anio, comb, carga);
            default:
                throw new IllegalArgumentException("Tipo desconocido.");
        }
    }

    private void refrescarTabla() {
        tabla.setItems(FXCollections.observableArrayList(gestor.listar()));
    }

    private void limpiarFormulario() {
        tfMarca.clear();
        tfModelo.clear();
        tfAnio.clear();
        tfExtra.clear();
        cbCombustible.setValue(TipoCombustible.NAFTA);
        cbTipo.setValue("Auto");
    }

    private void showError(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(titulo);
        a.showAndWait();
    }

    private void showInfo(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(titulo);
        a.showAndWait();
    }
}