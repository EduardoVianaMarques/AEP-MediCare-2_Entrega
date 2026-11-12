package medicare.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import medicare.dto.Medicamento.*;
import medicare.service.EstoqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/medicamentos/{id}/estoque")
public class EstoqueController {
    private final EstoqueService service;
    public EstoqueController(EstoqueService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<?> iniciar(@PathVariable String id, @Valid @RequestBody EstoqueRequest request,
                                      HttpSession session) {
        String usuario = (String) session.getAttribute("usuarioId");
        if (usuario == null) return naoAutenticado();
        return ResponseEntity.status(201).body(service.iniciar(id, usuario, request));
    }
    @PostMapping("/movimentos")
    public ResponseEntity<?> movimentar(@PathVariable String id, @Valid @RequestBody MovimentoRequest request,
                                         HttpSession session) {
        String usuario = (String) session.getAttribute("usuarioId");
        if (usuario == null) return naoAutenticado();
        return ResponseEntity.ok(service.movimentar(id, usuario, request));
    }
    private ResponseEntity<?> naoAutenticado() {
        return ResponseEntity.status(401).body(Map.of("mensagem", "Usuário não autenticado."));
    }
}
