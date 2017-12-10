## A tool for converting a primitive imperative language to C++
### Syntax
* expressions are separated by a newline
* all the variables are integers
* all the variables are global
* anything matching `[a-zA-Z_][a-zA-Z0-9_]*` is considered as a variable name
* `read` and `print` keywords are used to read from standart input and write to standart output corresponsively
* `read` is used as an expression: `x = read`
* `print` writes everything that comes after it through listing: `print x, 2`
* variables can be assigned through listing, for example: `x, y, z = 1, read, x`
* `if` clauses syntax : 
  ```
  if <cond>:
    <...>
  .
  ```
  ```
  if <cond>:
    <...>
  else:
    <...>
  .
  ```
  ```
  if <cond>:
    <...>
  else if <cond>:
    <...>
  .
  ```

Check the example for better understanding.
 
## Usage
Write your test program to file `input.txt`, run `Main.java`. Result is being written to file `output.cpp`.

Uses [***ANTLRv4***](http://www.antlr.org/) to generate parser.
