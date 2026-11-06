package facilbank.service;

import facilbank.dao.ClienteDAO;
import facilbank.dao.UsuarioDAO;
import facilbank.dao.factory.DAOFactory;
import facilbank.model.Cliente;
import facilbank.model.Usuario;
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

    public UsuarioAutenticado iniciarSesion(String username, String password) {
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
        usuarioDAO.reiniciarIntentos(u.getIdUsuario());

        if ("CLIENTE".equals(u.getRol())) {
            Cliente cliente = clienteDAO.buscarPorUsuario(u.getIdUsuario())
                    .orElseThrow(() -> new NegocioException("El usuario no tiene un cliente asociado"));
            String nombre = u.getNombreCompleto() != null && !u.getNombreCompleto().isBlank()
                    ? u.getNombreCompleto() : cliente.getNombreCompleto();
            return new UsuarioAutenticado(u.getIdUsuario(), u.getRol(), nombre, cliente.getIdCliente());
        }
        if ("ASESOR".equals(u.getRol())) {
            String nombre = u.getNombreCompleto() != null && !u.getNombreCompleto().isBlank()
                    ? u.getNombreCompleto() : u.getUsername();
            return new UsuarioAutenticado(u.getIdUsuario(), u.getRol(), nombre, null);
        }
        throw new NegocioException("Este portal es solo para clientes y asesores de crédito");
    }
}
