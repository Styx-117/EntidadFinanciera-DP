package facilbank.service;

import facilbank.dao.CuentaDAO;
import facilbank.dao.MovimientoDAO;
import facilbank.dao.factory.DAOFactory;
import facilbank.model.Cuenta;
import facilbank.model.Movimiento;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CuentaService {
    private final CuentaDAO cuentaDAO = DAOFactory.getCuentaDAO();
    private final MovimientoDAO movimientoDAO = DAOFactory.getMovimientoDAO();

    public List<Cuenta> listarCuentas(int idCliente) {
        return cuentaDAO.listarPorCliente(idCliente);
    }

    public BigDecimal sumarSaldos(List<Cuenta> cuentas) {
        return cuentas.stream().map(Cuenta::getSaldo).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Cuenta obtenerCuentaPropia(int idCliente, String numeroCuenta) {
        Cuenta c = cuentaDAO.buscarPorNumero(numeroCuenta)
                .orElseThrow(() -> new NegocioException("La cuenta no existe"));
        if (c.getIdCliente() != idCliente) {
            throw new NegocioException("La cuenta no le pertenece");
        }
        return c;
    }

    public List<Movimiento> listarMovimientos(int idCliente, String numeroCuenta) {
        Cuenta c = obtenerCuentaPropia(idCliente, numeroCuenta);
        return movimientoDAO.listarPorCuenta(c.getIdCuenta());
    }
}
