-- Import the Parsing module
import Parsing
import Debug.Trace

debug = flip trace

type Ide = String
-- Left recursion modified grammar
data Exp = Zero | One | TT | FF | Read | I Ide | Not Exp | Equal Exp Exp | Plus Exp Exp deriving Show
data Cmd = Assign Ide Exp | Output Exp | IfThenElse Exp Cmd Cmd | WhileDo Exp Cmd | Seq Cmd Cmd 
            deriving Show

-- <expr> ::= <expr> + <expr> | <expr> = <expr> | <term>
-- <term> ::= not <expr> | <factor>
-- <factor> ::= read | false | true | 0 | 1 | <ide> | (<expr>)
--  where <ide> is any valid identifier

-- <cmd> ::= <subcmd> ; <cmd> | <subcmd> -- Prevent left recursion
-- <subcmd> ::= output <expr> | if <expr> then <cmd> else <cmd> | while <expr> do <cmd> | <ide> := <expr> | (<cmd>)

expr :: Parser Exp
expr = do
  t1 <- term `debug` "Checking a+b"
  symbol "+"
  e2 <- expr 
  return (Plus t1 e2)
  +++
  do 
    t1 <- term `debug` "Checking a=b"
    symbol "="
    e2 <- expr 
    return (Equal t1 e2)
  +++
  term `debug` "----Term----"

term :: Parser Exp
term =  do 
    symbol "not" `debug` "Not"
    e1 <- expr 
    return (Not e1)
  +++
  factor `debug` "----Factor----"

factor :: Parser Exp
factor = do 
  symbol "0" `debug` "Zero"
  return (Zero)
  +++
  do
    symbol "1" `debug` "One"
    return (One)
  +++
  do
    symbol "true" `debug` "Truthy"
    return (TT)
  +++
  do
    symbol "false" `debug` "Falsy"
    return (FF)
  +++
  do
    symbol "read" `debug` "Read"
    return (Read)
  +++
  do
    symbol "(" `debug` "Brackets"
    e <- expr
    symbol ")"
    return e
  +++
  do 
    i <- identifier `debug` "checking identifier"
    return (I i)
  

cmd :: Parser Cmd
cmd = do
        c1 <- subcmd `debug` "Checking semicolon"
        symbol ";"
        c2 <- cmd
        return (Seq c1 c2)
      +++
      subcmd `debug` "----Subcmd----"


subcmd :: Parser Cmd
subcmd = do
        symbol "output"
        e <- expr `debug` "Output"
        return (Output e)
      +++
      do
        symbol "if" `debug` "If"
        e <- expr 
        symbol "then"
        c1 <- cmd
        symbol "else"
        c2 <- cmd
        return (IfThenElse e c1 c2)
      +++
      do
        symbol "while" `debug` "While"
        e <- expr 
        symbol "do"
        c <- cmd
        return (WhileDo e c)
      +++
      do 
        i <- identifier `debug` "Assign"
        symbol ":="
        e <- expr 
        return (Assign i e)
      +++
      do
        symbol "(" `debug` "Brackets"
        c <- cmd
        symbol ")"
        return c


eparse :: String -> Exp
eparse xs = case (parse expr xs) of
              [(n,[])] -> n
              [(_,out)] -> error ("unused input " ++ out)
              [] -> error "invalid input"

cparse :: String -> Exp
cparse xs = case (parse expr xs) of
              [(n,[])] -> n
              [(_,out)] -> error ("unused input " ++ out)
              [] -> error "invalid input"

main :: IO ()
main = do
        putStrLn (show (eparse "a+b=0"))
        putStrLn (show (eparse "a+read+c=d"))
        putStrLn (show (eparse "a=b+false"))
        putStrLn (show (eparse "a=b+1+d"))
        putStrLn (show (eparse "a=(true=c)"))

        putStrLn (show (cparse "output a; output b"))
        putStrLn (show (cparse "if a then output b else output c"))
        putStrLn (show (cparse "while a do output b"))
        putStrLn (show (cparse "a := b + c"))
        putStrLn (show (cparse "(output a; output b)"))
        putStrLn (show (cparse "sum:=0;x:=read;(while not (x = true) do (sum:=sum+x;x:=read));output sum"))
