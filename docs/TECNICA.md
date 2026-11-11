# Documentação técnica

## Arquitetura e orientação a objetos

Frontend HTML/CSS/JS servido pelo Spring Boot → controllers REST → serviços → repositories/MongoTemplate → MongoDB.
Controllers tratam HTTP; DTOs delimitam entrada e saída; services concentram regras; mapper converte medicamentos;
models representam documentos. Injeção por construtor permite testes com dependências simuladas.
`HistoricoService` concentra as consultas de histórico, sempre com medicamento + proprietário.
`RegistroUso` é um objeto imutável. O modelo devolve uma cópia da lista para proteger o estado interno.

## Estrutura

- `src/main/java/medicare/controller`: autenticação, páginas, CRUD e histórico.
- `service`: regras de usuário, medicamento e histórico.
- `model`, `dto`, `mapper`: documentos e contratos.
- `repository`: persistência Spring Data.
- `exception`: erros JSON padronizados.
- `src/main/resources/static`: interface.
- `src/test`: testes automatizados.
- `scripts/mongo-init.js`: índices.
- `.github/workflows/ci.yml`: integração contínua.

## Modelagem NoSQL

Duas coleções: `usuarios` e `medicamentos`. O campo `medicamentos.usuarioId` referencia `usuarios._id`.
MongoDB não cria chave estrangeira: o servidor obtém o proprietário da sessão, nunca do JSON enviado pelo cliente.
Não há exclusão de contas nesta PoC. Excluir medicamento também exclui seu histórico incorporado.

Exemplo conceitual (datas BSON representadas em Extended JSON):

```json
{
  "_id": "medicamento-id",
  "usuarioId": "usuario-id",
  "nome": "Medicamento demonstrativo",
  "dosagem": "Conforme prescrição",
  "horario": "08:00",
  "frequencia": "Conforme prescrição",
  "paciente": "Paciente de demonstração",
  "observacao": null,
  "historico": [
    {"id": "uuid-do-registro", "registradoEm": {"$date": "2026-10-29T12:00:00Z"}}
  ]
}
```

Usuário: `_id`, `nome`, `email` normalizado, `senha` (hash PBKDF2-HMAC-SHA256) e `dataCadastro`.
O histórico atende ao requisito de objeto complexo, com lista de subdocumentos efetivamente utilizada na interface.
São mantidos apenas os últimos 100 registros, uma decisão de escopo para impedir crescimento ilimitado do documento.
A inserção usa `$push/$each/$slice` atômico. A edição de medicamento usa `$set` apenas nos campos editáveis,
para não sobrescrever registros de uso simultâneos. O histórico não constitui prontuário ou auditoria permanente.

Índices: email único em `usuarios`; usuarioId em `medicamentos`; `_id` já é indexado pelo MongoDB.
A anotação `@Indexed` documenta o modelo, mas a criação garantida é pelo script de instalação.

## API

| Método | Rota | Resultado |
| --- | --- | --- |
| POST | /api/auth/cadastro | 200; validação 400; duplicidade concorrente 409 |
| POST | /api/auth/login | 200 com sessão; credenciais inválidas 401 |
| GET | /api/auth/usuario | Dados públicos da conta; sem sessão 401 |
| POST | /api/auth/logout | 204 e invalidação da sessão |
| GET / POST | /api/medicamentos | Listar (200) / cadastrar (201) |
| GET / PUT / DELETE | /api/medicamentos/{id} | Consultar (200), editar (200), excluir (204) |
| GET | /api/medicamentos/{id}/historico | Lista em ordem de inserção (200) |
| POST | /api/medicamentos/{id}/historico | Registra agora, sem corpo (201) |

Operações de medicamentos e histórico exigem sessão (401). Registro ausente ou de outra conta retorna 404.
Erros de validação de corpo e JSON inválido retornam 400. Horário deve estar entre 00:00 e 23:59.
No frontend, `textContent` apresenta dados sem interpretar HTML do usuário.

## Migração e segurança

Faça backup da base antes de atualizar. Não apague volumes para aplicar a segunda entrega.
Execute o script de índices também em bases antigas. Se houver emails duplicados, a criação do índice falha:
revise os cadastros antes de continuar, sem excluir dados automaticamente.
Documentos sem `historico` são lidos como lista vazia; `$push` cria o campo no primeiro uso.
Novas senhas usam PBKDF2-HMAC-SHA256. Senhas antigas em texto puro são convertidas após login correto, sem mudar a senha do usuário.
Contas antigas que nunca efetuarem login ainda precisam de migração/reset antes de uso real.
Cookie HttpOnly e SameSite=Strict, sessão com 30 minutos de inatividade. Sem integração com serviços externos.

Limitações: PoC local; sem recuperação de senha, limitação de tentativas, token CSRF dedicado, notificações,
edição de registros de uso, trilha de auditoria permanente ou certificação para uso clínico.
Uma implantação pública exige HTTPS, cookie Secure, proteção CSRF, gestão de credenciais do banco,
controle de acesso operacional e política de retenção. Não usar dados reais na demonstração acadêmica.

## Estoque e painel — 0.3.0

Cada documento de medicamento pode incluir `estoque: {quantidade: 20, minimo: 5}`.
Ausência/null significa não acompanhado, diferente de saldo zero. Registros antigos continuam válidos.
A quantidade e o mínimo são inteiros de 0 a 100.000; números fracionários são rejeitados.
O alerta visual aparece quando quantidade ≤ mínimo. Não há push, email, cálculo de consumo ou previsão de duração.

| Método | Rota | Corpo / resposta |
| --- | --- | --- |
| POST | /api/medicamentos/{id}/estoque | `{ "quantidade": 20, "minimo": 5 }`; 201 com estoque |
| POST | /api/medicamentos/{id}/estoque/movimentos | `{ "quantidade": -2 }`; 200 com novo saldo |

A inicialização ocorre uma única vez (409 se já existe). O mínimo é definido na inicialização e não tem edição nesta versão.
Movimentos positivos repõem, negativos retiram; zero é recusado. O saldo nunca pode ser menor que zero
ou maior que 100.000. O filtro de proprietário e as condições de saldo são aplicados atomicamente com `$inc`.
Saldo insuficiente, estoque não iniciado e limite excedido retornam 409; outra conta/ID inexistente retorna 404.
Somente o saldo atual é mantido; não há livro de movimentações de estoque nesta versão.
Editar nome/dosagem não modifica estoque nem histórico. Registrar uso não reduz estoque.

`painel-core.js` contém funções puras de busca, resumo, ordenação e CSV. `medicamentos.js` usa DOM e fetch.
Busca/filtros operam sobre os medicamentos já autorizados pela API; não são um mecanismo de autorização.
Indicadores resumem a conta inteira; filtros afetam os cartões e o CSV. Busca desconsidera acentos e caixa.
Ordenação por horário apenas organiza os horários cadastrados; não é agenda de doses previstas.

CSV usa UTF-8 com BOM, delimitador ponto-e-vírgula, escape de aspas e neutralização de fórmulas.
O arquivo é gerado localmente no navegador, contém os registros filtrados e não inclui senha, histórico ou dados de outras contas.
O tema é a única preferência persistida no localStorage. Conteúdo do usuário é exibido com textContent.
Não são carregadas fontes, scripts ou imagens de serviços externos.
