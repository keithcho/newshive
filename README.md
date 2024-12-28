## Description

🐝 **NewsHive** is a news feed aggregator that features a full email authentication flow without the need for passwords.

## Features

- 📈 Over 100 news sources 
- 🔍 Search for news articles 
- 🔖 Bookmarking for logged in users
- 🔒 Secure passwordless login
- 🤖 Free API endpoint for cached results

## Requirements

| Spring Boot | | Description |
| --- | --- | --- |
| Must handle HTTP POST and GET request | &check; | |
| Must include parameterized routes (`@PathVariable`) | &check; | `/category` GET endpoint uses `@PathVariable` to display various news categories |
| Must include form validation | &check; |  Form validation is present for login and email verification |
| Must include both MVC (`@Controller`) and REST endpoints (`@RestController`) | &check; | `/api` endpoint returns cached articles in JSON |
| POST must consume either ``form-urlencoded`` or JSON payload | &check; | Login POST endpoint uses ``form-urlencoded`` |
| Must support more than 1 user | &check; | |
| Must include a minimum of 3 views, not including REST endpoints | &check; | |
| **RESTful API** | | **Description** |
| Making HTTP request to external RESTful API | &check; | NewsAPI, OkSurf API and MailerSend API |
| REST endpoints must not be those that have been discussed or used in class or assessment (eg. Open Weather Map, Giphy, News, etc). You can use APIs that are discussed in class workshops or demos, but you must also include another external endpoint that has not been discussed in class. | &check; | Includes external endpoint (MailerSend API) |
| **HTML/CSS** | | **Description** |
| Use CSS to style your user interface. You may write your own CSS or use CSS frameworks like Bootstrap, Tailwind, Bulma, etc. Marks are awarded for the Look-n-Feel of your application. More information about Bootstrap is available here https://getbootstrap.com/ | &check; | Uses Bootstrap and Material Design for Bootstrap v5 |
| **Database** | | **Description** |
| Redis database must be running in the ‘cloud’. You can provision an instance from Redis Labs or from any cloud provider like Digital Ocean, AWS. | &check; | Uses Redis Cloud |
| **Deployment** | | **Description** |
| You must deploy your application to Railway or any public cloud provider. You will be using your deployed application for your presentation. If you present your application from your notebook, marks will be deducted. | &check; | Deployed to Railway using Docker |
| Spring Boot application must be deployed on JDK 23 | &check; | JDK 23 |
| Redis database must be in the 'cloud.’ | &check; | Uses Redis Cloud |

## Configuration

### Redis

This project uses a Redis Stack server as the database. Configure the database properties:

```properties
spring.data.redis.host=
spring.data.redis.port=
spring.data.redis.username=
spring.data.redis.password=
```

### Required API Keys

- [NewsAPI](https://newsapi.org/)
- [MailerSend API](https://www.mailersend.com/features/email-api)

```properties
# NewsAPI Key
secret.news-api-key=

# MailerSend API key
secret.mailersend-api-key=

# Email domain
secret.domain=
```