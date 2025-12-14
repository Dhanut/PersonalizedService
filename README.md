🛒 Personalized Shopper Application

A Spring Boot backend service that manages product metadata and personalized shopper shelves.
The system supports internal APIs for data ingestion and external APIs for retrieving personalized product recommendations with filtering and pagination.

📌 Features

✅ Product Metadata Management

Create product metadata (productId, category, brand)

Update existing product metadata

Prevent duplicate product entries


✅ Shopper Shelf Management

Create a personalized shelf for a shopper

Update shelf by:

Inserting new products

Updating relevancy scores

Skipping invalid products

Validates product existence before saving

✅ Product Retrieval

Fetch shopper products with:

Category filter

Brand filter

Pagination support

Optimized queries using pagination and joins

🧱 Tech Stack

Java 17

Spring Boot

Spring Data JPA

Hibernate

RESTful APIs

SLF4J Logging

MySQL

Maven

📂 Project Structure


<img width="553" height="348" alt="image" src="https://github.com/user-attachments/assets/89d1c831-02b4-404d-a8e3-5ff1ff68c106" />


▶️ Running the Application

mvn clean install

mvn spring-boot:run

The application will start at:

http://localhost:8080

🧪 Error Handling & Validation

Prevents duplicate product metadata

Validates shopperId and productId existence

Graceful error responses with meaningful messages

Transactional consistency for shelf updates

🚀 Future Enhancements

Authentication & Authorization

Caching for frequent shopper queries

Bulk product metadata ingestion

Swagger / OpenAPI documentation

👨‍💻 Author

Assignment Implementation – Personalized Shopper Backend
