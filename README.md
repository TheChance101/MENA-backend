
<p align="center">
    <img width="1440" height="744" alt="MENA_COVER" src="https://github.com/user-attachments/assets/6401c4d7-392b-405d-979f-39452775cefe" />
</p>
<p>
    <b>MENA</b> A super-chat application built for the Middle East and North Africa region.
    <br><br>
    MENA is a next-generation super-chat platform designed specifically for the culture and needs of the Middle East and North Africa. The app enables seamless text and (voice-messaging) communication, along with the ability to send gifts using a blockchain-based digital currency called Silver.
In addition to messaging, MENA empowers small businesses by providing a dedicated space to showcase their products directly to users across the region, with in-app purchasing fully supported.
The platform also includes a comprehensive faith section, allowing users to read and share the Qur’an.
To complement the social experience, MENA offers a trends hub where users can watch and share short-form videos, creating a dynamic and culturally relevant digital community.
    <br><br>
</p>

## Mobile App Repository
https://github.com/TheChance101/MENA-mobile)

---


## Setup your backend server
Open `application.properties` file you can find at `app\src\main\resources\application.properties`, and add these configurations:

### Environment
Set active profile to `dev` in `application.properties`
```
spring.profiles.active=dev 
```

### JWT 
Add your `jwt secret key` to `application.properties`
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

### Storage
We used [`Digital Ocean`](https://www.digitalocean.com) as a remote file storage server
1. **Create Digital Ocean account**
2. **Create a space bucket** \
   Please follow the guide here ([How to Create a Spaces Bucket](https://docs.digitalocean.com/products/spaces/how-to/create/)), We create a bucket for each feature
3. **Configure Application Properties** \
   Add the following bucket configuration settings to your `application.properties` file.  
```
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

**Alternatively, you can use [`LocalStack`](https://app.localstack.cloud/getting-started) for local simulation on your machine.**
1. Install [Docker](https://www.docker.com/get-started/)
1. Install [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
1. Install [LocalStack](https://app.localstack.cloud/getting-started)
1. Follow the guide on the local stack website to create a key, secret, endpoint, CDN endpoint (region) for MENA, Duckan, Trends, and Wallet
1. Assign `profile-image-directory` to the value `images`


### Elastic Search
1. **Sign up or log in at [Elasticsearch](https://www.elastic.co/elasticsearch) website**  
2. **Configure Application Properties**  
   Add the following Elasticsearch configuration settings to your `application.properties` file.  
```
spring.elasticsearch.uris=${ELASTIC_SEARCH_URIS}
spring.elasticsearch.password=${ELASTIC_SEARCH_PASSWORD}
spring.elasticsearch.username=${ELASTIC_SEARCH_USERNAME}
```
- **ELASTIC_SEARCH_URIS** → Address of your Elasticsearch server (example: http://localhost:9200 or your cloud URL)
- **ELASTIC_SEARCH_USERNAME** → Username for Elasticsearch
- **ELASTIC_SEARCH_PASSWORD** → Password for Elasticsearch

**Alternatively, if you need to test it as a local simulation, you can use Docker.**
1. You need to install Docker and [`elastic search docker image`](https://hub.docker.com/_/elasticsearch)
2. Open the terminal and run this command with your desired ports, for example, port1 `8088` and port2 `8089`:
```
docker run `
  --name elastic_search `
  -p [port1]:9200 `
  -p [port2]:9300 `
  --env xpack.security.enabled=false `
  --env xpack.security.http.ssl.enabled=false `
  --env discovery.type=single-node `
  -d elasticsearch:9.2.1
```
3. **Configure Application Properties**  
```
spring.elasticsearch.uris=${ELASTIC_SEARCH_URIS}
spring.elasticsearch.password=${ELASTIC_SEARCH_PASSWORD}
spring.elasticsearch.username=${ELASTIC_SEARCH_USERNAME}
```
- **ELASTIC_SEARCH_URIS** → http://localhost:[port1], http://localhost:[port2]
- **ELASTIC_SEARCH_USERNAME** → empty string
- **ELASTIC_SEARCH_PASSWORD** → empty string

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
