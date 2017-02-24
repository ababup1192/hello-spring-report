package org.ababup1192;

import org.ababup1192.book.after.Book;
import org.ababup1192.book.after.BookService;
import org.ababup1192.book.after.Category;
import org.ababup1192.book.before.BookCategory;
import org.ababup1192.book.before.BookCategoryRepository;
import org.ababup1192.book.BookCategoryMigrateService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BookCategoryMigrationTest {
    @Autowired
    private BookCategoryRepository bookCategoryRepository;
    @Autowired
    private BookCategoryMigrateService bookCategoryMigrateService;
    @Autowired
    private BookService bookService;

    private static final String title1 = "The Mythical Man-Month: Essays on Software Engineering";
    private static final String title2 = "Clean Code";
    private static final String title3 = "Domain-Driven Design: Tackling Complexity in the Heart of Software";
    private static final String title4 = "Refactoring";
    private static final String title5 = "INTRODUCTION TO ALGORITHMS";
    // Initial data
    private static final List<BookCategory> bookCategories = Arrays.asList(
            new BookCategory(title1, "SE"),
            new BookCategory(title2, "Coding"),
            new BookCategory(title3, "SE"),
            new BookCategory(title4, "Coding"),
            new BookCategory(title5, "Algorithm")

    );

    @Before
    public void SetUp() {
        bookCategoryRepository.truncateTable();

        bookCategoryRepository.save(bookCategories);
        bookCategoryMigrateService.migrate();
    }

    // Comment out this annotation if you check migrateTest only!!
    // @After
    public void tearDown() {
        bookService.dropBook();
    }

    @Test
    public void migrateTest() {
        List<Book> allBooks = bookService.findAll();

        assertThat(allBooks, contains(
                new Book(1, title1, new Category(1, "SE")),
                new Book(2, title2, new Category(2, "Coding")),
                new Book(3, title3, new Category(1, "SE")),
                new Book(4, title4, new Category(2, "Coding")),
                new Book(5, title5, new Category(3, "Algorithm")))
        );
    }

    @Test
    public void joinQueryTest() throws Exception {
        List<Book> havingForkRooms = bookService.findByCategoryName("Coding");

        assertThat(havingForkRooms, hasItems(
                new Book(2, title2, new Category(2, "Coding")),
                new Book(4, title4, new Category(2, "Coding"))
        ));
    }
}
