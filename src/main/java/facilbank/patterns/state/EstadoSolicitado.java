package facilbank.patterns.state;

public class EstadoSolicitado implements EstadoCredito {
    @Override
    public String nombre() { return "SOLICITADO"; }

    @Override
    public EstadoCredito evaluar() { return new EstadoEnEvaluacion(); }

    @Override
    public EstadoCredito aprobar() {
        throw new IllegalStateException("La solicitud debe pasar primero a evaluación");
    }

    @Override
    public EstadoCredito rechazar() {
        throw new IllegalStateException("La solicitud debe pasar primero a evaluación");
    }
}
