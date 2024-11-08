# OnlineLibrary


## Overview

This project is a Book Management Service that allows users to add books with details such as title, author, description, and associated files (PDFs and images). It provides a RESTful API with validation for input data, ensuring that only properly formatted information is processed.

## Features

- **Add Books**: Users can add new books with metadata, including book name, author name, description, and file uploads (PDF and image formats).
- **Validation Using Custom Annotations**: Ensures that all inputs meet specified criteria (e.g., non-empty fields, valid file types).
- **Error Handling**: Provides meaningful error messages in response to validation failures.
- **File Uploads**: Supports uploading PDF files for books and image files for cover art in supported formats (JPG, JPEG, PNG).

## Technologies Used

- **Java**: The primary programming language used.
- **Spring Boot**: Framework for building the RESTful API.
- **Jakarta Validation**: For validating user input.
- **Maven**: For dependency management and building the project.
- **Lombok**: For reducing boilerplate code in data classes.

## Getting Started

To run this project locally, follow these steps:

1. **Clone the Repository**:
  
  https://github.com/akashdas031/OnlineLibrary.git

2. Import the project to ypur Favorite IDE like Eclipse,STS,Intellij,NetBeans,VsCode etc
3. Run as Spring boot application
4. Java Version 17 is used and Spring boot version 3.3.5 is used

5. Request Format For PostMan :
    Base URL - localhost:6574/book/v1/
    Params -book,file(for book in pdf format),imageFile(for image of the book)
    - book ={
    "bookName" : "Head First Java",
    "description" : "Head First Java is an engaging and beginner-friendly guide that introduces the fundamentals of Java programming.",
    "authorName" : "Kathe Siera" }
    -  file :A pdf file for the book
    - imageFile : A image file of type JPG,JPEG and PNG
  

End Points :
- To Add a Book : localhost:6574/book/v1/addBook
- To get All Books : localhost:6574/book/v1/getAllBooks
- To get Single Book : localhost:6574/book/v1/getSingleBook/< YOUR BOOK ID >
- To Update a Book : localhost:6574/book/v1/updateBook/<Your Book ID >
- To Delete a Book : localhost:6574/book/v1/deleteBook/<Your Book ID >
- To search a book : localhost:6574/book/v1/<search Keyword>
- Search KeyWord Must contain id,bookname,authorname,booktype,genre
- http://localhost:6574/book/v1/booksByType/ (Required Params -bookType,page,pageSize)
- To create a User : localhost:6575/onlineLibrary/user/v1/createUser (Pass the parameters according to the Controller)
