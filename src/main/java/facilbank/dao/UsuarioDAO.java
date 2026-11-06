package facilbank.dao;

import facilbank.model.Usuario;
import java.util.Optional;

public interface UsuarioDAO {
    void insertar(Usuario usuario);
    Optional<Usuario> buscarPorUsername(String username);
    void registrarIntentoFallido(int idUsuario, int maxIntentos);
    void reiniciarIntentos(int idUsuario);
}
