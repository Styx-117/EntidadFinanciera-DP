package banca.service;
import banca.dao.ClienteDAO;
import banca.dao.UsuarioDAO;
import banca.dao.factory.DAOFactory;
import banca.model.Cliente;
import banca.model.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class AuthService {
    public static final int MAX_INTENTOS = 3;
    private static final String MSG_CREDENCIALES = "Usuario o contraseña incorrectos";
    private final UsuarioDAO usuarioDAO = DAOFactory.getUsuarioDAO();
    private final ClienteDAO clienteDAO = DAOFactory.getClienteDAO();
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public String encriptar(String clave) {
        return encoder.encode(clave);
    }
    public Cliente iniciarSesion(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            throw new NegocioException("Ingrese usuario y contraseña");
        }
        Usuario u = usuarioDAO.buscarPorUsername(username.trim())
                .orElseThrow(() -> new NegocioException(MSG_CREDENCIALES));

        if (u.isBloqueado() || !u.isActivo()) {
            throw new NegocioException("Usuario bloqueado tras " + MAX_INTENTOS
                    + " intentos fallidos. Comuníquese con el banco.");
        }
        if (!encoder.matches(password, u.getPasswordHash())) {
            usuarioDAO.registrarIntentoFallido(u.getIdUsuario(), MAX_INTENTOS);
            throw new NegocioException(MSG_CREDENCIALES);
        }
        if (!"CLIENTE".equals(u.getRol())) {
            throw new NegocioException("Este portal es solo para clientes");
        }
        usuarioDAO.reiniciarIntentos(u.getIdUsuario());
        return clienteDAO.buscarPorUsuario(u.getIdUsuario())
                .orElseThrow(() -> new NegocioException("El usuario no tiene un cliente asociado"));
    }
}
