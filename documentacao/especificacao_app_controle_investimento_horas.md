# Especificação do aplicativo — Controle de Investimento de Horas

## 1. Visão geral

Aplicativo Android para registrar como as horas do dia foram investidas, acompanhar metas por categoria e analisar a evolução diária e semanal.

O aplicativo substitui o fluxo principal da planilha `Controle horas.xlsx`, especialmente as abas:

- `Meta hr`: metas, realizado e desvio por categoria;
- `Dados Detalhados hr`: lançamentos individuais de tempo;
- `Evolução Frentes`: evolução diária por categoria;
- `Dados Medidos hr`: consolidação semanal;
- `Listas`: categorias válidas.

O MVP deve funcionar totalmente offline no celular, sem login e sem dependência de servidor. Os dados devem ser persistidos localmente e poderão ser exportados em CSV.

## 2. Objetivos

- Permitir o registro rápido das atividades realizadas ao longo do dia.
- Mostrar quanto tempo foi dedicado a cada categoria.
- Comparar o realizado com a meta definida para cada categoria.
- Exibir acompanhamento diário e semanal por meio de tabelas e gráficos.
- Permitir a extração dos registros detalhados em formato CSV.
- Manter a arquitetura preparada para categorias configuráveis, importação, backup e sincronização futura.

## 3. Escopo do MVP

### Incluído

- Aplicativo Android nativo desenvolvido em Java.
- Funcionamento 100% local e offline.
- Categorias iniciais fixas.
- Cadastro, edição e exclusão de registros de tempo.
- Metas percentuais por categoria.
- Cálculos diários, semanais e por período.
- Painel de metas e desvios.
- Gráfico de evolução diária.
- Resumo e gráfico semanal.
- Exportação dos registros detalhados em CSV.
- Preferências locais, incluindo início da semana.

### Fora do MVP

- Login e múltiplos usuários.
- Sincronização em nuvem.
- Aplicativo para iOS ou versão web.
- Categorias criadas ou removidas pelo usuário.
- Integração automática com calendário ou outros aplicativos.
- Importação da planilha histórica.
- Notificações e lembretes.
- Início/fim automático por geolocalização ou sensores.

## 4. Categorias iniciais

As categorias deverão ser cadastradas na criação do banco de dados e permanecer fixas no MVP:

1. Alimentação
2. Casa
3. Cuidado
4. Desperdício
5. Diversão
6. Dormir
7. Estudo
8. Itaú Estudo
9. Itaú Trabalho
10. Pessoal
11. Planejamento
12. Treino

Cada categoria deve ter internamente:

- identificador estável;
- nome;
- cor para gráficos;
- ordem de exibição;
- tipo da meta: `MINIMO` ou `MAXIMO`;
- percentual-alvo;
- indicador de ativa/inativa, previsto para uso futuro.

Configuração inicial baseada na linha atual de `Meta hr`:

| Categoria | Meta diária | Percentual do dia | Tipo |
|---|---:|---:|---|
| Alimentação | 1h04min48s | 4,5% | Mínimo |
| Casa | 2h31min12s | 10,5% | Máximo |
| Cuidado | 26min | 1,8% | Mínimo |
| Desperdício | 1h12min | 4,4% | Mínimo |
| Diversão | 57min36s | 4,0% | Mínimo |
| Dormir | 7h12min | 30,0% | Mínimo |
| Estudo | 7min12s | 0,5% | Máximo |
| Itaú Estudo | 1h55min12s | 8,0% | Máximo |
| Itaú Trabalho | 6h21min36s | 26,5% | Mínimo |
| Pessoal | 1h56min38s | 8,1% | Mínimo |
| Planejamento | 7min12s | 0,5% | Mínimo |
| Treino | 17min17s | 1,2% | Máximo |

Observação: os nomes, percentuais e tipos devem ser inicializados com esses valores, mas as metas precisam ser editáveis em uma tela própria. O total inicial é 99,5%; portanto, o aplicativo deve alertar quando o total não for 100%, sem impedir o salvamento.

## 5. Regras de negócio

### 5.1 Registro de atividade

Cada registro representa um intervalo contínuo dedicado a uma categoria e contém:

- data;
- data/hora inicial;
- data/hora final;
- duração calculada;
- categoria;
- observação opcional;
- data/hora de criação e última alteração.

Regras:

- O término deve ser posterior ao início.
- A duração deve ser calculada em segundos, nunca armazenada como número decimal de horas.
- Um intervalo que cruza a meia-noite é válido e deve ser dividido logicamente entre os dias nos relatórios diários e semanais.
- O aplicativo deve alertar e bloquear intervalos sobrepostos.
- Um dia pode ter lacunas sem atividade registrada.
- Edição e exclusão devem atualizar imediatamente todos os resumos e gráficos.
- A semana observada na planilha vai de domingo a sábado; essa deve ser a configuração padrão.
- O número da semana deve ser calculado, não digitado pelo usuário.

### 5.2 Metas

- A meta de cada categoria é uma porcentagem das horas disponíveis no período.
- Para um dia completo, a base é 24 horas.
- Para uma semana completa, a base é 168 horas.
- Para intervalos parciais, a base é a quantidade exata de horas entre o início e o fim do período.
- Meta em horas = percentual-alvo × horas do período.
- Percentual realizado = horas realizadas na categoria ÷ horas totais do período.
- Os relatórios devem apresentar valor realizado, percentual realizado, meta e desvio.

### 5.3 Interpretação do desvio

O tipo da meta reproduz o conceito da linha `Meta Tipo` da planilha:

- `MINIMO`: mais tempo é melhor; cumprimento = realizado ≥ meta.
- `MAXIMO`: menos tempo é melhor; cumprimento = realizado ≤ meta.

Fórmulas:

- Para `MINIMO`: `desvioPercentual = realizadoPercentual - metaPercentual`.
- Para `MAXIMO`: `desvioPercentual = metaPercentual - realizadoPercentual`.
- Desvio em horas = desvio percentual × horas do período.
- Resultado positivo significa desempenho favorável; negativo significa desempenho desfavorável.

Para facilitar a leitura, mostrar também:

- `Atingida`, quando o desvio for maior ou igual a zero;
- `Não atingida`, quando o desvio for menor que zero;
- progresso percentual, limitado visualmente quando necessário, mas mantendo o valor real nos detalhes.

## 6. Telas e navegação

### 6.1 Hoje

Tela inicial voltada ao lançamento rápido:

- data selecionada, com navegação para o dia anterior e seguinte;
- total registrado no dia e horas ainda sem classificação;
- lista cronológica dos registros;
- botão `Adicionar atividade`;
- atalhos para editar ou excluir um registro;
- resumo compacto por categoria;
- aviso visual de sobreposição, lacuna ou total diário superior a 24 horas.

Formulário de atividade:

- categoria;
- data/hora inicial;
- data/hora final;
- duração calculada em tempo real;
- observação opcional;
- ações `Salvar` e `Cancelar`.

Melhoria recomendada no MVP: botão `Iniciar agora`, que cria um cronômetro local, e botão `Encerrar`, que conclui o registro. Apenas um cronômetro pode ficar ativo.

### 6.2 Metas

Equivalente funcional à aba `Meta hr`:

- seletor de período: dia, semana ou intervalo personalizado;
- cartões ou tabela por categoria contendo:
  - meta percentual;
  - meta em horas;
  - realizado em horas;
  - realizado percentual;
  - desvio em horas e pontos percentuais;
  - situação da meta;
- total de horas registradas e não classificadas;
- edição do percentual e do tipo de cada meta;
- aviso quando a soma das metas for diferente de 100%.

### 6.3 Evolução diária

Equivalente funcional à aba `Evolução Frentes`:

- período padrão: últimos 30 dias;
- opções de 7, 30 e 90 dias e intervalo personalizado;
- seleção de uma ou várias categorias;
- gráfico de linhas com uma série por categoria;
- eixo X: data;
- eixo Y alternável entre:
  - horas realizadas no dia;
  - desvio acumulado em horas em relação à meta;
- linha de referência zero no modo de desvio;
- toque em um ponto para exibir data, categoria, realizado, meta e desvio;
- tabela diária opcional abaixo do gráfico.

O modo inicial recomendado é `desvio acumulado`, por ser o mais próximo do acompanhamento da planilha. O usuário deve poder alternar para `horas por dia`.

### 6.4 Acompanhamento semanal

Equivalente funcional à aba `Dados Medidos hr`, calculado diretamente dos registros detalhados:

- semanas de domingo a sábado por padrão;
- lista de semanas, da mais recente para a mais antiga;
- data inicial, data final e total registrado;
- horas e percentual por categoria;
- comparação com a meta semanal;
- seleção de uma semana para abrir seus dias e registros.

Novo gráfico semanal:

- tipo padrão: barras agrupadas;
- eixo X: semanas;
- eixo Y: horas;
- filtros de categoria;
- para cada categoria selecionada, mostrar `Realizado` e `Meta`;
- opção de alternar para gráfico de linha de `Desvio semanal`;
- períodos rápidos: 4, 8, 12 e 26 semanas;
- toque na barra ou ponto mostra semana, realizado, meta, diferença e situação.

### 6.5 Dados e exportação

- lista pesquisável de todos os registros;
- filtros por data e categoria;
- ordenação cronológica;
- botão `Exportar CSV`;
- compartilhamento ou salvamento pelo seletor de arquivos do Android;
- mensagem com quantidade de linhas e período exportado.

### 6.6 Configurações

- edição das metas e tipos;
- primeiro dia da semana, inicialmente domingo;
- formato de duração: `HH:mm` ou horas decimais apenas para exibição;
- separador do CSV: vírgula ou ponto e vírgula, com ponto e vírgula como padrão em `pt-BR`;
- tema claro, escuro ou padrão do sistema;
- versão do aplicativo.

## 7. Exportação CSV

O CSV deve ser compatível com a estrutura da aba `Dados Detalhados hr` e conter uma linha por registro:

```csv
Data;Hora de Início;Hora de Término;Semana;Duração Real;Projeto
2026-09-06;00:00:00;03:50:00;253;03:50:00;Dormir
```

Requisitos:

- Codificação UTF-8 com BOM, para boa abertura no Excel em português.
- Datas em `yyyy-MM-dd`.
- Horários e duração em `HH:mm:ss`.
- Cabeçalhos exatamente como acima.
- Ordenação por data e hora inicial.
- Nome sugerido: `controle_horas_yyyyMMdd_HHmmss.csv`.
- O campo `Semana` deve usar o identificador sequencial interno compatível com o aplicativo; para interoperabilidade futura, recomenda-se acrescentar opcionalmente `Ano-Semana ISO` em uma versão 2 do formato.
- Se um registro cruzar a meia-noite, o CSV deve preservar o registro original; a divisão é somente para agregações.
- Campos textuais devem ser escapados conforme RFC 4180 quando contiverem separador, aspas ou quebra de linha.

## 8. Modelo de dados local

Usar Room sobre SQLite.

### `Category`

| Campo | Tipo | Regra |
|---|---|---|
| `id` | `long` | Chave primária estável |
| `name` | `String` | Único e obrigatório |
| `colorArgb` | `int` | Cor da categoria |
| `displayOrder` | `int` | Ordem de exibição |
| `goalType` | `String` | `MINIMO` ou `MAXIMO` |
| `targetBasisPoints` | `int` | Percentual × 100; 4,5% = 450 |
| `active` | `boolean` | Sempre verdadeiro no MVP |

### `TimeEntry`

| Campo | Tipo | Regra |
|---|---|---|
| `id` | `long` | Chave primária autogerada |
| `categoryId` | `long` | Chave estrangeira para `Category` |
| `startEpochMillis` | `long` | Início no fuso local |
| `endEpochMillis` | `long` | Fim, maior que o início |
| `durationSeconds` | `long` | Calculado ao salvar |
| `note` | `String?` | Opcional |
| `createdAtEpochMillis` | `long` | Auditoria |
| `updatedAtEpochMillis` | `long` | Auditoria |

### `AppPreference`

Tabela chave/valor para primeiro dia da semana, separador CSV, formato de exibição e demais preferências.

### Índices

- Índice por `TimeEntry.startEpochMillis`.
- Índice composto por `TimeEntry.categoryId` e `TimeEntry.startEpochMillis`.
- Restrição de unicidade para `Category.name`.

Os resumos diário e semanal devem ser calculados por consultas, sem duplicar agregados em tabelas no MVP.

## 9. Arquitetura técnica

- Linguagem: Java.
- Interface: XML layouts com Material Components.
- Arquitetura: MVVM.
- Persistência: Room/SQLite.
- Acesso a dados: Repository + DAO.
- Estado de tela: ViewModel + LiveData.
- Navegação: Navigation Component.
- Trabalho assíncrono: `ExecutorService`; WorkManager somente quando houver tarefa realmente postergável.
- Gráficos: biblioteca Android compatível com Java, preferencialmente MPAndroidChart, encapsulada atrás de componentes próprios para permitir troca futura.
- Exportação: Storage Access Framework e `FileProvider`/folha de compartilhamento quando necessário.
- Datas: `java.time` com desugaring para versões Android compatíveis.

Versão mínima recomendada: Android 8.0, API 26. O projeto deve usar a versão estável mais recente do SDK disponível no início do desenvolvimento.

## 10. Estrutura sugerida do projeto

```text
app/
  data/
    local/
      dao/
      entity/
      AppDatabase.java
    repository/
  domain/
    model/
    service/
      GoalCalculator.java
      PeriodSplitter.java
      CsvExporter.java
  ui/
    today/
    goals/
    daily/
    weekly/
    export/
    settings/
  util/
```

## 11. Requisitos não funcionais

- Todos os recursos principais devem funcionar sem internet.
- Nenhuma permissão ampla de armazenamento deve ser solicitada.
- Cálculos monetários não se aplicam; cálculos de tempo devem usar segundos inteiros para evitar erros de ponto flutuante.
- Consultas e gráficos de até cinco anos de registros devem abrir em até dois segundos em um aparelho intermediário recente.
- Banco Room com migrações versionadas; não permitir migração destrutiva em versão publicada.
- Interface e CSV inicialmente em português do Brasil.
- Acessibilidade: contraste adequado, suporte a fonte ampliada e descrições para elementos interativos.
- Privacidade: nenhum dado deve sair do aparelho, exceto quando o usuário executar uma exportação ou compartilhamento.

## 12. Casos de teste essenciais

- Registrar atividade dentro do mesmo dia.
- Registrar atividade cruzando a meia-noite e conferir a divisão nos dois dias.
- Bloquear término anterior ou igual ao início.
- Bloquear sobreposição com registro existente.
- Editar categoria ou horário e recalcular os painéis.
- Excluir registro e recalcular os painéis.
- Calcular semana completa com 168 horas.
- Calcular corretamente uma semana parcial.
- Validar metas `MINIMO` e `MAXIMO` nos dois lados do limite.
- Conferir soma das categorias e horas não classificadas.
- Exportar acentos, aspas, separadores e observações com quebra de linha.
- Abrir o CSV exportado no Excel preservando colunas e caracteres.
- Manter dados após fechar o aplicativo e reiniciar o celular.

## 13. Critérios de aceite do MVP

O MVP será considerado concluído quando:

1. O usuário puder criar, editar e excluir registros nas 12 categorias.
2. Os dados permanecerem disponíveis offline após reiniciar o aplicativo.
3. A tela de metas mostrar meta, realizado, percentual, desvio e situação para qualquer período selecionado.
4. A evolução diária mostrar horas e desvio acumulado por categoria.
5. O resumo semanal consolidar corretamente domingo a sábado.
6. O gráfico semanal comparar realizado e meta e oferecer a visão de desvio.
7. O CSV tiver as seis colunas da aba `Dados Detalhados hr`, na mesma ordem e com dados válidos.
8. Intervalos sobrepostos forem impedidos e intervalos que cruzem a meia-noite forem agregados corretamente.
9. O aplicativo não exigir conexão, conta ou serviço externo.

## 14. Evoluções futuras

- Categorias totalmente configuráveis.
- Metas diferentes por dia da semana.
- Histórico de versões das metas, para que alterações não mudem avaliações passadas.
- Importação do CSV da planilha e restauração de backup.
- Backup completo em arquivo local.
- Widgets e notificações.
- Sincronização opcional entre dispositivos.
- Detecção de lacunas e sugestão de preenchimento.
- Relatórios mensais e anuais.
- Exportação de gráficos e relatório em PDF.

## 15. Ordem recomendada de implementação

1. Banco Room, categorias iniciais e CRUD de registros.
2. Tela `Hoje` e validações de intervalos.
3. Serviço de divisão por dia e agregações.
4. Tela de metas e cálculo de desvio.
5. Resumo semanal.
6. Gráficos diário e semanal.
7. Exportação CSV.
8. Testes, acessibilidade e ajustes de desempenho.

