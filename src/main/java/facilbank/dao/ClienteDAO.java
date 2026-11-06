package facilbank.dao;

import facilbank.model.Cliente;
import java.util.Optional;

public interface ClienteDAO {
    void insertar(Cliente cliente);
    Optional<Cliente> buscarPorUsuario(int idUsuario);
    Optional<Cliente> buscarPorId(int idCliente);
}
