package medicare.controller;

import jakarta.servlet.http.HttpSession;
import medicare.service.HistoricoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/medicamentos/{id}/historico")
public class HistoricoController {
    private final HistoricoService service;
    public HistoricoController(HistoricoService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<?> listar(@PathVariable String id, HttpSession session) {
        String usuarioId = (String) session.getAttribute("usuarioId");
        if (usuarioId == null) return naoAutenticado();
        return ResponseEntity.ok(service.listar(id, usuarioId));
    }

    @PostMapping
    public ResponseEntity<?> registrar(@PathVariable String id, HttpSession session) {
        String usuarioId = (String) session.getAttribute("usuarioId");
        if (usuarioId == null) return naoAutenticado();
        return ResponseEntity.status(201).body(service.registrar(id, usuarioId));
    }

    private ResponseEntity<?> naoAutenticado() {
        return ResponseEntity.status(401).body(Map.of("mensagem", "Usuário não autenticado."));
    }
}
