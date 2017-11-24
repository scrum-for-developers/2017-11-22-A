Meta:
@themes Book

Narrative:
In order to add new books to the library
As a librarian
I want to add books through the website

Scenario:

Given an empty library
When a librarian adds a book with title <title>, author <author>, edition <edition>, year <year> and isbn <isbn>
Then The booklist contains a book with values title <title>, author <author>, year <year>, edition <edition>, isbn <isbn>

Examples:

{trim=false}
| isbn       | author           | title     | edition   | year  |
| 0552131075 |Terry Pratchett|Sourcery|1|1989|
|0552131075|Terry Pratchett|Sourcery| 1         |1989|


Scenario:

Given a library, containing a book with isbn <isbn>
When a librarian adds a book with title <title>, author <author>, edition <edition>, year <year> and isbn <isbn>
Then The booklist contains a book with values title <title>, author <author>, year <year>, edition <edition>, isbn <isbn>

Examples:

| isbn       | author           | title     | edition   | year  |
| 0552131075 | Terry Pratchett  | Sourcery  |2          | 1989  |

Scenario:

Given a library, containing a book with isbn <isbn>
When a librarian adds a book with title <title>, author <author>, edition <edition>, year <year> and isbn <isbn>
Then the library contains only the book with <isbn>

Examples:

| isbn       | author           | title     | edition   | year  |
| 0552131075 | Terry Pratchett  | Sourcery  |2          | 1989  |







