package facilbank.controller;

import facilbank.model.Credito;
import facilbank.patterns.facade.EvaluacionCreditoFacade;
import facilbank.service.AsesorService;
import facilbank.service.NegocioException;
import facilbank.util.Sesion;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/asesor")
public class AsesorController {
    private final AsesorService asesorService;
    private final EvaluacionCreditoFacade evaluacion;

    public AsesorController(AsesorService asesorService, EvaluacionCreditoFacade evaluacion) {
        this.asesorService = asesorService;
        this.evaluacion = evaluacion;
    }

    private int idAsesor(HttpSession session) {
        return (Integer) session.getAttribute(Sesion.ID_USUARIO);
    }

    @GetMapping("/solicitudes")
    public String solicitudes(Model model) {
        model.addAttribute("solicitudes", asesorService.listarPendientes());
        return "asesor_solicitudes";
    }

    @GetMapping("/resueltas")
    public String resueltas(Model model) {
        model.addAttribute("solicitudes", asesorService.listarResueltas());
        return "asesor_resueltas";
    }

    @GetMapping("/cliente/{idCliente}")
    public String historial(@PathVariable int idCliente, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("historial", asesorService.historial(idCliente));
            return "asesor_cliente";
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/asesor/solicitudes";
        }
    }

    @GetMapping("/solicitud/{idCredito}")
    public String detalle(@PathVariable int idCredito, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("credito", asesorService.obtenerCredito(idCredito));
            return "asesor_evaluacion";
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/asesor/solicitudes";
        }
    }

    @PostMapping("/solicitud/{idCredito}/evaluar")
    public String iniciarEvaluacion(@PathVariable int idCredito,
                                    @RequestParam(required = false) String observaciones,
                                    HttpSession session, RedirectAttributes ra) {
        try {
            evaluacion.iniciarEvaluacion(idCredito, idAsesor(session), observaciones);
            ra.addFlashAttribute("ok", "La solicitud pasó a evaluación y el cliente fue avisado.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/asesor/solicitud/" + idCredito;
    }

    @PostMapping("/solicitud/{idCredito}/observaciones")
    public String guardarObservaciones(@PathVariable int idCredito, @RequestParam String observaciones,
                                       HttpSession session, RedirectAttributes ra) {
        try {
            evaluacion.registrarObservaciones(idCredito, idAsesor(session), observaciones);
            ra.addFlashAttribute("ok", "Observaciones guardadas.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/asesor/solicitud/" + idCredito;
    }

    @PostMapping("/solicitud/{idCredito}/aprobar")
    public String aprobar(@PathVariable int idCredito,
                          @RequestParam(required = false) String observaciones,
                          HttpSession session, RedirectAttributes ra) {
        try {
            evaluacion.aprobar(idCredito, idAsesor(session), observaciones);
            ra.addFlashAttribute("ok", "Crédito aprobado. El cliente fue notificado.");
            return "redirect:/asesor/resueltas";
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/asesor/solicitud/" + idCredito;
        }
    }

    @PostMapping("/solicitud/{idCredito}/rechazar")
    public String rechazar(@PathVariable int idCredito,
                           @RequestParam(required = false) String observaciones,
                           HttpSession session, RedirectAttributes ra) {
        try {
            evaluacion.rechazar(idCredito, idAsesor(session), observaciones);
            ra.addFlashAttribute("ok", "Solicitud rechazada. El cliente fue notificado.");
            return "redirect:/asesor/resueltas";
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/asesor/solicitud/" + idCredito;
        }
    }

    @GetMapping("/credito/{idCredito}/cronograma")
    public String cronograma(@PathVariable int idCredito, Model model, RedirectAttributes ra) {
        try {
            Credito credito = asesorService.obtenerCredito(idCredito);
            model.addAttribute("credito", credito);
            model.addAttribute("cuotas", asesorService.cronograma(credito));
            return "asesor_cronograma";
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/asesor/resueltas";
        }
    }
}
