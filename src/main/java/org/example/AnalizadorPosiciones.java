package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AnalizadorPosiciones {
    private static final Set<String> PALABRAS_RESERVADAS = Set.of("int", "float");

    private enum TipoToken {
        PALABRA_RESERVADA,
        IDENTIFICADOR,
        ASIGNACION,
        NUMERO,
        DELIMITADOR
    }

    private record Token(TipoToken tipo, String lexema, int linea, int columna) {
    }

    public static void main(String[] args) {
        System.out.println("Ingrese el codigo y finalice la entrada con EOF:");

        try {
            String entrada = new String(System.in.readAllBytes(), StandardCharsets.UTF_8);
            mostrarTokens(analizar(entrada));
        } catch (IOException error) {
            System.err.println("No se pudo leer la entrada: " + error.getMessage());
        }
    }

    private static List<Token> analizar(String entrada) {
        List<Token> tokens = new ArrayList<>();
        int posicion = 0;
        int linea = 1;
        int columna = 1;

        while (posicion < entrada.length()) {
            char caracter = entrada.charAt(posicion);

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
                while (posicion < entrada.length()
                        && (Character.isLetterOrDigit(entrada.charAt(posicion))
                        || entrada.charAt(posicion) == '_')) {
                    posicion++;
                    columna++;
                }

                String lexema = entrada.substring(inicio, posicion);
                TipoToken tipo = PALABRAS_RESERVADAS.contains(lexema)
                        ? TipoToken.PALABRA_RESERVADA
                        : TipoToken.IDENTIFICADOR;
                tokens.add(new Token(tipo, lexema, lineaInicial, columnaInicial));
                continue;
            }

            if (Character.isDigit(caracter)) {
                int inicio = posicion;
                while (posicion < entrada.length()
                        && Character.isDigit(entrada.charAt(posicion))) {
                    posicion++;
                    columna++;
                }

                if (posicion < entrada.length() && entrada.charAt(posicion) == '.'
                        && posicion + 1 < entrada.length()
                        && Character.isDigit(entrada.charAt(posicion + 1))) {
                    posicion++;
                    columna++;
                    while (posicion < entrada.length()
                            && Character.isDigit(entrada.charAt(posicion))) {
                        posicion++;
                        columna++;
                    }
                }

                tokens.add(new Token(TipoToken.NUMERO,
                        entrada.substring(inicio, posicion), lineaInicial, columnaInicial));
                continue;
            }

            if (caracter == '=') {
                tokens.add(new Token(TipoToken.ASIGNACION, "=", lineaInicial, columnaInicial));
                posicion++;
                columna++;
                continue;
            }

            if (caracter == ';') {
                tokens.add(new Token(TipoToken.DELIMITADOR, ";", lineaInicial, columnaInicial));
                posicion++;
                columna++;
                continue;
            }

            System.err.printf("Error lexico: caracter '%s' en linea %d, columna %d%n",
                    caracter, linea, columna);
            posicion++;
            columna++;
        }

        return tokens;
    }

    private static void mostrarTokens(List<Token> tokens) {
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
