package banca.app;

import banca.dao.ClienteDAO;
import banca.dao.CuentaDAO;
import banca.dao.DAOException;
import banca.dao.MovimientoDAO;
import banca.dao.UsuarioDAO;
import banca.dao.factory.DAOFactory;
import banca.model.Cliente;
import banca.model.Cuenta;
import banca.model.Movimiento;
import banca.model.Usuario;
import banca.service.AuthService;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Crea datos de prueba al iniciar (solo si no existen):
 *   cliente1 / 1234  -> cuentas 001-0000001 (ahorro) y 001-0000002 (corriente)
 *   cliente2 / 1234  -> cuenta  001-0000003 (ahorro)   (sirve como destino de transferencias)
 */
@Component
public class DatosDemo implements CommandLineRunner {

    private final AuthService authService;
    private final UsuarioDAO usuarioDAO = DAOFactory.getUsuarioDAO();
    private final ClienteDAO clienteDAO = DAOFactory.getClienteDAO();
    private final CuentaDAO cuentaDAO = DAOFactory.getCuentaDAO();
    private final MovimientoDAO movimientoDAO = DAOFactory.getMovimientoDAO();

    public DatosDemo(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        try {
            if (usuarioDAO.buscarPorUsername("cliente1").isPresent()) return;

            Cliente ana = crearCliente("cliente1", "12345678", "Ana", "Torres Rojas", "ana@mail.com", "999888777");
            crearCuenta(ana, "001-0000001", "AHORRO", "1500.00");
            crearCuenta(ana, "001-0000002", "CORRIENTE", "3200.00");

            Cliente luis = crearCliente("cliente2", "87654321", "Luis", "Mendoza Paredes", "luis@mail.com", "988777666");
            crearCuenta(luis, "001-0000003", "AHORRO", "800.00");

            System.out.println(">>> Datos de prueba creados: cliente1 / 1234  y  cliente2 / 1234");
        } catch (DAOException e) {
            System.err.println(">>> No se pudieron crear los datos de prueba. "
                    + "Verifique que ejecutó sql/schema.sql y que db.properties es correcto.");
            throw e;
        }
    }

    private Cliente crearCliente(String username, String dni, String nombres, String apellidos,
                                 String email, String telefono) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPasswordHash(authService.encriptar("1234"));
        u.setRol("CLIENTE");
        usuarioDAO.insertar(u);

        Cliente c = new Cliente(u.getIdUsuario(), dni, nombres, apellidos, email, telefono);
        clienteDAO.insertar(c);
        return c;
    }

    private void crearCuenta(Cliente c, String numero, String tipo, String saldoInicial) {
        Cuenta cuenta = new Cuenta(c.getIdCliente(), numero, tipo);
        cuenta.depositar(new BigDecimal(saldoInicial));
        cuentaDAO.insertar(cuenta);
        movimientoDAO.insertar(new Movimiento(cuenta.getIdCuenta(), "DEPOSITO",
                cuenta.getSaldo(), "Depósito inicial"));
    }
}
