package banca.model;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class Movimiento {
    private int idMovimiento;
    private int idCuenta;
    private String tipo;          // DEPOSITO | RETIRO | TRANSFERENCIA | PAGO_SERVICIO
    private BigDecimal monto;
    private String descripcion;
    private LocalDateTime fecha;
    public Movimiento() { }
    public Movimiento(int idCuenta, String tipo, BigDecimal monto, String descripcion) {
        this.idCuenta = idCuenta;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
    }
    public boolean isIngreso() {
        return "DEPOSITO".equals(tipo)
                || (descripcion != null && descripcion.startsWith("Transferencia recibida"));
    }
    public int getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(int idMovimiento) { this.idMovimiento = idMovimiento; }
    public int getIdCuenta() { return idCuenta; }
    public void setIdCuenta(int idCuenta) { this.idCuenta = idCuenta; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
