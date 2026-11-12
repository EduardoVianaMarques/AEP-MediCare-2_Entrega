# Catálogo dos casos de teste

[Voltar ao índice](README.md) · [Ver resultados](TESTES.md#resultados-verificados)

Os IDs deste catálogo são referências da documentação. Os nomes apontam para os casos definidos no código.

## Java

Foram identificados **86 métodos de teste**. O teste parametrizado de estoque acrescenta quatro execuções no lugar de uma, totalizando **89 casos previstos** com a integração MongoDB habilitada.

### AuthControllerTest

`src/test/java/medicare/controller/AuthControllerTest.java`

| ID | Método |
| --- | --- |
| JAVA-01 | `cadastroOk` |
| JAVA-02 | `cadastroDuplicado` |
| JAVA-03 | `loginOk` |
| JAVA-04 | `loginSenhaInvalida` |
| JAVA-05 | `loginUsuarioInexistente` |
| JAVA-06 | `usuarioSemSessao` |
| JAVA-07 | `usuarioComSessao` |
| JAVA-08 | `logout` |

### EstoqueControllerTest

`src/test/java/medicare/controller/EstoqueControllerTest.java`

| ID | Método |
| --- | --- |
| JAVA-09 | `exigeSessao` |
| JAVA-10 | `inicia` |
| JAVA-11 | `movimenta` |
| JAVA-12 | `validaEntrada` (4 variações) |
| JAVA-13 | `rejeitaQuantidadeFracionada` |
| JAVA-14 | `informaConflito` |
| JAVA-15 | `escondeRegistroDeOutraConta` |

### HistoricoControllerTest

`src/test/java/medicare/controller/HistoricoControllerTest.java`

| ID | Método |
| --- | --- |
| JAVA-16 | `exigeSessaoEmAmbasOperacoes` |
| JAVA-17 | `registraUsoNaContaDaSessao` |
| JAVA-18 | `lista` |
| JAVA-19 | `retorna404ParaOutroUsuario` |

### MedicamentoControllerTest

`src/test/java/medicare/controller/MedicamentoControllerTest.java`

| ID | Método |
| --- | --- |
| JAVA-20 | `semSessaoRetorna401` |
| JAVA-21 | `cadastra` |
| JAVA-22 | `lista` |
| JAVA-23 | `busca` |
| JAVA-24 | `atualiza` |
| JAVA-25 | `exclui` |
| JAVA-26 | `validaCampos` |
| JAVA-27 | `naoEncontrado` |
| JAVA-28 | `rejeitaHorarioInvalido` |
| JAVA-29 | `rejeitaJsonMalformado` |

### PaginaControllerTest

`src/test/java/medicare/controller/PaginaControllerTest.java`

| ID | Método |
| --- | --- |
| JAVA-30 | `inicio` |
| JAVA-31 | `login` |
| JAVA-32 | `cadastro` |
| JAVA-33 | `medicamentos` |

### DocumentacaoTest

`src/test/java/medicare/documentation/DocumentacaoTest.java`

| ID | Método |
| --- | --- |
| JAVA-34 | `deveDisponibilizarFrontend` |
| JAVA-35 | `deveGerarContratoOpenApi` |
| JAVA-36 | `deveDisponibilizarSwaggerUi` |

### ExceptionTest

`src/test/java/medicare/exception/ExceptionTest.java`

| ID | Método |
| --- | --- |
| JAVA-37 | `exceptionMessage` |
| JAVA-38 | `apiError` |

### MongoIntegrationTest

`src/test/java/medicare/integration/MongoIntegrationTest.java`

| ID | Método |
| --- | --- |
| JAVA-39 | `estoquePersisteESaidasConcorrentesNaoGeramSaldoNegativo` |
| JAVA-40 | `persisteRelacionamentoHistoricoLimiteEEdicaoParcial` |

### MedicamentoMapperTest

`src/test/java/medicare/mapper/MedicamentoMapperTest.java`

| ID | Método |
| --- | --- |
| JAVA-41 | `deveMapearMedicamentoRequestParaEntity` |
| JAVA-42 | `deveMapearMedicamentoEntityParaResponse` |
| JAVA-43 | `deveAtualizarTodosOsDadosDaEntity` |
| JAVA-44 | `devePreservarIdAoAtualizarEntity` |
| JAVA-45 | `devePreservarDataDeCadastroAoAtualizarEntity` |

### ModelTest

`src/test/java/medicare/model/ModelTest.java`

| ID | Método |
| --- | --- |
| JAVA-46 | `deveCriarMedicamentoComTodosOsDados` |
| JAVA-47 | `deveAlterarDadosDoMedicamento` |
| JAVA-48 | `deveDefinirUsuarioEDataDeCadastroDoMedicamento` |
| JAVA-49 | `devePermitirCriarMedicamentoVazio` |
| JAVA-50 | `deveCriarUsuarioComTodosOsDados` |
| JAVA-51 | `deveAlterarDadosDoUsuario` |
| JAVA-52 | `deveDefinirDataDeCadastroDoUsuario` |
| JAVA-53 | `usuarioNovoDevePossuirIdNulo` |
| JAVA-54 | `usuarioDevePermitirAlteracaoCompletaDosDados` |

### EstoqueServiceTest

`src/test/java/medicare/service/EstoqueServiceTest.java`

| ID | Método |
| --- | --- |
| JAVA-55 | `iniciaSemSobrescreverSaldoExistente` |
| JAVA-56 | `movimentaSaldoComFiltroDoProprietario` |
| JAVA-57 | `retornaConflitoParaSaldoInsuficiente` |
| JAVA-58 | `naoReiniciaEstoqueExistente` |
| JAVA-59 | `naoExpoeMedicamentoDeOutraConta` |
| JAVA-60 | `recusaMovimentoZero` |
| JAVA-61 | `exigeUsuario` |

### HistoricoServiceTest

`src/test/java/medicare/service/HistoricoServiceTest.java`

| ID | Método |
| --- | --- |
| JAVA-62 | `registraAtomicamenteComFiltroDoProprietarioELimite` |
| JAVA-63 | `naoRegistraEmMedicamentoAusenteOuDeOutraConta` |
| JAVA-64 | `listaHistoricoESuportaDocumentoAntigo` |
| JAVA-65 | `naoListaMedicamentoDeOutraConta` |
| JAVA-66 | `exigeUsuarioAntesDeAcessarBanco` |

### MedicamentoServiceTest

`src/test/java/medicare/service/MedicamentoServiceTest.java`

| ID | Método |
| --- | --- |
| JAVA-67 | `cadastrar` |
| JAVA-68 | `listar` |
| JAVA-69 | `buscar` |
| JAVA-70 | `buscarInexistente` |
| JAVA-71 | `atualizar` |
| JAVA-72 | `atualizarInexistente` |
| JAVA-73 | `excluir` |
| JAVA-74 | `excluirInexistente` |
| JAVA-75 | `exigeUsuario` |
| JAVA-76 | `naoDeveBuscarMedicamentoDeOutroUsuario` |
| JAVA-77 | `naoDeveAtualizarMedicamentoDeOutroUsuario` |
| JAVA-78 | `naoDeveExcluirMedicamentoDeOutroUsuario` |

### UsuarioServiceTest

`src/test/java/medicare/service/UsuarioServiceTest.java`

| ID | Método |
| --- | --- |
| JAVA-79 | `cadastrarSenhaComHash` |
| JAVA-80 | `rejeitaEmailDuplicado` |
| JAVA-81 | `buscaEmailNormalizado` |
| JAVA-82 | `buscaInexistente` |
| JAVA-83 | `senhaValida` |
| JAVA-84 | `naoMigraSenhaLegadaIncorreta` |
| JAVA-85 | `rejeitaValoresNulos` |
| JAVA-86 | `suportaSenhaUnicodeLongaSemTruncamento` |

## Frontend unitário

### core.test.cjs

`tests/frontend/core.test.cjs`

| ID | Cenário automatizado |
| --- | --- |
| NODE-01 | busca ignora acentos e caixa |
| NODE-02 | busca também paciente |
| NODE-03 | busca observações |
| NODE-04 | combina filtros e não confunde estoque ausente com zero |
| NODE-05 | ordena horários e não modifica entrada |
| NODE-06 | ordena estoque com não acompanhado por último |
| NODE-07 | resumo conta pacientes únicos e alertas |
| NODE-08 | estoque no mínimo dispara alerta |
| NODE-09 | estoque zero dispara alerta |
| NODE-10 | lista vazia tem resumo zero |
| NODE-11 | CSV escapa aspas, separador e quebras de linha |
| NODE-12 | CSV neutraliza fórmulas e preserva UTF-8 |
| NODE-13 | CSV não exporta undefined nem null |
| NODE-14 | sem resultados não produz itens |

### experience.test.cjs

`tests/frontend/experience.test.cjs`

| ID | Cenário automatizado |
| --- | --- |
| NODE-15 | navegação reduzida é imediata e não anima |
| NODE-16 | navegação nativa não sofre atraso artificial |
| NODE-17 | fallback faz saída curta e evita clique duplicado |
| NODE-18 | voltar pelo histórico restaura a interação |
| NODE-19 | navegação interrompida não deixa a página invisível |
| NODE-20 | alternar tema persiste preferência mesmo sem View Transition API |
| NODE-21 | fechar diálogo respeita redução de movimento |
| NODE-22 | clique com Ctrl preserva a abertura em outra aba |
| NODE-23 | âncora na própria página mantém comportamento padrão |

## Interface no navegador

Cada cenário é configurado para desktop e celular, totalizando 28 execuções.

### identidade.spec.cjs

`tests/e2e/identidade.spec.cjs`

| ID | Cenário automatizado |
| --- | --- |
| E2E-01 | página inicial e navegação até cadastro mantêm identidade |
| E2E-02 | login mostra senha apenas quando solicitado |
| E2E-03 | redução de movimento mantém navegação e conteúdo |
| E2E-04 | cadastro com senhas diferentes conserva os campos e mostra erro |

### painel.spec.cjs

`tests/e2e/painel.spec.cjs`

| ID | Cenário automatizado |
| --- | --- |
| E2E-05 | painel resume dados e não gera rolagem horizontal |
| E2E-06 | busca sem acentos, paciente e reposição |
| E2E-07 | cadastra, edita e exclui com confirmação |
| E2E-08 | registra uso e consulta histórico |
| E2E-09 | entrada e erro de saldo insuficiente |
| E2E-10 | exporta lista filtrada e persiste preferência de tema |
| E2E-11 | não interpreta HTML vindo do cadastro |
| E2E-12 | mostra erro de carregamento e permite tentar novamente |
| E2E-13 | erro de validação mantém formulário e dados |
| E2E-14 | formulário fecha por teclado e devolve o foco |
