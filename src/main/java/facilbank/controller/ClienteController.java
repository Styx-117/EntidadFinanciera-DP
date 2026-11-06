package facilbank.controller;

import facilbank.model.Cuenta;
import facilbank.service.CreditoService;
import facilbank.service.CuentaService;
import facilbank.service.NegocioException;
import facilbank.service.NotificacionService;
import facilbank.util.Sesion;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliente")
public class ClienteController {
    private final CuentaService cuentaService;
    private final CreditoService creditoService;
    private final NotificacionService notificacionService;

    public ClienteController(CuentaService cuentaService, CreditoService creditoService,
                             NotificacionService notificacionService) {
        this.cuentaService = cuentaService;
        this.creditoService = creditoService;
        this.notificacionService = notificacionService;
    }

    private int idCliente(HttpSession session) {
        return (Integer) session.getAttribute(Sesion.ID_CLIENTE);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        int id = idCliente(session);
        List<Cuenta> cuentas = cuentaService.listarCuentas(id);
        model.addAttribute("cuentas", cuentas);
        model.addAttribute("saldoTotal", cuentaService.sumarSaldos(cuentas));
        model.addAttribute("notificaciones", notificacionService.listarNoLeidas(id));
        return "dashboard";
    }

    @PostMapping("/notificaciones/leer")
    public String leerNotificaciones(HttpSession session) {
        notificacionService.marcarLeidas(idCliente(session));
        return "redirect:/cliente/dashboard";
    }

    @GetMapping("/movimientos")
    public String movimientos(@RequestParam(required = false) String cuenta,
                              HttpSession session, Model model) {
        int id = idCliente(session);
        List<Cuenta> cuentas = cuentaService.listarCuentas(id);
        model.addAttribute("cuentas", cuentas);

        String seleccionada = cuenta;
        if ((seleccionada == null || seleccionada.isBlank()) && !cuentas.isEmpty()) {
            seleccionada = cuentas.get(0).getNumeroCuenta();
        }
        if (seleccionada != null) {
            try {
                model.addAttribute("movimientos", cuentaService.listarMovimientos(id, seleccionada));
                model.addAttribute("seleccionada", seleccionada);
            } catch (NegocioException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        return "movimientos";
    }

    @GetMapping("/credito")
    public String credito(HttpSession session, Model model) {
        model.addAttribute("creditos", creditoService.listar(idCliente(session)));
        model.addAttribute("plazos", CreditoService.PLAZOS);
        model.addAttribute("tasa", CreditoService.TASA_REFERENCIAL);
        return "credito";
    }

    @PostMapping("/credito")
    public String solicitarCredito(@RequestParam BigDecimal monto, @RequestParam int plazo,
                                   HttpSession session, RedirectAttributes ra) {
        try {
            creditoService.solicitar(idCliente(session), monto, plazo);
            ra.addFlashAttribute("ok", "Solicitud enviada. Un asesor la revisará y te avisaremos aquí.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cliente/credito";
    }
}
