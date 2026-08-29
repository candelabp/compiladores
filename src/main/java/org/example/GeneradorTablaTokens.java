package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GeneradorTablaTokens {
    private static final Set<String> PALABRAS_RESERVADAS = Set.of("int", "float");

    private enum TipoToken {
        PALABRA_RESERVADA,
        IDENTIFICADOR,
        ASIGNACION,
        NUMERO,
        OPERADOR_ARITMETICO,
        DELIMITADOR
    }

    private record Token(int linea, int columna, TipoToken tipo, String lexema) {
    }

    public static void main(String[] args) {
        Path archivo = Path.of(args.length > 0 ? args[0] : "programa_tokens.txt");

        try {
            String codigo = Files.readString(archivo);
            List<Token> tokens = generarTokens(codigo);
            System.out.println("Tabla de tokens: " + archivo.toAbsolutePath());
            mostrarTabla(tokens);
        } catch (IOException error) {
            System.err.println("No se pudo leer el archivo '" + archivo + "': "
                    + error.getMessage());
        }
    }

    private static List<Token> generarTokens(String codigo) {
        List<Token> tokens = new ArrayList<>();
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

            int lineaInicial = linea;
            int columnaInicial = columna;

            if (Character.isLetter(caracter) || caracter == '_') {
                int inicio = posicion;
                while (posicion < codigo.length()
                        && (Character.isLetterOrDigit(codigo.charAt(posicion))
                        || codigo.charAt(posicion) == '_')) {
                    posicion++;
                    columna++;
                }

                String lexema = codigo.substring(inicio, posicion);
                TipoToken tipo = PALABRAS_RESERVADAS.contains(lexema)
                        ? TipoToken.PALABRA_RESERVADA
                        : TipoToken.IDENTIFICADOR;
                tokens.add(new Token(lineaInicial, columnaInicial, tipo, lexema));
                continue;
            }

            if (Character.isDigit(caracter)) {
                int inicio = posicion;
                while (posicion < codigo.length()
                        && Character.isDigit(codigo.charAt(posicion))) {
                    posicion++;
                    columna++;
                }

                if (posicion < codigo.length() && codigo.charAt(posicion) == '.'
                        && posicion + 1 < codigo.length()
                        && Character.isDigit(codigo.charAt(posicion + 1))) {
                    posicion++;
                    columna++;
                    while (posicion < codigo.length()
                            && Character.isDigit(codigo.charAt(posicion))) {
                        posicion++;
                        columna++;
                    }
                }

                tokens.add(new Token(lineaInicial, columnaInicial, TipoToken.NUMERO,
                        codigo.substring(inicio, posicion)));
                continue;
            }

            TipoToken tipo = switch (caracter) {
                case '=' -> TipoToken.ASIGNACION;
                case '+' -> TipoToken.OPERADOR_ARITMETICO;
                case ';' -> TipoToken.DELIMITADOR;
                default -> null;
            };

            if (tipo != null) {
                tokens.add(new Token(lineaInicial, columnaInicial, tipo,
                        String.valueOf(caracter)));
            } else {
                System.err.printf("Error lexico: caracter '%s' en linea %d, columna %d%n",
                        caracter, linea, columna);
            }

            // Se avanza incluso si hay un error para completar la tabla del archivo.
            posicion++;
            columna++;
        }

        return tokens;
    }

    private static void mostrarTabla(List<Token> tokens) {
        String borde = "+--------+----------+----------------------+----------------------+";
        System.out.println(borde);
        System.out.printf("| %-6s | %-8s | %-20s | %-20s |%n",
                "LINEA", "COLUMNA", "TOKEN", "LEXEMA");
        System.out.println(borde);
        for (Token token : tokens) {
            System.out.printf("| %-6d | %-8d | %-20s | %-20s |%n",
                    token.linea(), token.columna(), token.tipo(), token.lexema());
        }
        System.out.println(borde);
    }
}
