# Fluxos de uso e regras de negócio

[Voltar ao índice](README.md)

## Cadastro, login e sessão

1. Abra a página inicial e selecione **Criar conta**. Preencha nome, e-mail, senha e confirmação. O formulário indica quando as duas senhas divergem.
2. O servidor valida os dados, normaliza o e-mail e impede cadastro duplicado.
3. Em **Entrar**, informe o e-mail e a senha. Quando a autenticação é aceita, o navegador segue para o painel.
4. O painel consulta a conta da sessão e carrega sua lista de medicamentos. Se a sessão estiver ausente ou expirada, o painel encaminha o usuário para entrar.
5. **Sair da conta** invalida a sessão no servidor.

## Cadastro e edição de medicamentos

1. No painel, selecione **Novo medicamento** e preencha nome, dosagem conforme a prescrição, horário, frequência e paciente. A observação é opcional.
2. Ao salvar, a API valida o horário entre `00:00` e `23:59`, os campos obrigatórios e os limites de nome e observação.
3. **Editar** abre o formulário com os dados existentes. **Excluir** pede confirmação antes de remover o medicamento.
4. Se o servidor devolver erros de campo, o formulário preserva os valores para que possam ser corrigidos.

## Histórico de uso

1. Selecione **Registrar uso** e confirme. O servidor grava o instante atual com um identificador único.
2. Selecione **Histórico** para consultar até 100 registros, apresentados do mais recente para o mais antigo. O navegador converte o horário para o fuso local.
3. Cada confirmação gera um registro. O saldo do estoque é atualizado pelo fluxo de movimentação.

## Estoque e reposição

1. Abra **Estoque** no cartão de um medicamento e informe o saldo inicial e o mínimo de reposição.
2. Depois de ativar o controle, selecione **Entrada / reposição** ou **Saída de unidades** e informe a quantidade. A API recebe um número positivo para entrada e negativo para saída.
3. Quando o saldo chega ao mínimo ou fica abaixo dele, o cartão mostra o alerta e aparece no filtro **Apenas reposição**.
4. Uma saída maior que o saldo ou uma entrada que ultrapasse 100.000 unidades gera conflito. Medicamentos sem controle ativo aparecem como estoque não acompanhado.

## Busca, indicadores e CSV

- Os indicadores mostram o total de medicamentos, pacientes distintos, alertas de reposição e medicamentos com estoque acompanhado.
- A busca considera nome, paciente e observações, ignorando acentos e diferenças entre maiúsculas e minúsculas. O filtro de paciente pode ser combinado com **Apenas reposição**.
- A ordenação pode usar horário, nome ou menor estoque. Na ordem por estoque, os medicamentos sem acompanhamento ficam no final.
- **Exportar CSV** usa os resultados filtrados e ordenados. O exportador preserva acentos, escapa aspas e trata valores que uma planilha poderia interpretar como fórmula.
