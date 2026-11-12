# MediCare — Segunda entrega (0.4.0)

PoC acadêmica em Java 17, Spring Boot e MongoDB para organizar medicamentos por usuário.
**ODS 3 — Saúde e Bem-Estar:** centraliza informações para pessoas que utilizam medicamentos,
familiares e cuidadores. Não prescreve, calcula doses ou substitui orientação profissional.

## Identidade visual 0.4

- Nova marca SVG, também usada como favicon, e símbolo decorativo próprio.
- Página inicial editorial; login e cadastro com nova composição; painel com a mesma identidade.
- Paleta verde-tinta, papel e lima; ícones vetoriais e contraste tipográfico.
- Animações de entrada, interações nos cartões, diálogos e transições entre páginas.
- Respeito a redução de movimento, navegação por teclado e controles para mostrar senha.
- **23 testes Node aprovados**; **28 cenários de navegador configurados**.

Consulte [DESIGN_0.4.md](docs/DESIGN_0.4.md) para detalhes e limites de validação.

## Funcionalidades preservadas

- Painel redesenhado com navegação lateral, indicadores, cartões e página inicial renovada.
- Interface responsiva, modo escuro persistente e formulários em diálogos nativos.
- Busca sem acentos por nome, paciente e observações; filtros combinados e ordenação.
- Controle opcional de estoque, entradas/saídas manuais e alerta visual de reposição.
- Exportação CSV da lista filtrada, com proteção contra interpretação de fórmulas.
- Estados de carregamento, lista vazia, erros, confirmação de exclusão e feedback de validação.
- Testes unitários de frontend, testes de navegador desktop/mobile e testes de estoque no backend.

**Estoque é manual:** registrar uso não reduz unidades automaticamente. O sistema não calcula doses.

## Evolução da primeira entrega

| Base recebida | Segunda entrega |
| --- | --- |
| Cadastro, login e CRUD por usuário | Fluxos preservados |
| Senhas em texto puro | PBKDF2-HMAC-SHA256; migração após login válido de conta antiga |
| Documentos simples | Histórico com lista de subdocumentos, UUID e data UTC |
| Sem registro de uso | Botões Registrar uso e Histórico na interface |
| Horário apenas obrigatório | Validação HH:mm |
| Índice de email apenas anotado | Script reproduzível de índices |
| Testes e JaCoCo | Novos testes, CI e limite de 70% sem exclusões de classes |
| README | Documentação técnica, quadro de tarefas e roteiro de vídeo |

## Executar

Requisitos: JDK 17, Docker com Compose e acesso à internet para baixar Maven/dependências.

```bash
docker compose up -d
# Após o MongoDB estar saudável, aplicar índices (inclusive em volumes existentes):
docker compose exec -T mongo mongosh --quiet /docker-entrypoint-initdb.d/mongo-init.js
```

Linux/macOS: `bash mvnw spring-boot:run`.
Windows PowerShell: `.\mvnw.cmd spring-boot:run`.

Abra http://localhost:8080, cadastre uma conta, entre e cadastre um medicamento.
Use **Registrar uso** para registrar o momento atual e **Histórico** para consultar os últimos 100 usos.
Datas são armazenadas em UTC e exibidas no fuso do navegador. Cada clique confirmado representa um novo registro.

Swagger: http://localhost:8080/docs. Contrato: http://localhost:8080/v3/api-docs.
Para banco externo, configure `SPRING_DATA_MONGODB_URI`; aplique os índices de `scripts/mongo-init.js` nessa base.
O MongoDB local fica exposto somente em 127.0.0.1 e o volume preserva os dados.

## Testes e cobertura

```bash
bash mvnw clean verify
```

Windows: `.\mvnw.cmd clean verify`.
O comando executa JUnit/Mockito/MockMvc, gera `target/site/jacoco/index.html` e falha abaixo de **70% das linhas**
do backend Java de produção, sem exclusões de classes. HTML/CSS/JavaScript não são medidos pelo JaCoCo.
O GitHub Actions executa o mesmo comando e disponibiliza os relatórios como artefato.
Os testes unitários utilizam mocks; o teste de integração com MongoDB real é habilitado no CI.
Para executá-lo localmente, consulte `docs/TESTES.md`.

**Estado da validação desta alteração:** o Maven foi invocado, mas o ambiente de edição não resolveu
`repo.maven.apache.org` (`UnknownHostException`). Portanto, compilação, testes e percentual de cobertura
precisam ser confirmados em ambiente com acesso ao Maven Central. Nenhum percentual antigo é atribuído a esta versão.

## Testes do frontend

Node.js 22 ou superior:

```bash
npm test
# Para testes de interface (requer internet na instalação):
npm install
npx playwright install chromium
npm run test:e2e
```

`npm test` executa regras puras sem instalar dependências. Os testes Playwright usam API simulada
para validar a interface; a integração real com MongoDB é testada pela suíte Java.
O workflow tem jobs independentes para backend e frontend.

Validação desta versão: **23 testes unitários do frontend aprovados**, com regras de dados e navegação.
Os **28 cenários de navegador** (14 cenários × desktop/mobile) foram reconhecidos pelo runner,
mas a execução e a inspeção visual continuam pendentes: Chromium ausente no ambiente.
A validação Java/MongoDB e a cobertura JaCoCo permanecem pendentes devido ao download Maven bloqueado.
Os números de cobertura anteriores não comprovam a cobertura completa desta versão.

## Documentação da entrega

- [Arquitetura, dados, API e migração](docs/TECNICA.md)
- [Plano de testes e evidências](docs/TESTES.md)
- [Quadro de tarefas](docs/QUADRO_TAREFAS.md)
- [Roteiro do vídeo de 3 a 5 minutos](docs/ROTEIRO_VIDEO.md)
- [Checklist e identificação dos integrantes](docs/ENTREGA.md)
- [Alterações](CHANGELOG.md)

## Versionamento

Este ZIP não contém o histórico Git original. A versão Maven foi identificada como `0.4.0`.
Copie os arquivos para o repositório existente, revise o diff e publique um commit real.
Não recrie nem invente commits da primeira entrega. Após `verify` passar, marque a entrega:

```bash
git add .
git update-index --chmod=+x mvnw
git commit -m "feat: evolui MediCare para segunda entrega da AEP"
git tag -a v0.4.0 -m "Segunda entrega AEP"
git push origin HEAD
git push origin v0.4.0
```

O envio ao GitHub, o link do repositório, a gravação e publicação do vídeo e os dados dos integrantes
continuam sob responsabilidade da equipe. Consulte o checklist antes de entregar.
