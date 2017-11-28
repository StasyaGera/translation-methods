## Grammar for regular expressions

    `EXPR   -> EXPR | CONCAT`
    `EXPR   -> CONCAT`
    `CONCAT -> CONCAT KLEENE`
    `CONCAT -> KLEENE`
    `KLEENE -> KLEENE*`
    `KLEENE -> n`
    `KLEENE -> (EXPR)`

where `n` from [a-z]

## After removing left recursion

    `EXPR    -> CONCAT EXPR'`
    `EXPR'   -> | CONCAT EXPR'`
    `EXPR'   -> eps`
    `CONCAT  -> KLEENE CONCAT'`
    `CONCAT' -> KLEENE CONCAT'`
    `CONCAT' -> eps`
    `KLEENE  -> n KLEENE'`
    `KLEENE  -> (EXPR) KLEENE'`
    `KLEENE' -> *KLEENE'`
    `KLEENE' -> eps`

## FIRST and FOLLOW for non-terminals

           |  FIRST  |    FOLLOW     |
   -------- --------- ---------------
  | EXPR   | n, (    | $, )          |
  | EXPR'  | |, eps    | $, )          |
  | CONCAT | n, (    | |, $, )       |
  | CONCAT'| eps n, ( | n, (, |, $, ) |
  | KLEENE | n, (    | n, (, |, $, ) |
  | KLEENE'| *, eps    | n, (, |, $, ) |
   -------- --------- ---------------

##Usage

