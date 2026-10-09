package banca.controller;

import banca.service.CuentaService;
import banca.service.NegocioException;
import banca.service.OperacionService;
import banca.util.Sesion;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** GRASP CONTROLADOR: recibe las peticiones de transferencia y pago de servicios. */
@Controller
@RequestMapping("/cliente")
public class OperacionController {

    private final OperacionService operacionService;
    private final CuentaService cuentaService;

    public OperacionController(OperacionService operacionService, CuentaService cuentaService) {
        this.operacionService = operacionService;
        this.cuentaService = cuentaService;
    }

    private int idCliente(HttpSession session) {
        return (Integer) session.getAttribute(Sesion.ID_CLIENTE);
    }

    @GetMapping("/transferencia")
    public String transferencia(HttpSession session, Model model) {
        model.addAttribute("cuentas", cuentaService.listarCuentas(idCliente(session)));
        return "transferencia";
    }

    @PostMapping("/transferencia")
    public String transferir(@RequestParam String origen, @RequestParam String destino,
                             @RequestParam BigDecimal monto,
                             HttpSession session, RedirectAttributes ra) {
        try {
            operacionService.transferir(idCliente(session), origen, destino, monto);
            ra.addFlashAttribute("ok", "Transferencia realizada correctamente.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cliente/transferencia";
    }

    @GetMapping("/pagos")
    public String pagos(HttpSession session, Model model) {
        model.addAttribute("cuentas", cuentaService.listarCuentas(idCliente(session)));
        model.addAttribute("servicios", OperacionService.SERVICIOS);
        return "pago_servicios";
    }

    @PostMapping("/pagos")
    public String pagar(@RequestParam String origen, @RequestParam String servicio,
                        @RequestParam String codigo, @RequestParam BigDecimal monto,
                        HttpSession session, RedirectAttributes ra) {
        try {
            operacionService.pagarServicio(idCliente(session), origen, servicio, codigo, monto);
            ra.addFlashAttribute("ok", "Pago de " + servicio + " realizado correctamente.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cliente/pagos";
    }
}
