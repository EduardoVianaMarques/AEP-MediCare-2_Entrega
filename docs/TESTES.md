# Testes e evidências

## Automatizados

Execute `bash mvnw clean verify` (Windows: `.\mvnw.cmd clean verify`).
Abra `target/site/jacoco/index.html`; preserve também XML e relatórios Surefire.
A regra exige 70% das linhas de todo o backend. O workflow publica os relatórios mesmo quando o build falha.

Testes existentes: CRUD, isolamento de usuários, mapper, modelos, autenticação, validação e OpenAPI.
Novos/ajustados: hash de senha, migração de senha antiga, consulta do histórico, filtro por proprietário,
registro atômico com limite, ausência de sessão, medicamento de outra conta, horário inválido e JSON malformado.
Mocks não comprovam persistência real. É necessário executar a validação abaixo.

## Teste automatizado com MongoDB real

Com MongoDB em localhost:27017: Linux/macOS `MONGODB_INTEGRATION=true bash mvnw clean verify`.
No PowerShell: `$env:MONGODB_INTEGRATION="true"` e depois `.\mvnw.cmd clean verify`.
O teste cria uma base `medicare_test_<UUID>` isolada e a remove ao terminar.
Verifica persistência, relacionamento, limite de 100 usos, edição parcial e isolamento.
Sem a variável, é ignorado; no GitHub Actions é habilitado com um serviço MongoDB.

## Validação manual com MongoDB

1. Subir o Compose, aplicar índices e executar a aplicação.
2. Cadastrar duas contas fictícias A/B. Repetir email de A: cadastro deve ser recusado.
3. Entrar com A, cadastrar um medicamento e anotar seu ID via Swagger ou aba Network.
4. Registrar uso e abrir Histórico: data/hora deve aparecer. Atualizar a página e reiniciar a aplicação: registro permanece.
5. Editar nome/horário: histórico deve permanecer. Testar 25:70 diretamente pela API: 400.
6. Sair e entrar com B. A lista não mostra A. GET/POST no histórico do ID de A deve retornar 404.
7. Sair: GET/POST de histórico retorna 401.
8. Voltar a A e excluir medicamento: GET retorna 404 e o histórico deixa de existir.
9. Inspecionar a base: senhas novas começam com `{pbkdf2}`; não há senha em texto puro para contas novas.
10. Conferir os índices com `db.usuarios.getIndexes()` e `db.medicamentos.getIndexes()` no mongosh.
11. Em base de teste, criar usuário legado em texto puro, entrar uma vez e verificar conversão para PBKDF2-HMAC-SHA256.
12. Registrar 101 usos em um medicamento fictício; histórico deve conter 100 itens. Não executar com dados reais.

## Estado honesto das evidências

Durante a edição, `./mvnw -B clean verify` falhou antes da compilação por
`java.net.UnknownHostException: repo.maven.apache.org`. O log desta tentativa está em
`docs/evidencias/tentativa-maven.log`. Não é um relatório de testes aprovados.
A medição nova ainda não foi obtida; não reutilizar a cobertura da primeira entrega.
O ambiente também não executou a validação com MongoDB nem a interface no navegador.
Após execução pela equipe/CI, registrar data, commit, total de testes, falhas e percentual real abaixo.

- Commit validado: pendente
- Total de testes executados: pendente
- Falhas: pendente
- Linhas cobertas / total: pendente
- Percentual: pendente
- Validação com banco e navegador: pendente

## Ampliação do frontend e estoque

Resultados e comandos da versão 0.3.0: [VALIDACAO_0.3.md](VALIDACAO_0.3.md).
No fluxo manual, iniciar estoque em 2 unidades/mínimo 5, verificar alerta, adicionar 10 e verificar remoção do alerta.
Tentar retirar 100: erro, sem alteração do saldo. Editar medicamento e registrar uso: saldo permanece.
Filtrar por paciente, buscar sem acentos e conferir que o CSV contém apenas os cartões filtrados.
Testar no celular, alternar tema, usar Tab/Escape nos diálogos e simular API offline.
