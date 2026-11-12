package medicare.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import medicare.dto.Medicamento.MedicamentoRequest;
import medicare.dto.Medicamento.MedicamentoResponse;
import medicare.service.MedicamentoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoController {
    private final MedicamentoService service;
    public MedicamentoController(MedicamentoService service) {
        this.service = service;
    }
    @PostMapping
    public ResponseEntity<?> cadastrar(
            @Valid @RequestBody MedicamentoRequest request,
            HttpSession session
    ) {
        String usuarioId = obterUsuarioId(session);
        if (usuarioId == null) {
            return usuarioNaoAutenticado();
        }
        MedicamentoResponse response =
                service.cadastrar(request, usuarioId);
        return ResponseEntity
                .created(
                        URI.create(
                                "/api/medicamentos/" + response.id()
                        )
                )
                .body(response);
    }
    @GetMapping
    public ResponseEntity<?> listar(HttpSession session) {
        String usuarioId = obterUsuarioId(session);
        if (usuarioId == null) {
            return usuarioNaoAutenticado();
        }
        List<MedicamentoResponse> medicamentos =
                service.listarTodos(usuarioId);
        return ResponseEntity.ok(medicamentos);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(
            @PathVariable String id,
            HttpSession session
    ) {
        String usuarioId = obterUsuarioId(session);
        if (usuarioId == null) {
            return usuarioNaoAutenticado();
        }
        return ResponseEntity.ok(
                service.buscarPorId(id, usuarioId)
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable String id,
            @Valid @RequestBody MedicamentoRequest request,
            HttpSession session
    ) {
        String usuarioId = obterUsuarioId(session);
        if (usuarioId == null) {
            return usuarioNaoAutenticado();
        }
        return ResponseEntity.ok(
                service.atualizar(
                        id,
                        request,
                        usuarioId
                )
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(
            @PathVariable String id,
            HttpSession session
    ) {
        String usuarioId = obterUsuarioId(session);
        if (usuarioId == null) {
            return usuarioNaoAutenticado();
        }
        service.excluir(id, usuarioId);
        return ResponseEntity
                .noContent()
                .build();
    }
    private String obterUsuarioId(HttpSession session) {

        Object usuarioId =
                session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return null;
        }

        return usuarioId.toString();
    }
    private ResponseEntity<Map<String, String>>
    usuarioNaoAutenticado() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        Map.of(
                                "mensagem",
                                "Usuário não autenticado."
                        )
                );
    }
}