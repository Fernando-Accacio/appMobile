# GoBarber - Agendamento de Barbearia 💈📱

GoBarber é um aplicativo mobile desenvolvido para modernizar o processo de agendamento de serviços em barbearias, conectando clientes e barbeiros de forma prática e eficiente.

> ✅ **Disponível na Google Play Store**
> 📦 `Package Name`: `visionary.gobarber`
> 🛠️ Versão Atual: `1.0.8`

---

## 📱 Funcionalidades

### Para Clientes:

* Registro, login e recuperação de senha
* Visualização de barbeiros por localização
* Agendamento de serviços (cabelo, barba, sobrancelha)
* Acompanhamento do status dos agendamentos
* Histórico e possibilidade de alterar agendamentos

### Para Barbeiros:

* Cadastro e gerenciamento de loja
* Definição de serviços e horários de atendimento
* Visualização, confirmação e cancelamento de agendamentos

---

## 🧱 Arquitetura

* **Padrão**: MVC (Model-View-Controller)
* **Frontend**: Android Nativo (Java + XML)
* **Backend/Persistência**: Firebase (Firestore, Auth, Storage, Messaging)
* **Testing**: JUnit 4, Espresso, Mockito
* **Build**: Gradle com Kotlin DSL
* **Análise**: Firebase Analytics + JaCoCo (code coverage)

---

## 🚀 Instalação

### Pré-requisitos

* Android Studio 4.0+
* JDK 11+
* Android SDK (API 24+)
* Conta no Firebase

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/Emilio133752/appMobile.git
cd appMobile

# 2. Adicione o arquivo de configuração do Firebase
# (google-services.json em app/)

# 3. Configure o keystore (para builds de release)
# Crie um arquivo keystore.properties com os campos necessários

# 4. Execute o projeto
./gradlew assembleDebug
./gradlew installDebug
```

---

## 📦 Build para Produção

```bash
# APK
./gradlew assembleRelease

# Android App Bundle (recomendado para Play Store)
./gradlew bundleRelease
```

---

## 🔍 Estrutura do Projeto

```
appMobile/
├── app/
│   ├── src/main/java/com/example/app/
│   │   ├── controller/   # Lógica e adaptação de views
│   │   ├── model/        # Entidades e validações
│   │   └── view/         # Activities (UI)
│   ├── res/              # Layouts, imagens, valores
│   └── AndroidManifest.xml
├── build.gradle.kts
├── keystore.properties
└── README.md
```

---

## ☁️ Firebase

### Coleções no Firestore:

* `users/` → dados dos usuários
* `barbeiros/` → informações da loja
* `agendamentos/` → registros de agendamento

### Segurança:

* Leitura e escrita restrita por `uid`
* Agendamentos visíveis apenas por envolvidos

---

## 🛠️ Comandos Úteis

```bash
./gradlew clean                      # Limpar projeto
./gradlew test                       # Rodar testes unitários
./gradlew connectedAndroidTest       # Testes instrumentados
./gradlew jacocoTestReportAndroid    # Cobertura de testes
```

---

## 📊 Monitoramento e Analytics

* Firebase Analytics integrado
* Rastreamento de eventos críticos
* Firebase Crashlytics para erros
* Métricas de performance (tempo de tela, carregamento etc.)

Seja bem vindo ao GoBarber 💈
