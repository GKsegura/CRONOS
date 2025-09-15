(_English version below_)
# CRONOS

![Java](https://img.shields.io/badge/Java-17+-blue.svg)
![SQLite](https://img.shields.io/badge/SQLite-embutido-green.svg)
![Licença: MIT](https://img.shields.io/badge/Licença-MIT-yellow.svg)
![Status](https://img.shields.io/badge/status-ativo-success.svg)

**CRONOS** é uma aplicação de console para **registrar e gerar relatórios do trabalho diário de suporte**.  
Registre suas jornadas, horários de trabalho e almoço, tarefas, categorias, e gere **relatórios automáticos em Markdown**.

## ✨ Funcionalidades

- ⏰ Registrar **dias de trabalho** (início, almoço, término)
- 📝 Adicionar **tarefas** com categoria, cooperativa e duração
- 📊 Calcular **horas líquidas trabalhadas** (descontando almoço)
- 💾 Armazenamento **local via SQLite (`database.db`)**
- 📂 Gerar **relatórios em Markdown** dentro de `relatorios/`
- 🖥️ Menu de **console interativo** para gerenciar os registros  

---

## 📂 Estrutura do Projeto

```
src/
├── application/
│   └── Main.java
├── database/
│   ├── SQLiteConnection.java
│   └── TableCreator.java
├── entities/
│   ├── Categoria.java
│   ├── Dia.java
│   └── Task.java
├── repository/
│   └── DiaRepository.java
└── service/
    ├── DiaService.java
    ├── RelatorioService.java
    └── TarefaService.java
```

---

## 🚀 Como Executar

### Pré-requisitos
- Java 17+  
- SQLite (já embutido, não requer configuração externa)  

### Rodar
```bash
git clone https://github.com/GKsegura/CRONOS.git
cd CRONOS
mvn install
mvn compile exec:java
```

---

## 📑 Relatórios

Relatórios são salvos como **arquivos Markdown** dentro da pasta `relatorios/`.  
Exemplo:  

```
relatorio_2025-09-15_segunda-feira.md
```

E o conteúdo fica assim:

```markdown
# Relatório do Dia 15/09/2025

## Horários
- Início do trabalho: 08:00
- Início do almoço: 12:00
- Fim do almoço: 14:00
- Fim do trabalho: 18:00
- Total de horas trabalhadas: 08:00

## Tarefas por Categoria
### SUPORTE
- Chamado 9999 [SUPORTE] [cooperativa] [0h00m]

### REUNIAO [1h00m]
- Reunião [REUNIAO] [cooperativa] [1h00m]

### SUPORTE_HORAS_PAGAS
- Chamado 9090 [SUPORTE_HORAS_PAGAS] [cooperativa] [1h00m]

### DESPESA_GERAL
- Laboral [DESPESA_GERAL] [cooperativa] [0h15m]
```

---

## ⚖️ Licença
Licença MIT.  

---
English version
# CRONOS

![Java](https://img.shields.io/badge/Java-17+-blue.svg)
![SQLite](https://img.shields.io/badge/SQLite-embutido-green.svg)
![Licença: MIT](https://img.shields.io/badge/Licença-MIT-yellow.svg)
![Status](https://img.shields.io/badge/status-ativo-success.svg)

**CRONOS** is a console application to **log and generate daily support work reports**.  
Track your workdays, start/end/lunch times, tasks, categories, and generate **automatic Markdown reports**.

## ✨ Features

- ⏰ Log **workdays** (start, lunch, end)
- 📝 Add **tasks** with category, cooperative, and duration
- 📊 Calculate **net worked hours** (excluding lunch)
- 💾 Local storage via SQLite (`database.db`)
- 📂 Generate **Markdown reports** in the `relatorios/` folder
- 🖥️ Interactive **console menu** to manage records

---

## 📂 Project Structure

```
src/
├── application/
│   └── Main.java
├── database/
│   ├── SQLiteConnection.java
│   └── TableCreator.java
├── entities/
│   ├── Categoria.java
│   ├── Dia.java
│   └── Task.java
├── repository/
│   └── DiaRepository.java
└── service/
    ├── DiaService.java
    ├── RelatorioService.java
    └── TarefaService.java
```

---

## 🚀 How to Run

### Previous requirements
- Java 17+  
- SQLite (embedded, no external setup required)

### Run
```bash
git clone https://github.com/GKsegura/CRONOS.git
cd CRONOS
mvn install
mvn compile exec:java
```

---

## 📑 Reports

Reports are saved as **Markdown files** in the `relatorios/` folder.  
Example:

```
relatorio_2025-09-15_segunda-feira.md
```

E o conteúdo fica assim:

```markdown
# Report for 15/09/2025

## Hours
- Work start: 08:00
- Lunch start: 12:00
- Lunch end: 14:00
- Work end: 18:00
- Total worked hours: 08:00

## Tasks by Category
### SUPPORT
- Ticket 9999 [SUPPORT] [cooperative] [0h00m]

### MEETING [1h00m]
- Meeting [MEETING] [cooperative] [1h00m]

### PAID_SUPPORT_HOURS
- Ticket 9090 [PAID_SUPPORT_HOURS] [cooperative] [1h00m]

### GENERAL_EXPENSE
- Labor [GENERAL_EXPENSE] [cooperative] [0h15m]
```

---

## ⚖️ License
MIT License
