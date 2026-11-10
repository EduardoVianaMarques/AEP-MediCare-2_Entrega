package medicare.model;

import java.time.Instant;

/** Registro informativo de uso, sem recomendar ou calcular doses. */
public record RegistroUso(String id, Instant registradoEm) { }
