-- Sample books
INSERT INTO books (title, author, isbn, genre, publisher, publication_year, total_copies, available_copies)
VALUES
  ('The Great Gatsby', 'F. Scott Fitzgerald', '978-0-7432-7356-5', 'Fiction', 'Scribner', 1925, 3, 3),
  ('To Kill a Mockingbird', 'Harper Lee', '978-0-06-112008-4', 'Fiction', 'J. B. Lippincott', 1960, 2, 2),
  ('1984', 'George Orwell', '978-0-452-28423-4', 'Dystopian', 'Secker & Warburg', 1949, 4, 4),
  ('Pride and Prejudice', 'Jane Austen', '978-0-14-143951-8', 'Romance', 'T. Egerton', 1813, 2, 2),
  ('The Hobbit', 'J.R.R. Tolkien', '978-0-618-00221-3', 'Fantasy', 'Allen & Unwin', 1937, 3, 3),
  ('Harry Potter and the Sorcerer''s Stone', 'J.K. Rowling', '978-0-590-35340-3', 'Fantasy', 'Bloomsbury', 1997, 5, 5),
  ('The Da Vinci Code', 'Dan Brown', '978-0-385-50420-5', 'Thriller', 'Doubleday', 2003, 3, 3),
  ('Clean Code', 'Robert C. Martin', '978-0-13-235088-4', 'Technology', 'Prentice Hall', 2008, 2, 2),
  ('Effective Java', 'Joshua Bloch', '978-0-13-468599-1', 'Technology', 'Addison-Wesley', 2018, 2, 2),
  ('The Alchemist', 'Paulo Coelho', '978-0-06-231500-7', 'Fiction', 'HarperOne', 1988, 3, 3);

-- Sample members
INSERT INTO members (name, email, phone, address, membership_date, status)
VALUES
  ('Alice Johnson', 'alice@example.com', '555-0101', '123 Maple St', CURRENT_DATE, 'ACTIVE'),
  ('Bob Smith', 'bob@example.com', '555-0102', '456 Oak Ave', CURRENT_DATE, 'ACTIVE'),
  ('Carol White', 'carol@example.com', '555-0103', '789 Pine Rd', CURRENT_DATE, 'ACTIVE'),
  ('David Brown', 'david@example.com', '555-0104', '321 Elm Dr', CURRENT_DATE, 'ACTIVE'),
  ('Eve Davis', 'eve@example.com', '555-0105', '654 Birch Ln', CURRENT_DATE, 'INACTIVE');
