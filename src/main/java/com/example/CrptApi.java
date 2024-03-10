package com.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CrptApi {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Semaphore requestSemaphore;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.requestSemaphore = new Semaphore(requestLimit);

        long period = switch (timeUnit) {
            case SECONDS -> 1;
            case MINUTES -> 60;
            case HOURS -> 3600;
            default -> throw new IllegalArgumentException("Неподдерживаемая единица измерения времени");
        };

        new java.util.Timer(true).scheduleAtFixedRate(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        requestSemaphore.release(requestLimit - requestSemaphore.availablePermits());
                    }
                },
                0, TimeUnit.SECONDS.toMillis(period)
        );
    }

    public void createDocument(String apiUrl, Document document, String signature) {
        try {
            requestSemaphore.acquire();

            String requestBody = objectMapper.writeValueAsString(document);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Код ответа: " + response.statusCode());
            System.out.println("Тело ответа: " + response.body());

            if (response.statusCode() == 401) {
                System.out.println("""
                    --------------------------------------------------------------------------
                    Шаблон успешного результата:""");
                int responseCode = 0;
                String description = "описание", participantInn = "ИНН участника", participantString = "строка участника";
                String docId = "идентификатор", docStatus = "статус", docType = "тип";
                int inStock = 109;
                boolean importRequest = true;
                String ownerInn = "ИНН владельца", participant_Inn = "ИНН_участника", producerInn = "ИНН производителя";
                LocalDate production_date= LocalDate.now();
                String productionType = "тип производства", certificate_document = "документ сертификата";
                LocalDate certificate_document_date = LocalDate.now();
                String certificate_document_number = "номер сертификата";
                int thved_code = 0;
                String uit_code = "код интеграции";
                LocalDate regDate = LocalDate.now();
                String regNumber = "номер регистрации";
                
                String responseBody = "{\"" + description + "\": {\"" + participantInn + "\": \"" + participantString
                + "\"}, " + "\"doc_Id" + "\": \"" + docId + "\", " + "\"doc_status" + "\": \"" + docStatus + "\", " + "\"doc_type"
                + "\": \"" + docType + "\", " + "\"" + "inStock" + "\": \"" + inStock + "\", \"importRequest" + "\": \""
                + importRequest + "\", " + "\"owner_inn" + "\": \"" + ownerInn + "\", " + "\"participant_inn" + "\": \""
                + participant_Inn + "\", " + "\"producer_inn" + "\": \"" + producerInn + "\", " + "\"production_date" + "\": \""
                + production_date + "\", " + "\"production_type" + "\": \"" + productionType + "\", " + "\"products"
                + "\": [ {\"certificate_document" + "\": \"" + certificate_document + "\", " + "\"certificate_document_date"
                + "\": \"" + certificate_document_date + "\", " + "\"certificate_document_number" + "\": \""
                + certificate_document_number + "\", " + "\"owner_inn" + "\": \"" + ownerInn + "\", " + "\"producer_inn"
                + "\": \"" + producerInn + "\", " + "\"production_date" + "\": \"" + production_date + "\", " + "\"thved_code" + "\": \""
                + thved_code + "\", " + "\"uit_code" + "\": \"" + uit_code + "\"} ], " + "\"reg_date" + "\": \"" + regDate + "\", "
                + "\"regNumber" + "\": \"" + regNumber + "\"}";

                String text = responseBody;
                File file = new File("src/main/scripts/response.json");
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter
                (new FileOutputStream(file), "UTF-8"));
                writer.write(text);
                writer.close();

                // Форматированный вывод
                System.out.println ("Код ответа: " + responseCode);
                System.out.println ("Тело ответа: " + responseBody);
               
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            requestSemaphore.release();
        }
    }

        public static class Document {
            @JsonProperty("description")
            private final Map<String, String> description;

            @JsonProperty("doc_id")
            private final String docId;

            @JsonProperty("doc_status")
            private final String docStatus;

            @JsonProperty("doc_type")
            private final String docType;

            @JsonProperty("importRequest")
            private final boolean importRequest;

            @JsonProperty("owner_inn")
            private final String ownerInn;

            @JsonProperty("participant_inn")
            private final String participantInn;

            @JsonProperty("producer_inn")
            private final String producerInn;

            @JsonProperty("production_date")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            private final LocalDate productionDate;

            @JsonProperty("production_type")
            private final String productionType;

            @JsonProperty("products")
            private final List<Product> products;

            @JsonProperty("reg_date")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            private final LocalDate regDate;

            @JsonProperty("reg_number")
            private final String regNumber;


        public Document(Map<String, String> description, String docId, String docStatus, String docType,
        boolean importRequest, String ownerInn, String participantInn, String producerInn, LocalDate productionDate,
        String productionType, List<Product> products, LocalDate regDate, String regNumber) {
            this.description = description;
            this.docId = docId;
            this.docStatus = docStatus;
            this.docType = docType;
            this.importRequest = importRequest;
            this.ownerInn = ownerInn;
            this.participantInn = participantInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.productionType = productionType;
            this.products = products;
            this.regDate = regDate;
            this.regNumber = regNumber;
        }
        public Document() {
            this.description = null;
            this.docId = null;
            this.docStatus = null;
            this.docType = null;
            this.importRequest = false;
            this.ownerInn = null;
            this.participantInn = null;
            this.producerInn = null;
            this.productionDate = null;
            this.productionType = null;
            this.products = null;
            this.regDate = null;
            this.regNumber = null;
        }
    }
        public static class Product {
            @JsonProperty("certificate_document")
            private final String certificateDocument;

            @JsonProperty("certificate_document_date")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            private final LocalDate certificateDocumentDate;

            @JsonProperty("certificate_document_number")
            private final String certificateDocumentNumber;

            @JsonProperty("owner_inn")
            private final String ownerInn;

            @JsonProperty("producer_inn")
            private final String producerInn;

            @JsonProperty("production_date")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            private final LocalDate productionDate;

            @JsonProperty("tnved_code")
            private final String tnvedCode;

            @JsonProperty("uit_code")
            private final String uitCode;

            public Product(String certificateDocument, LocalDate certificateDocumentDate, String certificateDocumentNumber,
            String ownerInn, String producerInn, LocalDate productionDate, String tnvedCode, String uitCode, String uituCode) {
                this.certificateDocument = certificateDocument;
                this.certificateDocumentDate = certificateDocumentDate;
                this.certificateDocumentNumber = certificateDocumentNumber;
                this.ownerInn = ownerInn;
                this.producerInn = producerInn;
                this.productionDate = productionDate;
                this.tnvedCode = tnvedCode;
                this.uitCode = uitCode;

            }
            public Product() {
                this.certificateDocument = null;
                this.certificateDocumentDate = null;
                this.certificateDocumentNumber = null;
                this.ownerInn = null;
                this.producerInn = null;
                this.productionDate = null;
                this.tnvedCode = null;
                this.uitCode = null;
            }

        }

    public static void main (String[] args) {
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 5);
        Document document = new Document();
        String apiUrl = "https://ismp.crpt.ru/api/v3/lk/documents/create";
        String signature = "test_signature_string";
        crptApi.createDocument(apiUrl, document, signature);
    }
}