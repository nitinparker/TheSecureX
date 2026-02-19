# Stage 1: Build
FROM golang:1.21-alpine AS build
WORKDIR /app
# Install libpcap for network sniffing
RUN apk add --no-cache libpcap-dev gcc musl-dev
COPY go.mod ./
RUN go mod download
COPY . .
RUN go build -o network-tool .

# Stage 2: Runtime
FROM alpine:latest
RUN apk add --no-cache libpcap
WORKDIR /app
COPY --from=build /app/network-tool .
# Create vault directory
RUN mkdir -p /data/vault
EXPOSE 8002
CMD ["./network-tool"]