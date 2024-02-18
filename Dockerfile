FROM --platform=linux/amd64 ghcr.io/graalvm/native-image-community:21 AS builder

# Set the working directory to /home/app
WORKDIR /build

# Copy the source code into the image for building
COPY . /build

# Build
RUN ./mvnw --no-transfer-progress native:compile -Pnative

# The deployment Image
FROM alpine:latest

RUN apk add gcompat libstdc++

EXPOSE 8080

# Copy the native executable into the containers
COPY --from=builder /build/target/rinha-2024-q1 app
ENTRYPOINT ["/app"]