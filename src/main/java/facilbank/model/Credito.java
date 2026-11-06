package facilbank.model;

import facilbank.patterns.state.EstadoCredito;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Credito {
    private int idCredito;
    private int idCliente;
    private Integer idAsesor;
    private BigDecimal monto;
    private int plazoMeses;
    private BigDecimal tasaAnual;
    private String estado = "SOLICITADO";
    private String observaciones;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaEvaluacion;
    private String clienteNombre;
    private String clienteDni;

    public void iniciarEvaluacion() {
        estado = EstadoCredito.desde(estado).evaluar().nombre();
    }

    public void aprobar() {
        estado = EstadoCredito.desde(estado).aprobar().nombre();
    }

    public void rechazar() {
        estado = EstadoCredito.desde(estado).rechazar().nombre();
    }

    public BigDecimal cuotaMensual() {
        double r = tasaAnual.doubleValue() / 100.0 / 12.0;
        double m = monto.doubleValue();
        double cuota = (r == 0) ? m / plazoMeses : m * r / (1 - Math.pow(1 + r, -plazoMeses));
        return BigDecimal.valueOf(cuota).setScale(2, RoundingMode.HALF_UP);
    }

    public List<Cuota> generarCronograma() {
        List<Cuota> lista = new ArrayList<>();
        BigDecimal tasaMensual = tasaAnual.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal cuota = cuotaMensual();
        BigDecimal saldo = monto;
        LocalDate base = (fechaEvaluacion != null ? fechaEvaluacion : LocalDateTime.now()).toLocalDate();
        for (int i = 1; i <= plazoMeses; i++) {
            BigDecimal interes = saldo.multiply(tasaMensual).setScale(2, RoundingMode.HALF_UP);
            BigDecimal capital = cuota.subtract(interes);
            BigDecimal cuotaFinal = cuota;
            if (i == plazoMeses) {
                capital = saldo;
                cuotaFinal = capital.add(interes);
            }
            saldo = saldo.subtract(capital);
            lista.add(new Cuota(i, base.plusMonths(i), cuotaFinal, interes, capital, saldo));
        }
        return lista;
    }

    public int getIdCredito() { return idCredito; }
    public void setIdCredito(int idCredito) { this.idCredito = idCredito; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public Integer getIdAsesor() { return idAsesor; }
    public void setIdAsesor(Integer idAsesor) { this.idAsesor = idAsesor; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public int getPlazoMeses() { return plazoMeses; }
    public void setPlazoMeses(int plazoMeses) { this.plazoMeses = plazoMeses; }
    public BigDecimal getTasaAnual() { return tasaAnual; }
    public void setTasaAnual(BigDecimal tasaAnual) { this.tasaAnual = tasaAnual; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public LocalDateTime getFechaEvaluacion() { return fechaEvaluacion; }
    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) { this.fechaEvaluacion = fechaEvaluacion; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getClienteDni() { return clienteDni; }
    public void setClienteDni(String clienteDni) { this.clienteDni = clienteDni; }
}
