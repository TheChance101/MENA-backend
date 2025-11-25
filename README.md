# MENA project
Backend Repository of MENA app

## Branding

<img width="365" height="365" alt="mena_logo" src="https://github.com/user-attachments/assets/72cb47fb-27a1-45ab-971b-bfef705abded" />

A super app crafted for the Middle Eastern and North African community - empowering you to do everything you need, all while nurturing a genuine sense of belonging.

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
