import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ASDI implements Parser {

    private boolean hayErrores = false;
    private final List<Token> tokens;
    ArrayList<String> produccionArrayList = new ArrayList<>();
    String produccion;
    String stringX;
    Token A;
    String stringA;
    Stack<String> pila = new Stack<>();

    public ASDI(List<Token> tokens) {
        this.tokens = tokens;
    }

    @Override
    public boolean parse() {
        String[][] tabla = {
                { "",   "select",               "from",     "distinct",         "*",        ",",            "id",           ".",            "$" },
                { "Q",  "Q -> select D from T", "",         "",                 "",         "",             "",             "",             "" },
                { "D",  "",                     "",         "D -> distinct P",  "D -> P",   "",             "D -> P",       "",             "" },
                { "P",  "",                     "",         "",                 "P -> *",   "",             "P -> A",       "",             "" },
                { "A",  "",                     "",         "",                 "",         "",             "A -> A2 A1",   "",             "" },
                { "A1", "",                     "A1 -> E",  "",                 "",         "A1 -> , A",    "",             "",             "" },
                { "A2", "",                     "",         "",                 "",         "",             "A2 -> id A3",  "",             "" },
                { "A3", "",                     "A3 -> E",  "",                 "",         "A3 -> E",      "",             "A3 -> . id",   "" },
                { "T",  "",                     "",         "",                 "",         "",             "T -> T2 T1",   "",             "" },
                { "T1", "",                     "",         "",                 "",         "T1 -> , T",    "",             "",             "T1 -> E" },
                { "T2", "",                     "",         "",                 "",         "",             "T2 -> id T3",  "",             "" },
                { "T3", "",                     "",         "",                 "",         "T3 -> E",      "T3 -> id",     "",             "T3 -> E" }
        };

        analizar(tokens, tabla);

        if (A.tipo == TipoToken.EOF && !hayErrores) {
            System.out.println("Consulta correcta");
            return true;
        } else {
            System.out.println("Se encontraron errores");
        }
        return false;
    }

    private void analizar(List<Token> tokens, String[][] tabla) {

        pila.clear();

        int ip = 0;
        pila.push("$");
        pila.push("Q");

        stringX = pila.lastElement();
        A = tokens.get(ip);
        stringA = A.lexema;

        while (!stringX.equals("$")) {
            if(A.tipo == TipoToken.IDENTIFICADOR){ //Para identificadores
                stringA = "id";
            }

            if (stringX.hashCode() == stringA.hashCode()) {
                pila.pop();
                ip++;
                A = tokens.get(ip);
                stringA = A.lexema;
            } else if (TablaAnalisis.esTerminal(stringX, tabla)) {
                System.out.println("Error. " + (ip + 1) + ": Simbolo terminal no esperado...");
                hayErrores = true;
                break;
            } else if (TablaAnalisis.validar(stringX, stringA, tabla) == "") {
                System.out.println("Error. " + (ip + 1) + ": Producción no válida.");
                hayErrores = true;
                break;
            } else {
                produccion = TablaAnalisis.validar(stringX, stringA, tabla);
                System.out.println(produccion);
                pila.pop();
                produccionArrayList = Leer.aArraylist(produccion);
                if(produccionArrayList.get(0).hashCode() != "E".hashCode()){ //Si no es E no mete la producción a la pila
                    for (int i = produccionArrayList.size() - 1; i >= 0; i--) {
                        pila.push(produccionArrayList.get(i));
                    }
                }
                produccionArrayList.clear();
            }
            stringX = pila.lastElement();
        }
    }
}