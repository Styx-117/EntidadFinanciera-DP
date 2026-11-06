package facilbank.service;

import facilbank.dao.ClienteDAO;
import facilbank.dao.CreditoDAO;
import facilbank.dao.CuentaDAO;
import facilbank.dao.MovimientoDAO;
import facilbank.dao.factory.DAOFactory;
import facilbank.model.Cliente;
import facilbank.model.Credito;
import facilbank.model.Cuenta;
import facilbank.model.Cuota;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AsesorService {
    private final CreditoDAO creditoDAO = DAOFactory.getCreditoDAO();
    private final ClienteDAO clienteDAO = DAOFactory.getClienteDAO();
    private final CuentaDAO cuentaDAO = DAOFactory.getCuentaDAO();
    private final MovimientoDAO movimientoDAO = DAOFactory.getMovimientoDAO();

    public List<Credito> listarPendientes() {
        return creditoDAO.listarPendientes();
    }

    public List<Credito> listarResueltas() {
        return creditoDAO.listarResueltas();
    }

    public Credito obtenerCredito(int idCredito) {
        return creditoDAO.buscarPorId(idCredito)
                .orElseThrow(() -> new NegocioException("La solicitud no existe"));
    }

    public HistorialCliente historial(int idCliente) {
        Cliente cliente = clienteDAO.buscarPorId(idCliente)
                .orElseThrow(() -> new NegocioException("El cliente no existe"));
        List<Cuenta> cuentas = cuentaDAO.listarPorCliente(idCliente);
        BigDecimal total = cuentas.stream().map(Cuenta::getSaldo).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new HistorialCliente(cliente, cuentas, total,
                movimientoDAO.listarRecientesPorCliente(idCliente, 10),
                creditoDAO.listarPorCliente(idCliente));
    }

    public List<Cuota> cronograma(Credito credito) {
        if (!"APROBADO".equals(credito.getEstado())) {
            throw new NegocioException("Solo los créditos aprobados tienen cronograma de pagos");
        }
        return credito.generarCronograma();
    }
}
