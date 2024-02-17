# Rinha de Backend, Segunda Edição: 2024/Q1

https://github.com/edmilson1968/rinha-2024-q1


## Criando a native-image

Super fácil pois estou usando o plugin do packeto https://github.com/dashaun/paketo-arm64

```
mvn clean package -Pnative spring-boot:build-image
```