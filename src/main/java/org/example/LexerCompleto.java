package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class LexerCompleto {
    private static final Set<String> RESERVADAS = Set.of("int", "float", "if");

    enum TipoToken {
        PALABRA_RESERVADA,
        IDENTIFICADOR,
        ASIGNACION,
        NUMERO_ENTERO,
        NUMERO_DECIMAL,
        CADENA,
        OPERADOR_ARITMETICO,
        OPERADOR_RELACIONAL,
        DELIMITADOR,
        COMENTARIO
    }

    record Token(TipoToken tipo, String lexema, int linea, int columna) {
    }

    record ErrorLexico(String lexema, String descripcion, int linea, int columna) {
    }

    record Resultado(List<Token> tokens, List<ErrorLexico> errores, int lineas) {
    }

    Resultado analizar(String codigo) {
        List<Token> tokens = new ArrayList<>();
        List<ErrorLexico> errores = new ArrayList<>();
        int posicion = 0;
        int linea = 1;
        int columna = 1;

        while (posicion < codigo.length()) {
            char caracter = codigo.charAt(posicion);

            if (caracter == '\n') {
                posicion++;
                linea++;
                columna = 1;
                continue;
            }
            if (Character.isWhitespace(caracter)) {
                posicion++;
                columna++;
                continue;
            }

            int inicio = posicion;
            int lineaInicial = linea;
            int columnaInicial = columna;

            if (caracter == '/' && posicion + 1 < codigo.length()
                    && codigo.charAt(posicion + 1) == '/') {
                posicion += 2;
                columna += 2;
                while (posicion < codigo.length() && codigo.charAt(posicion) != '\n') {
                    posicion++;
                    columna++;
                }
                tokens.add(new Token(TipoToken.COMENTARIO,
                        codigo.substring(inicio, posicion), lineaInicial, columnaInicial));
                continue;
            }

            if (Character.isLetter(caracter) || caracter == '_') {
                posicion++;
                columna++;
                while (posicion < codigo.length()
                        && (Character.isLetterOrDigit(codigo.charAt(posicion))
                        || codigo.charAt(posicion) == '_')) {
                    posicion++;
                    columna++;
                }
                String lexema = codigo.substring(inicio, posicion);
                TipoToken tipo = RESERVADAS.contains(lexema)
                        ? TipoToken.PALABRA_RESERVADA : TipoToken.IDENTIFICADOR;
                tokens.add(new Token(tipo, lexema, lineaInicial, columnaInicial));
                continue;
            }

            if (Character.isDigit(caracter)) {
                posicion++;
                columna++;
                while (posicion < codigo.length()
                        && (Character.isLetterOrDigit(codigo.charAt(posicion))
                        || codigo.charAt(posicion) == '_' || codigo.charAt(posicion) == '.')) {
                    posicion++;
                    columna++;
                }
                String lexema = codigo.substring(inicio, posicion);
                if (lexema.matches("[0-9]+")) {
                    tokens.add(new Token(TipoToken.NUMERO_ENTERO, lexema,
                            lineaInicial, columnaInicial));
                } else if (lexema.matches("[0-9]+\\.[0-9]+")) {
                    tokens.add(new Token(TipoToken.NUMERO_DECIMAL, lexema,
                            lineaInicial, columnaInicial));
                } else {
                    errores.add(new ErrorLexico(lexema, "numero o identificador invalido",
                            lineaInicial, columnaInicial));
                }
                continue;
            }

            if (caracter == '"') {
                posicion++;
                columna++;
                boolean cerrada = false;
                while (posicion < codigo.length() && codigo.charAt(posicion) != '\n') {
                    if (codigo.charAt(posicion) == '\\' && posicion + 1 < codigo.length()
                            && codigo.charAt(posicion + 1) != '\n') {
                        posicion += 2;
                        columna += 2;
                    } else if (codigo.charAt(posicion) == '"') {
                        posicion++;
                        columna++;
                        cerrada = true;
                        break;
                    } else {
                        posicion++;
                        columna++;
                    }
                }
                String lexema = codigo.substring(inicio, posicion);
                if (cerrada) {
                    tokens.add(new Token(TipoToken.CADENA, lexema,
                            lineaInicial, columnaInicial));
                } else {
                    errores.add(new ErrorLexico(lexema, "cadena sin comilla de cierre",
                            lineaInicial, columnaInicial));
                }
                continue;
            }

            if (posicion + 1 < codigo.length()) {
                String operadorLargo = codigo.substring(posicion, posicion + 2);
                if (operadorLargo.equals("==") || operadorLargo.equals(">=")
                        || operadorLargo.equals("<=") || operadorLargo.equals("!=")) {
                    tokens.add(new Token(TipoToken.OPERADOR_RELACIONAL, operadorLargo,
                            lineaInicial, columnaInicial));
                    posicion += 2;
                    columna += 2;
                    continue;
                }
            }

            TipoToken tipoSimple = switch (caracter) {
                case '=' -> TipoToken.ASIGNACION;
                case '>', '<' -> TipoToken.OPERADOR_RELACIONAL;
                case '+', '-', '*', '/' -> TipoToken.OPERADOR_ARITMETICO;
                case ';', '{', '}', '(', ')', ',' -> TipoToken.DELIMITADOR;
                default -> null;
            };
            if (tipoSimple != null) {
                tokens.add(new Token(tipoSimple, String.valueOf(caracter),
                        lineaInicial, columnaInicial));
            } else {
                errores.add(new ErrorLexico(String.valueOf(caracter),
                        "caracter no reconocido", lineaInicial, columnaInicial));
            }
            posicion++;
            columna++;
        }

        int cantidadLineas = codigo.isEmpty() ? 0
                : linea - (codigo.endsWith("\n") ? 1 : 0);
        return new Resultado(List.copyOf(tokens), List.copyOf(errores), cantidadLineas);
    }

    static void mostrarTokens(Resultado resultado) {
        String borde = "+--------+----------+----------------------+--------------------------+";
        System.out.println(borde);
        System.out.printf("| %-6s | %-8s | %-20s | %-24s |%n",
                "LINEA", "COLUMNA", "TOKEN", "LEXEMA");
        System.out.println(borde);
        for (Token token : resultado.tokens()) {
            System.out.printf("| %-6d | %-8d | %-20s | %-24s |%n",
                    token.linea(), token.columna(), token.tipo(), token.lexema());
        }
        System.out.println(borde);
    }

    static void mostrarErrores(Resultado resultado) {
        if (resultado.errores().isEmpty()) {
            System.out.println("No se encontraron errores lexicos.");
            return;
        }
        for (int i = 0; i < resultado.errores().size(); i++) {
            ErrorLexico error = resultado.errores().get(i);
            System.out.printf("Error %d: %s | Lexema: %s | Linea: %d | Columna: %d%n",
                    i + 1, error.descripcion(), error.lexema(), error.linea(), error.columna());
        }
    }

    static void mostrarEstadisticas(Resultado resultado) {
        System.out.println("Tokens reconocidos: " + resultado.tokens().size());
        System.out.println("Errores lexicos: " + resultado.errores().size());
        System.out.println("Lineas analizadas: " + resultado.lineas());
    }
}
