package medicare.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import medicare.dto.Usuario.CadastroRequest;
import medicare.dto.Usuario.LoginRequest;
import medicare.model.Usuario;
import medicare.service.UsuarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(
            UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(
            @Valid @RequestBody CadastroRequest request
    ) {

        try {

            usuarioService.cadastrar(request);

            return ResponseEntity.ok(
                    Map.of(
                            "mensagem",
                            "Usuário cadastrado com sucesso."
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(Map.of("mensagem", e.getMessage()) );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            jakarta.servlet.http.HttpServletRequest httpRequest,
            HttpSession session
    ) {

        try {

            Usuario usuario =
                    usuarioService.buscarPorEmail(
                            request.getEmail()
                    );

            if (!usuarioService.senhaValida(
                    request.getSenha(),
                    usuario
            )) {

                return ResponseEntity.status(401).body(Map.of("mensagem", "Email ou senha inválidos.") );
            }
            // Renova o identificador da sessão após autenticar.
            httpRequest.changeSessionId();
            session.setAttribute(
                    "usuarioId",
                    usuario.getId()
            );
            session.setAttribute(
                    "usuarioNome",
                    usuario.getNome()
            );
            session.setAttribute(
                    "usuarioEmail",
                    usuario.getEmail()
            );
            return ResponseEntity.ok(
                    Map.of(
                            "mensagem",
                            "Login realizado com sucesso.",
                            "nome",
                            usuario.getNome()
                    )
            );
        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(401).body(Map.of("mensagem","Email ou senha inválidos.") );
        }
    }
    @GetMapping("/usuario")
    public ResponseEntity<?> usuario(
            HttpSession session
    ) {
        Object id =
                session.getAttribute("usuarioId");
        if (id == null) {
            return ResponseEntity
                    .status(401)
                    .body(
                            Map.of(
                                    "mensagem",
                                    "Usuário não autenticado."
                            )
                    );
        }
        return ResponseEntity.ok(
                Map.of(
                        "id",
                        id,
                        "nome",
                        session.getAttribute("usuarioNome"),
                        "email",
                        session.getAttribute("usuarioEmail")
                )
        );
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpSession session
    ) {

        session.invalidate();

        return ResponseEntity
                .noContent()
                .build();
    }
}