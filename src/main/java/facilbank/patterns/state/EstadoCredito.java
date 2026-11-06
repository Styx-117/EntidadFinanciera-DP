package facilbank.patterns.state;

public interface EstadoCredito {
    String nombre();
    EstadoCredito evaluar();
    EstadoCredito aprobar();
    EstadoCredito rechazar();

    static EstadoCredito desde(String nombre) {
        switch (nombre) {
            case "SOLICITADO": return new EstadoSolicitado();
            case "EN_EVALUACION": return new EstadoEnEvaluacion();
            case "APROBADO": return new EstadoAprobado();
            case "RECHAZADO": return new EstadoRechazado();
            default: throw new IllegalArgumentException("Estado de crédito desconocido: " + nombre);
        }
    }
}
