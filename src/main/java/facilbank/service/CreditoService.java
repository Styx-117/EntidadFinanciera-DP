package facilbank.service;

import facilbank.dao.CreditoDAO;
import facilbank.dao.factory.DAOFactory;
import facilbank.model.Credito;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CreditoService {
    public static final BigDecimal TASA_REFERENCIAL = new BigDecimal("18.00");
    public static final BigDecimal MONTO_MIN = new BigDecimal("500");
    public static final BigDecimal MONTO_MAX = new BigDecimal("100000");
    public static final List<Integer> PLAZOS = List.of(6, 12, 24, 36, 48, 60);

    private final CreditoDAO creditoDAO = DAOFactory.getCreditoDAO();

    public Credito solicitar(int idCliente, BigDecimal monto, int plazoMeses) {
        if (monto == null || monto.compareTo(MONTO_MIN) < 0 || monto.compareTo(MONTO_MAX) > 0) {
            throw new NegocioException("El monto debe estar entre S/ " + MONTO_MIN + " y S/ " + MONTO_MAX);
        }
        if (!PLAZOS.contains(plazoMeses)) {
            throw new NegocioException("Seleccione un plazo válido");
        }

        for (Credito existente : creditoDAO.listarPorCliente(idCliente)) {
            String e = existente.getEstado();
            if ("SOLICITADO".equals(e) || "EN_EVALUACION".equals(e)) {
                throw new NegocioException("Ya tiene una solicitud de crédito en trámite");
            }
        }
        Credito c = new Credito();
        c.setIdCliente(idCliente);
        c.setMonto(monto);
        c.setPlazoMeses(plazoMeses);
        c.setTasaAnual(TASA_REFERENCIAL);
        creditoDAO.insertar(c);
        return c;
    }

    public List<Credito> listar(int idCliente) {
        return creditoDAO.listarPorCliente(idCliente);
    }
}
