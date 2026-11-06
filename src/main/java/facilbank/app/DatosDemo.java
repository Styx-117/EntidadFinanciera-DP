package facilbank.app;

import facilbank.dao.ClienteDAO;
import facilbank.dao.CreditoDAO;
import facilbank.dao.CuentaDAO;
import facilbank.dao.DAOException;
import facilbank.dao.MovimientoDAO;
import facilbank.dao.UsuarioDAO;
import facilbank.dao.factory.DAOFactory;
import facilbank.model.Cliente;
import facilbank.model.Credito;
import facilbank.model.Cuenta;
import facilbank.model.Movimiento;
import facilbank.model.Usuario;
import facilbank.service.AuthService;
import facilbank.service.CreditoService;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatosDemo implements CommandLineRunner {
    private final AuthService authService;
    private final UsuarioDAO usuarioDAO = DAOFactory.getUsuarioDAO();
    private final ClienteDAO clienteDAO = DAOFactory.getClienteDAO();
    private final CuentaDAO cuentaDAO = DAOFactory.getCuentaDAO();
    private final MovimientoDAO movimientoDAO = DAOFactory.getMovimientoDAO();
    private final CreditoDAO creditoDAO = DAOFactory.getCreditoDAO();

    public DatosDemo(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        try {
            asegurarCliente("cliente1", "Ana Torres Rojas", "12345678", "Ana", "Torres Rojas",
                    "ana@mail.com", "999888777",
                    new String[][]{{"001-0000001", "AHORRO", "1500.00"}, {"001-0000002", "CORRIENTE", "3200.00"}});
            Cliente luis = asegurarCliente("cliente2", "Luis Mendoza Paredes", "87654321", "Luis",
                    "Mendoza Paredes", "luis@mail.com", "988777666",
                    new String[][]{{"001-0000003", "AHORRO", "800.00"}});
            asegurarAsesor("asesor1", "Carlos Vega Salinas");
            asegurarSolicitud(luis, "5000.00", 12);
            System.out.println(">>> Usuarios de prueba (clave 1234): cliente1, cliente2 y asesor1");
        } catch (DAOException e) {
            System.err.println(">>> No se pudieron crear los datos de prueba. "
                    + "Verifique que ejecutó sql/schema.sql y que db.properties es correcto.");
            throw e;
        }
    }

    private Usuario crearUsuario(String username, String nombreCompleto, String rol) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setNombreCompleto(nombreCompleto);
        u.setPasswordHash(authService.encriptar("1234"));
        u.setRol(rol);
        usuarioDAO.insertar(u);
        return u;
    }

    private Cliente asegurarCliente(String username, String nombreCompleto, String dni, String nombres,
                                    String apellidos, String email, String telefono, String[][] cuentas) {
        Optional<Usuario> existente = usuarioDAO.buscarPorUsername(username);
        if (existente.isPresent()) {
            return clienteDAO.buscarPorUsuario(existente.get().getIdUsuario()).orElseThrow();
        }
        Usuario u = crearUsuario(username, nombreCompleto, "CLIENTE");
        Cliente c = new Cliente(u.getIdUsuario(), dni, nombres, apellidos, email, telefono);
        clienteDAO.insertar(c);
        for (String[] datos : cuentas) {
            Cuenta cuenta = new Cuenta(c.getIdCliente(), datos[0], datos[1]);
            cuenta.depositar(new BigDecimal(datos[2]));
            cuentaDAO.insertar(cuenta);
            movimientoDAO.insertar(new Movimiento(cuenta.getIdCuenta(), "DEPOSITO",
                    cuenta.getSaldo(), "Depósito inicial"));
        }
        return c;
    }

    private void asegurarAsesor(String username, String nombreCompleto) {
        if (usuarioDAO.buscarPorUsername(username).isEmpty()) {
            crearUsuario(username, nombreCompleto, "ASESOR");
        }
    }

    private void asegurarSolicitud(Cliente cliente, String monto, int plazo) {
        if (creditoDAO.listarPorCliente(cliente.getIdCliente()).isEmpty()) {
            Credito credito = new Credito();
            credito.setIdCliente(cliente.getIdCliente());
            credito.setMonto(new BigDecimal(monto));
            credito.setPlazoMeses(plazo);
            credito.setTasaAnual(CreditoService.TASA_REFERENCIAL);
            creditoDAO.insertar(credito);
        }
    }
}
