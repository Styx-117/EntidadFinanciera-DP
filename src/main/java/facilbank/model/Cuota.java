package facilbank.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Cuota {
    private final int numero;
    private final LocalDate vencimiento;
    private final BigDecimal cuota;
    private final BigDecimal interes;
    private final BigDecimal capital;
    private final BigDecimal saldo;

    public Cuota(int numero, LocalDate vencimiento, BigDecimal cuota, BigDecimal interes,
                 BigDecimal capital, BigDecimal saldo) {
        this.numero = numero;
        this.vencimiento = vencimiento;
        this.cuota = cuota;
        this.interes = interes;
        this.capital = capital;
        this.saldo = saldo;
    }

    public int getNumero() { return numero; }
    public LocalDate getVencimiento() { return vencimiento; }
    public BigDecimal getCuota() { return cuota; }
    public BigDecimal getInteres() { return interes; }
    public BigDecimal getCapital() { return capital; }
    public BigDecimal getSaldo() { return saldo; }
}
