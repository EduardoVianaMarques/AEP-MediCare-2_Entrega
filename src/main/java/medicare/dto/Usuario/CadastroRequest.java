package medicare.dto.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CadastroRequest {

    @NotBlank(message = "O nome é obrigatório")
    @Size(
            min = 3,
            max = 100,
            message = "O nome deve possuir entre 3 e 100 caracteres"
    )
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Informe um email válido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(
            min = 6,
            max = 100,
            message = "A senha deve possuir pelo menos 6 caracteres"
    )
    private String senha;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}