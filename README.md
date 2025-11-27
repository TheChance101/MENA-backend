*Backend Repository of [MENA app](https://github.com/TheChance101/MENA-mobile)*

## MENA As Brand
A super app crafted for the Middle Eastern and North African community - empowering you to do everything you need, all while nurturing a genuine sense of belonging.

<img width="50" alt="MENA" src="https://github.com/user-attachments/assets/f5b90dce-0144-4ec7-ae0d-9c964a50076b" />
 was brought to life through the passion and dedication of <a href="https://www.linkedin.com/company/thechance101/">The Chance</a> Mentorship program. Over eleven weeks, we combined hard work, creativity, and collaboration to bring this vision to life.

>
<div>
<img width="200" src="https://github.com/user-attachments/assets/72cb47fb-27a1-45ab-971b-bfef705abded"/>
</div>


## Setup
Open `application.properties` file you can find at `app\src\main\resources\application.properties`, and add these configurations:

### Environment
Set active profile to `dev` in `application.properties`
```
spring.profiles.active=dev 
```

### JWT 
Sdd your `jwt secret key` to `application.properties`
```
jwt.secret-key=${JWT_SECRET}
```

### Database
1. Install `Postgress` database on your machine
1. Add your database configuration parameters to `application.properties`
```
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER_NAME}
spring.datasource.password=${DATABASE_PASSWORD}
```

### Elastic Search
1- Create [Elastic](https://www.elastic.co/elasticsearch) account
2- Get started with Elasticsearch, choose **Create index** option
2- Add the following Elasticsearch settings to `application.properties` file:
```
spring.elasticsearch.uris=${ELASTIC_SEARCH_URIS}
spring.elasticsearch.password=${ELASTIC_SEARCH_PASSWORD}
spring.elasticsearch.username=${ELASTIC_SEARCH_USERNAME}
```

### Storage
1. we used [`Digital Ocean`](https://www.digitalocean.com) as remote file storage server. Alternative you can use [`LocalStack`](https://app.localstack.cloud/getting-started) for local simulation on your machine.
1. install [Docker](https://www.docker.com/get-started/)
1. install [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
1. install [LocalStack](https://app.localstack.cloud/getting-started)
1. Follow the guide on the local stack website to create key, secret, endpoint, cdn-enpoint(region) for MENA, Duckan, Trends, and Wallet
1. Assign `profile-image-directory` to the value `images`
```
# Storage
storage.mena.key=${STORAGE_MENA_KEY}
storage.mena.secret=${STORAGE_MENA_SECRET}
storage.mena.bucket=${STORAGE_MENA_BUCKET}
storage.mena.endpoint=${STORAGE_MENA_ENDPOINT}
storage.mena.cdn-endpoint=${STORAGE_MENA_CDN_ENDPOINT}

storage.dukan.key=${STORAGE_DUKAN_KEY}
storage.dukan.secret=${STORAGE_DUKAN_SECRET}
storage.dukan.bucket=${STORAGE_DUKAN_BUCKET}
storage.dukan.endpoint=${STORAGE_DUKAN_ENDPOINT}
storage.dukan.cdn-endpoint=${STORAGE_DUKAN_CDN_ENDPOINT}

storage.trends.key=${STORAGE_TRENDS_KEY}
storage.trends.secret=${STORAGE_TRENDS_SECRET}
storage.trends.bucket=${STORAGE_TRENDS_BUCKET}
storage.trends.endpoint=${STORAGE_TRENDS_ENDPOINT}
storage.trends.cdn-endpoint=${STORAGE_TRENDS_CDN_ENDPOINT}

storage.wallet.key=${STORAGE_WALLET_KEY}
storage.wallet.secret=${STORAGE_WALLET_SECRET}
storage.wallet.bucket=${STORAGE_WALLET_BUCKET}
storage.wallet.endpoint=${STORAGE_WALLET_ENDPOINT}
storage.wallet.cdn-endpoint=${STORAGE_WALLET_CDN_ENDPOINT}

identity.resources.profile-image-directory=images
```

## Architecture
```
.
├── app        # Application entry point, core configuration, global exception handlers
├── identity   # Authentication, authorization, user profiles, and security configuration
├── chat       # Chat and messaging features
├── dukan      # Dukan-related domain logic and services
├── faith      # Faith-related domain logic and services
├── trends     # Trends-related domain logic and analytics
├── wallet     # Wallet, payments, and financial transactions
└── events     # Event bus and cross-feature integrations
```
![MENA package hierarchy](https://github.com/user-attachments/assets/684a5e49-b3e7-43cb-9e3a-99a778ef1410)

## Technologies Used

- **Language**: Kotlin
- **Framework**: Spring Boot
- **Architecture**: MVC (Model-View-Controller) / REST API
- **Database**: PostgreSQL
- **Testing**: [JUnit 5](https://junit.org/junit5/), [MockK](https://mockk.io/), [Spring Boot Test](https://spring.io/guides/gs/testing-web)
- **Code Coverage**: [Kover](https://github.com/Kotlin/kotlinx-kover)
- **Dependency Injection**: Spring Framework IoC Container

## Contractors

- **Bareq AlTahma** – The chance instructor ([@iBareq](https://github.com/iBareq))
- **Falah Hasan** – Mentor ([@devfalah](https://github.com/devfalah))
- **Karrar Mohammed** – Mentor ([@Karrar-Mohammed](https://github.com/Karrar-Mohammed))

## Contributors

<a href="https://github.com/TheChance101/MENA-backend/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=TheChance101/MENA-backend"/>
</a>

## License:

    Copyright 2025 The Chance

    Licensed under the Apache License, Version 2.0 (the "License");
    You may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
