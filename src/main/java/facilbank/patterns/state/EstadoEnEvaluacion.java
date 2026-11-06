package facilbank.patterns.state;

public class EstadoEnEvaluacion implements EstadoCredito {
    @Override
    public String nombre() { return "EN_EVALUACION"; }

    @Override
    public EstadoCredito evaluar() {
        throw new IllegalStateException("La solicitud ya está en evaluación");
    }

    @Override
    public EstadoCredito aprobar() { return new EstadoAprobado(); }

    @Override
    public EstadoCredito rechazar() { return new EstadoRechazado(); }
}
