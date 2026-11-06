package facilbank.service;

import facilbank.model.Cliente;
import facilbank.model.Credito;
import facilbank.model.Cuenta;
import facilbank.model.Movimiento;
import java.math.BigDecimal;
import java.util.List;

public class HistorialCliente {
    private final Cliente cliente;
    private final List<Cuenta> cuentas;
    private final BigDecimal saldoTotal;
    private final List<Movimiento> movimientos;
    private final List<Credito> creditos;

    public HistorialCliente(Cliente cliente, List<Cuenta> cuentas, BigDecimal saldoTotal,
                            List<Movimiento> movimientos, List<Credito> creditos) {
        this.cliente = cliente;
        this.cuentas = cuentas;
        this.saldoTotal = saldoTotal;
        this.movimientos = movimientos;
        this.creditos = creditos;
    }

    public Cliente getCliente() { return cliente; }
    public List<Cuenta> getCuentas() { return cuentas; }
    public BigDecimal getSaldoTotal() { return saldoTotal; }
    public List<Movimiento> getMovimientos() { return movimientos; }
    public List<Credito> getCreditos() { return creditos; }
}
