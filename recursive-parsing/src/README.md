## Grammar for regular expressions

* `EXPR   -> EXPR | CONCAT`
* `EXPR   -> CONCAT`
* `CONCAT -> CONCAT KLEENE`
* `CONCAT -> KLEENE`
* `KLEENE -> KLEENE*`
* `KLEENE -> n`
* `KLEENE -> (EXPR)`

where `n` from `[a-z]`

## After removing left recursion

* `EXPR    -> CONCAT EXPR'`
* `EXPR'   -> | CONCAT EXPR'`
* `EXPR'   -> eps`
* `CONCAT  -> KLEENE CONCAT'`
* `CONCAT' -> KLEENE CONCAT'`
* `CONCAT' -> eps`
* `KLEENE  -> n KLEENE'`
* `KLEENE  -> (EXPR) KLEENE'`
* `KLEENE' -> *KLEENE'`
* `KLEENE' -> eps`

## FIRST and FOLLOW for non-terminals

non-term|    FIRST    |       FOLLOW     
--------|-------------|--------------------
 EXPR   | n, (        | $, )          
 EXPR'  | &#124;, eps | $, )          
 CONCAT | n, (        | &#124;, $, )       
 CONCAT'| eps, n, (   | n, (, &#124;, $, ) 
 KLEENE | n, (        | n, (, &#124;, $, ) 
 KLEENE'| *, eps      | n, (, &#124;, $, ) 
 
## Usage
* Run `Test.java` to evaluate random tests
* Write your test to file `test.in` or pass it as an argument; run `Main.java`

Uses [***JUNG2***](http://jung.sourceforge.net/) library to draw graphs.
