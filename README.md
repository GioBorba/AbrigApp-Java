#  AbrigApp 

O **AbrigApp** é uma aplicação web desenvolvida em Java com o objetivo de ajudar pessoas a encontrarem abrigos seguros durante eventos extremos, como enchentes, ondas de calor e outros desastres naturais.

Ao acessar o sistema, você poderá visualizar uma lista de abrigos disponíveis, com seus respectivos endereços e localizações geográficas. Usuários autenticados podem avaliar os abrigos com notas de 1 a 5 e também interagir com um assistente inteligente via chat, integrado com tecnologias da OpenAI.

---

## ✨ Funcionalidades

- 🗺️ Listagem de abrigos com endereço e localização
- 🔐 Autenticação com OAuth2 (Google)
- ⭐ Avaliações de abrigos por usuários autenticados
- 🧠 Integração com assistente inteligente (OpenAI - Spring AI)
- 🌐 Suporte a múltiplos idiomas (Português e Inglês)
- 📩 Notificações com RabbitMQ ao cadastrar novos abrigos

---

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot**
- **Spring MVC**
- **Thymeleaf**
- **MongoDB Atlas** (Banco de Dados)
- **RabbitMQ** (Mensageria)
- **Spring Security + OAuth2** (Login com Google)
- **Spring AI** (Integração com OpenAI)
- **Internacionalização (i18n)** para inglês e português

---

## 📦 Como executar o projeto

### Pré-requisitos

- Java 21
- Maven
- MongoDB Atlas (ou instância local)
- RabbitMQ
- Conta Google para login OAuth2

### Passos

1. Clone o repositório:
   git clone https://github.com/GioBorba/AbrigApp-Java.git
   cd AbrigApp-Java

2. Configure o arquivo `application.properties` com as credenciais do MongoDB, OAuth2 e RabbitMQ.

3. Execute o projeto:
   mvn spring-boot:run

---

## 📲 Telas disponíveis (via Thymeleaf)

- Página inicial pública
- Lista de abrigos
- Detalhes e avaliações de cada abrigo
- Área administrativa para cadastro de abrigos (restrita a ADMIN)
- Chat com assistente inteligente

---


