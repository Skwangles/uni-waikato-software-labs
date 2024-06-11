-- Import the Parsing module
import Parsing


type Ide = String

-- Left recursion modified grammar
data Exp = Equal Trm Exp | Plus Trm Exp | Trm deriving Show
data Trm = Not Exp | Fctr deriving Show
data Fctr = Zero | One | T T | F F | Read | I Ide | (Exp) deriving Show
data Cmd = Assign Ide Exp | Output Exp | If Then Else Exp Cmd Cmd | While Do Exp Cmd | Seq Cmd Cmd deriving Show


expr :: Parser Exp
expr = do t1 <- term
        symbol "+"
        e2 <- expr
        return (Plus t1 e2)
        +++
        do t1 <- term 
            symbol "="
            e2 <- expr
          return (Equal t1 e2)
        +++
        term

term :: Parser Trm
term = do symbol "not"
           e1 <- expr
           return (Not e1)
          +++
           factor

factor :: Parser Fctr
factor = do 
          symbol "0"
          return Zero
          +++
          do
            symbol "1"
            return One
          +++
          do
            symbol "true"
            return (T t)
          +++
          do
            symbol "false"
            return (F f)
          +++
          do
            symbol "read"
            return Read
          +++
          do
            symbol "("
            e <- expr
            symbol ")"
            return e
          +++
          do
            i <- ident
            return (I i)


cmd :: Parser Cmd
cmd = do i <- ident
        symbol ":="
        e <- expr
        return (Assign i e)
        +++
        do
          symbol "output"
          e <- expr
          return (Output e)
        +++
        do
          symbol "if"
          e <- expr
          symbol "then"
          c1 <- cmd
          symbol "else"
          c2 <- cmd
          return (If e c1 c2)
        +++
        do
          symbol "while"
          e <- expr
          symbol "do"
          c <- cmd
          return (While e c)
        +++
        do
          c1 <- cmd
          symbol ";"
          c2 <- cmd
          return (Seq c1 c2)
        +++
          return c