# API HTTP

[Voltar ao índice](README.md)

As requisições com corpo usam `Content-Type: application/json`. As rotas de medicamentos exigem a sessão criada no login. O contrato OpenAPI é servido em `/v3/api-docs` com a aplicação em execução.

## Autenticação

| Método | Rota | Dados de entrada | Sucesso |
| --- | --- | --- | --- |
| POST | `/api/auth/cadastro` | `nome`, `email`, `senha` | 200 |
| POST | `/api/auth/login` | `email`, `senha` | 200 e sessão autenticada |
| GET | `/api/auth/usuario` | Cookie da sessão | 200 com ID, nome e e-mail |
| POST | `/api/auth/logout` | Cookie da sessão | 204 e sessão invalidada |

## Medicamentos, histórico e estoque

| Método | Rota | Operação | Sucesso |
| --- | --- | --- | --- |
| GET | `/api/medicamentos` | Lista medicamentos da conta | 200 |
| POST | `/api/medicamentos` | Cadastra medicamento | 201 |
| GET | `/api/medicamentos/{id}` | Consulta um medicamento | 200 |
| PUT | `/api/medicamentos/{id}` | Atualiza os dados de cadastro | 200 |
| DELETE | `/api/medicamentos/{id}` | Exclui o medicamento | 204 |
| GET | `/api/medicamentos/{id}/historico` | Lista registros de uso | 200 |
| POST | `/api/medicamentos/{id}/historico` | Registra o uso no instante atual | 201 |
| POST | `/api/medicamentos/{id}/estoque` | Inicializa saldo e mínimo | 201 |
| POST | `/api/medicamentos/{id}/estoque/movimentos` | Aplica entrada ou saída ao saldo | 200 |

Corpo de exemplo para cadastrar ou atualizar um medicamento com dados fictícios:

```json
{
  "nome": "Medicamento de exemplo",
  "dosagem": "Conforme prescrição",
  "horario": "08:00",
  "frequencia": "Diária",
  "paciente": "Pessoa de teste",
  "observacao": "Registro fictício para demonstrar a API"
}
```

Para inicializar o estoque, envie `{"quantidade":10,"minimo":5}`. Para retirar duas unidades, envie `{"quantidade":-2}` à rota de movimentos. A quantidade deve ser inteira. O registro de uso usa um POST sem corpo.

## Respostas de erro

| Status | Situação |
| --- | --- |
| 400 | Corpo inválido, horário incorreto, e-mail duplicado no cadastro ou quantidade inválida. |
| 401 | Credenciais inválidas ou sessão ausente em rota protegida. |
| 404 | Medicamento inexistente ou pertencente a outra conta. |
| 409 | Estoque já iniciado na inicialização ou movimentação inviável por saldo, limite ou falta de estoque ativo. |
| 500 | Falha inesperada tratada pelo servidor. |

As respostas de erro apresentam uma mensagem. O tratamento centralizado também pode fornecer status, instante, caminho e detalhes de validação por campo.
