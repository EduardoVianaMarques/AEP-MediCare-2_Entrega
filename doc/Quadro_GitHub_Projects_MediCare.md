# MediCare — Quadro da segunda entrega da AEP

**Visualização sugerida** · Board, agrupado pelo campo **Status**  
**Colunas sugeridas** · A fazer → Em andamento → Concluído

Use os títulos abaixo para criar as issues do repositório e adicione-as ao Project. Copie o objetivo e o checklist para o corpo da issue. Use o campo **Assignees** para selecionar a conta GitHub do responsável indicado. Preencha **Evidência** com o link do PR, commit, relatório ou execução do GitHub Actions.

A distribuição de responsáveis segue a lista da equipe. Defina o status de cada cartão com base no andamento real e marque os critérios conforme forem comprovados.

| ID | Tarefa | Responsável |
| --- | --- | --- |
| AEP-01 | Configurar o MongoDB e modelar as coleções | Eduardo |
| AEP-02 | Implementar cadastro, login e sessão | Eduardo |
| AEP-03 | Implementar CRUD de medicamentos | Eduardo |
| AEP-04 | Implementar histórico de uso e controle de estoque | Eduardo |
| AEP-05 | Redesenhar as telas do MediCare | Leonardo |
| AEP-06 | Adicionar busca, filtros e exportação CSV | Leonardo |
| AEP-07 | Validar responsividade e acessibilidade | Leonardo |
| AEP-08 | Executar os testes do front-end | Leonardo |
| AEP-09 | Executar os testes Java com MongoDB | Pedro |
| AEP-10 | Comprovar cobertura mínima de 70% | Pedro |
| AEP-11 | Atualizar documentação e quadro da entrega | Pedro |

## AEP-01 — Configurar o MongoDB e modelar as coleções

**Responsável** · Eduardo

Configure a conexão com o MongoDB e documente as coleções `usuarios` e `medicamentos`, o vínculo por `usuarioId` e os índices.

- [ ] A aplicação conecta a uma base MongoDB de teste.
- [ ] As coleções, o vínculo e os índices estão documentados, e os índices foram conferidos na base de teste.

**Evidência** · Adicionar link da documentação e registro da conferência dos índices.

## AEP-02 — Implementar cadastro, login e sessão

**Responsável** · Eduardo

Implemente o cadastro de usuários, o armazenamento de senhas com hash, a autenticação e o encerramento da sessão no logout.

- [ ] Uma conta pode ser criada e autenticada, e a senha é armazenada com hash.
- [ ] O cadastro rejeita e-mail duplicado e o login rejeita senha incorreta.
- [ ] Após o logout, as rotas protegidas voltam a exigir autenticação.

**Evidência** · Adicionar link do PR ou commit e dos testes de autenticação.

## AEP-03 — Implementar CRUD de medicamentos

**Responsável** · Eduardo

Permita cadastrar, listar, editar e excluir medicamentos vinculados ao usuário autenticado.

- [ ] O usuário consegue cadastrar, listar, editar e excluir seus medicamentos.
- [ ] Uma segunda conta fica impedida de consultar, alterar ou excluir os medicamentos da primeira.

**Evidência** · Adicionar link do PR ou commit e dos testes de CRUD e isolamento entre contas.

## AEP-04 — Implementar histórico de uso e controle de estoque

**Responsável** · Eduardo

Registre usos como subdocumentos e implemente entradas e saídas de estoque com alerta de reposição.

- [ ] O histórico persiste após recarregar e mantém os 100 registros mais recentes.
- [ ] Entradas e saídas atualizam o saldo, e o alerta aparece quando ele atinge o mínimo ou fica abaixo dele.
- [ ] Uma saída acima do saldo é rejeitada.

**Evidência** · Adicionar link dos testes de histórico e estoque.

## AEP-05 — Redesenhar as telas do MediCare

**Responsável** · Leonardo

Aplique a identidade visual do MediCare à página inicial, ao login, ao cadastro e ao painel.

- [ ] As quatro telas seguem a mesma identidade visual.
- [ ] Os fluxos principais podem ser concluídos pela interface.

**Evidência** · Adicionar link do PR ou commit e capturas das quatro telas.

## AEP-06 — Adicionar busca, filtros e exportação CSV

**Responsável** · Leonardo

Implemente busca por medicamentos, combinação de filtros, ordenação da lista e exportação dos resultados.

- [ ] A busca encontra os mesmos registros com termos digitados com ou sem acento.
- [ ] Os filtros podem ser combinados, e a ordenação é aplicada à lista exibida.
- [ ] O CSV contém apenas os registros exibidos após aplicar os filtros.

**Evidência** · Adicionar link dos testes ou exemplo de exportação com dados fictícios.

## AEP-07 — Validar responsividade e acessibilidade

**Responsável** · Leonardo

Confira as telas em desktop e celular, a navegação por teclado, o foco nos diálogos e a preferência por movimento reduzido.

- [ ] Os controles principais funcionam em desktop e celular.
- [ ] Os diálogos podem ser operados por teclado e devolvem o foco ao fechar.
- [ ] A interface respeita a preferência por movimento reduzido.

**Evidência** · Adicionar link do relatório de teste ou do registro da verificação manual.

## AEP-08 — Executar os testes do front-end

**Responsável** · Leonardo

Execute os testes Node e os cenários Playwright.

- [ ] O resultado de `npm test` está registrado com o commit testado.
- [ ] O resultado de `npm run test:e2e` está registrado com o commit testado.

**Evidência** · Adicionar link da execução do CI e do relatório Playwright.

## AEP-09 — Executar os testes Java com MongoDB

**Responsável** · Pedro

Execute os testes unitários, de API e de integração usando uma base de teste.

- [ ] Os resultados das suítes Java e da integração com MongoDB estão registrados.
- [ ] A execução comprova os fluxos de autenticação, CRUD, histórico, estoque e isolamento entre contas.

**Evidência** · Adicionar link da execução do CI ou dos relatórios de teste do Maven.

## AEP-10 — Comprovar cobertura mínima de 70%

**Responsável** · Pedro

Execute `clean verify` e gere o relatório JaCoCo da versão entregue.

- [ ] O relatório JaCoCo mostra pelo menos **70% de cobertura de linhas do backend**.
- [ ] O relatório está vinculado ao commit avaliado.

**Evidência** · Adicionar link do artefato JaCoCo ou da execução do CI associada ao commit.

## AEP-11 — Atualizar documentação e quadro da entrega

**Responsável** · Pedro

Revise o README, a documentação técnica, o quadro de tarefas e a identificação da equipe.

- [ ] As instruções permitem instalar, executar e testar a PoC.
- [ ] O README contém o link do quadro e o quadro mostra responsáveis e evidências.
- [ ] A ficha contém os nomes e RAs dos integrantes.

