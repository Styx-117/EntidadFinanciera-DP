package banca.service;

import banca.dao.CuentaDAO;
import banca.dao.MovimientoDAO;
import banca.dao.factory.DAOFactory;
import banca.model.Cuenta;
import banca.model.Movimiento;
import java.util.List;
import org.springframework.stereotype.Service;

/** Funcionalidades 2 y 3: consultar saldos y movimientos. */
@Service
public class CuentaService {

    private final CuentaDAO cuentaDAO = DAOFactory.getCuentaDAO();
    private final MovimientoDAO movimientoDAO = DAOFactory.getMovimientoDAO();

    public List<Cuenta> listarCuentas(int idCliente) {
        return cuentaDAO.listarPorCliente(idCliente);
    }

    /** Verifica que la cuenta pertenezca al cliente antes de mostrar sus movimientos. */
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
