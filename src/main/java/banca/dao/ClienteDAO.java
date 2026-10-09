package banca.dao;

import banca.model.Cliente;
import java.util.Optional;

public interface ClienteDAO {
    void insertar(Cliente cliente);
    Optional<Cliente> buscarPorUsuario(int idUsuario);
}
