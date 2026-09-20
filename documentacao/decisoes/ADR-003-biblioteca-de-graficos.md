# ADR-003 — Biblioteca de gráficos

**Estado:** Aceito  
**Data:** 2026-09-19

## Contexto

O aplicativo exige gráficos de linhas e barras em Java com Views/XML, `minSdk 26`, acessibilidade e dependências compatíveis com o toolchain Android atual.

## Avaliação

### MPAndroidChart 3.1.0

- Oferece os gráficos necessários e licença Apache-2.0.
- A instalação oficial exige o repositório JitPack.
- A release publicada referencia Gradle 4.6, Android Gradle Plugin 3.2.1 e Build Tools 28.0.3, portanto não demonstra compatibilidade com o toolchain atual do projeto.

O risco de uma dependência antiga e resolvida por JitPack não é justificável para este MVP.

### AndroidPlot 1.6.0

- Oferece gráficos de linha e barras, funciona com Java e XML e suporta API 5 ou superior.
- É distribuído sob Apache-2.0.
- O artefato `com.androidplot:androidplot-core:1.6.0` está disponível no Maven Central como AAR; a resolução foi comprovada por HTTP 200 para POM e AAR.
- A dependência de runtime declarada é `androidx.annotation:annotation:1.10.0`.

## Decisão

Usar AndroidPlot 1.6.0, adicionado na F01 exclusivamente por `mavenCentral()`:

```gradle
implementation "com.androidplot:androidplot-core:1.6.0"
```

Não adicionar JitPack. A integração deve ficar atrás de componentes próprios em `ui/chart/`:

- modelos de dados imutáveis e independentes da biblioteca;
- uma interface de renderização por tipo de gráfico;
- adaptadores AndroidPlot sem lógica de domínio;
- tabela ou resumo textual equivalente para cada gráfico.

ViewModels devem expor somente modelos de gráfico e dados textuais acessíveis, nunca tipos AndroidPlot.

## Consequências

- F01 inclui AndroidPlot, mas a compilação e a resolução da dependência precisam passar em `assembleDebug`.
- F06 e F07 implementam linha e barras por adaptadores próprios e testes de transformação de dados.
- A acessibilidade não depende do canvas do gráfico: cada tela mantém alternativa textual navegável.
