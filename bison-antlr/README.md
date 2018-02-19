## A tool for converting a primitive imperative language to C++
### Syntax
* expressions are separated by a newline
* all the variables are integers
* all the variables are global
* anything matching `[a-zA-Z_][a-zA-Z0-9_]*` is considered as a variable name
* `read()` and `print(<vars>)` keywords are used to read from standart input and write to standart output corresponsively
* `read()` is used as an expression in assignment: `x = read()`
* `print(<vars>)` writes its arguments: `print(x, 2)`
* variables can be assigned through listing, for example: `x, y, z = 1, read(), x`
* all clauses should be finished by a `.`
* `while` clauses syntax:
  ```
  while <cond>:
    <...>
  .
* `for` clauses syntax:
  ```
  for <var> in <int>..<int>:
    <...>
  .
  ```
* `if` clauses syntax: 
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

Check the [example](input.txt) for better understanding.
 
## Usage
Write your test program to file `input.txt`, run `Main.java`. Result is being written to file `output.cpp`.

Uses [***ANTLRv4***](http://www.antlr.org/) to generate parser.
