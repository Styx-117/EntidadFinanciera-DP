package facilbank.dao;

import facilbank.model.Credito;
import java.util.List;
import java.util.Optional;

public interface CreditoDAO {
    void insertar(Credito credito);
    Optional<Credito> buscarPorId(int idCredito);
    List<Credito> listarPorCliente(int idCliente);
    List<Credito> listarPendientes();
    List<Credito> listarResueltas();
    void actualizarEvaluacion(Credito credito);
}
