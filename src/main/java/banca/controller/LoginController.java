package banca.controller;

import banca.model.Cliente;
import banca.service.AuthService;
import banca.service.NegocioException;
import banca.util.Sesion;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** GRASP CONTROLADOR: recibe las peticiones de login/logout y delega en AuthService. */
@Controller
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {
        if (session.getAttribute(Sesion.ID_CLIENTE) != null) {
            return "redirect:/cliente/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        HttpSession session, Model model) {
        try {
            Cliente cliente = authService.iniciarSesion(username, password);
            session.setAttribute(Sesion.ID_CLIENTE, cliente.getIdCliente());
            session.setAttribute(Sesion.USUARIO, cliente.getNombreCompleto());
            return "redirect:/cliente/dashboard";
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
