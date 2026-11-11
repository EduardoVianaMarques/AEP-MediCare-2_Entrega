# Validação da versão 0.3.0

## O que foi executado

- `npm test`: **14 testes aprovados, 0 falhas**. Não depende de MongoDB, Maven ou instalação npm.
- `node --experimental-test-coverage --test tests/frontend/*.test.cjs`: 96,55% de linhas,
  71,43% de ramos e 100% de funções **apenas em painel-core.js**.
- `node --check`: scripts da aplicação, configuração, servidor de testes e testes E2E.
- Análise de XML/YAML e checagem de IDs únicos e arquivos estáticos referenciados.
- Playwright `--list`: 20 cenários reconhecidos (10 cenários em desktop e mobile).

## O que ficou bloqueado

- Maven: download indisponível (`UnknownHostException: repo.maven.apache.org`). Sem confirmação de compilação,
  testes Java ou cobertura JaCoCo desta versão.
- Playwright: runner disponível, mas Chromium ausente. Tentativa de instalação falhou no download.
  O cenário de painel falhou na inicialização do navegador, antes de exercitar a interface.
- Não houve inspeção visual em navegador nem teste real com MongoDB neste ambiente.

Os arquivos em `docs/evidencias` registram execuções e falhas, sem apresentar uma tentativa bloqueada como sucesso.

## Como completar a validação

1. Java 17 + acesso ao Maven Central: executar `bash mvnw clean verify` ou `.\mvnw.cmd clean verify`.
2. Com MongoDB local ativo, habilitar `MONGODB_INTEGRATION=true` e repetir verify. Em PowerShell:
   `$env:MONGODB_INTEGRATION="true"` antes do comando.
3. Node 22: `npm install`, `npx playwright install chromium`, `npm test`, `npm run test:e2e`.
4. Abrir `playwright-report/index.html` e os screenshots de painel em `test-results`.
5. Abrir JaCoCo, confirmar ≥70% das linhas do backend e registrar o percentual real no checklist.
6. Validar manualmente no backend real o cadastro, edição, estoque, uso e consulta entre duas contas.

## Cobertura dos cenários novos

Backend: inicialização e movimentação de estoque, validação, ausência de sessão, proprietário incorreto,
saldo insuficiente, inicialização duplicada, limite superior, preservação em edição e duas saídas concorrentes.

Frontend unitário: normalização, combinações de filtros, ordenação sem mutar entrada, resumo, alertas e CSV seguro.

Navegador (API simulada): indicadores, responsividade, filtros, CRUD, uso/histórico, estoque/erro,
exportação/tema, dados contendo HTML, erro de carregamento/retry, erro de validação e foco por teclado.
