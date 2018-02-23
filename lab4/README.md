## A simplified analogue of ANTLR tool for generating parsers

### Syntax
Syntax is copied from [ANTLRv4](http://www.antlr.org/). 

### Nuances (consider yourself warned)
* if you want to describe your token as a regexp, surround the regexp with slashes (`/`)
* allows `@init` sections for rules, but no `@after` ones
* supports an unlimited amount of inherited attributes but only one synthesised
* does not allow user-defined symbols to skip (skips every `c` for which Java's `Character.isWhitespace(c)` is true)
* no guarantees that parser will be generated without naming collisions
* `transformToLL1` method from [Grammar class](src/Grammar.java) works only for grammars without attributes

### Usage
Write your grammar to the `input.txt` file, then run Main. Generated files will be placed to `my_gen` directory.

### Examples
Grammar and generated files for:
* [arithmetics](ex1) (operations `+`, `*` and right-associative power `^^`).
* [regexps](ex2) (same as [this](../recursive-parsing))
