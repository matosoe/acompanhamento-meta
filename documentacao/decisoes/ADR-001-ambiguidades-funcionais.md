# ADR-001 — Ambiguidades funcionais do MVP

**Estado:** Aceito  
**Data:** 2026-09-19

## Contexto

O MVP precisa fixar o formato CSV, a identificação semanal, o efeito temporal das metas, o escopo do cronômetro e os limites de período para que dados, cálculos e interface usem a mesma regra.

## Decisões

### Observações e CSV

A versão inicial não apresenta nem coleta observações nos registros. O CSV V1 possui exatamente seis colunas, nesta ordem: `Data`, `Hora de Início`, `Hora de Término`, `Semana`, `Duração Real` e `Projeto`. Uma futura versão poderá introduzir observações como alteração explícita de formato.

### Semana sequencial

A semana padrão começa no domingo e termina no sábado. O campo CSV `Semana` é um identificador sequencial calculado a partir de domingo, 2021-11-07, definido como semana 1:

```text
semana = 1 + número de semanas completas entre 2021-11-07 e o domingo de início da semana do registro
```

Essa convenção reproduz o exemplo especificado: o domingo 2026-09-06 inicia a semana 253. O identificador não é digitável.

### Metas trimestrais

As metas são snapshots por trimestre operacional, não valores históricos mutáveis. Cada trimestre termina no primeiro sábado em ou após o último dia do trimestre civil; o período seguinte começa no domingo seguinte. Portanto, um trimestre é uma sequência domingo–sábado, normalmente com 13 semanas.

Ao alterar uma meta, a alteração vale somente para o trimestre operacional atual. Trimestres encerrados permanecem imutáveis. Ao abrir um trimestre novo, o aplicativo cria seus snapshots a partir dos valores vigentes no trimestre anterior; alterações posteriores atingem apenas o novo trimestre.

### Soma das metas

As metas iniciais das categorias somam sempre 10.000 basis points (100%). Elas representam a totalidade do tempo disponível em uma semana. Esta decisão confirma o total inicial de 100% adotado pela especificação e pelo plano.

### Fuso e intervalos

Todos os cálculos de dias, semanas e trimestres usam o fuso local do aparelho. Os intervalos internos são semiabertos: `[início, fim)`.

### Cronômetro

O cronômetro está fora do MVP. A F09 não será executada para a primeira versão e não deve introduzir estado ou interface de cronômetro antes de uma nova decisão de produto.

## Consequências

- O banco precisará de um modelo de metas por trimestre antes da tela de metas, preservando snapshots de períodos fechados.
- Cálculos e exportação devem usar o identificador semanal definido acima, mesmo que o primeiro dia de exibição seja configurável.
- O formulário de registros e o CSV V1 não terão campo de observação.
- A navegação, os testes e a matriz de aceite não incluirão cronômetro no MVP.
- O seed de categorias e suas validações devem exigir soma de 10.000 basis points.
