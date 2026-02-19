FROM golang:1.21 AS build
WORKDIR /app
COPY . .
RUN go build -o sniffer sniffer.go

FROM debian:bookworm-slim
WORKDIR /app
COPY --from=build /app/sniffer .
CMD ["./sniffer"]
