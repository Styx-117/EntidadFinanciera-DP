package banca.model;

import java.math.BigDecimal;

/**
 * GRASP EXPERTO: la Cuenta es dueña del saldo, por lo tanto ella misma
 * valida y calcula los depósitos y retiros.
 */
public class Cuenta {
    private int idCuenta;
    private int idCliente;
    private String numeroCuenta;
    private String tipo;
    private BigDecimal saldo = BigDecimal.ZERO;
    private String estado = "ACTIVA";

    public Cuenta() { }

    public Cuenta(int idCliente, String numeroCuenta, String tipo) {
        this.idCliente = idCliente;
        this.numeroCuenta = numeroCuenta;
        this.tipo = tipo;
    }

    public void depositar(BigDecimal monto) {
        validar(monto);
        saldo = saldo.add(monto);
    }

    public void retirar(BigDecimal monto) {
        validar(monto);
        if (saldo.compareTo(monto) < 0) {
            throw new IllegalStateException("Saldo insuficiente en la cuenta " + numeroCuenta);
        }
        saldo = saldo.subtract(monto);
    }

    private void validar(BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
        if (!"ACTIVA".equals(estado)) {
            throw new IllegalStateException("La cuenta " + numeroCuenta + " no está activa");
        }
    }

    public int getIdCuenta() { return idCuenta; }
    public void setIdCuenta(int idCuenta) { this.idCuenta = idCuenta; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
