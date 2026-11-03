package banca.model;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
public class Credito {
    private int idCredito;
    private int idCliente;
    private BigDecimal monto;
    private int plazoMeses;
    private BigDecimal tasaAnual;
    private String estado = "SOLICITADO";
    private String observaciones;
    private LocalDateTime fechaSolicitud;
    public BigDecimal cuotaMensual() {
        double r = tasaAnual.doubleValue() / 100.0 / 12.0;
        double m = monto.doubleValue();
        double cuota = (r == 0) ? m / plazoMeses : m * r / (1 - Math.pow(1 + r, -plazoMeses));
        return BigDecimal.valueOf(cuota).setScale(2, RoundingMode.HALF_UP);
    }
    public int getIdCredito() { return idCredito; }
    public void setIdCredito(int idCredito) { this.idCredito = idCredito; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
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
}
