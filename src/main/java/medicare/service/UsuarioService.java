package medicare.service;

import medicare.dto.Usuario.CadastroRequest;
import medicare.model.Usuario;
import medicare.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    private final org.springframework.security.crypto.password.PasswordEncoder encoder =
            org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario cadastrar(CadastroRequest request) {
        String email = request.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Este email já está cadastrado.");
        }
        // Segunda entrega: novas senhas são armazenadas somente como hash PBKDF2 com salt aleatório.
        Usuario usuario = new Usuario(request.getNome().trim(), email, "{pbkdf2}" + encoder.encode(request.getSenha()));
        return repository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        if (email == null) throw new IllegalArgumentException("Email inválido.");
        return repository.findByEmail(email.trim().toLowerCase(java.util.Locale.ROOT))
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

    public boolean senhaValida(String senha, Usuario usuario) {
        if (usuario == null || senha == null || usuario.getSenha() == null) return false;
        String armazenada = usuario.getSenha();
        if (armazenada.startsWith("{pbkdf2}")) return encoder.matches(senha, armazenada.substring(8));
        // Compatibilidade com a primeira entrega: migra após login válido.
        if (!java.security.MessageDigest.isEqual(
                senha.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                armazenada.getBytes(java.nio.charset.StandardCharsets.UTF_8))) return false;
        usuario.setSenha("{pbkdf2}" + encoder.encode(senha));
        repository.save(usuario);
        return true;
    }
}
