package facilbank.controller;

import facilbank.service.AuthService;
import facilbank.service.NegocioException;
import facilbank.service.UsuarioAutenticado;
import facilbank.util.Sesion;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        Object rol = session.getAttribute(Sesion.ROL);
        if (rol != null) {
            return destino(rol.toString());
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        HttpSession session, Model model) {
        try {
            UsuarioAutenticado u = authService.iniciarSesion(username, password);
            session.setAttribute(Sesion.ID_USUARIO, u.getIdUsuario());
            session.setAttribute(Sesion.ROL, u.getRol());
            session.setAttribute(Sesion.USUARIO, u.getNombre());
            if (u.getIdCliente() != null) {
                session.setAttribute(Sesion.ID_CLIENTE, u.getIdCliente());
            }
            return destino(u.getRol());
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

    private String destino(String rol) {
        return "ASESOR".equals(rol) ? "redirect:/asesor/solicitudes" : "redirect:/cliente/dashboard";
    }
}
