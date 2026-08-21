package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AnalizadorEspacios {
    private enum TipoToken {
        IDENTIFICADOR,
        ASIGNACION,
        NUMERO,
        OPERADOR_ARITMETICO,
        DELIMITADOR
    }

    private record Token(TipoToken tipo, String lexema) {
    }

    public static void main(String[] args) {
        System.out.println("Ingrese una expresion y finalice la entrada con EOF:");

        try {
            String entrada = new String(System.in.readAllBytes(), StandardCharsets.UTF_8);
            mostrarTokens(analizar(entrada));
        } catch (IOException error) {
            System.err.println("No se pudo leer la entrada: " + error.getMessage());
        } catch (IllegalArgumentException error) {
            System.err.println("Error lexico: " + error.getMessage());
        }
    }

    private static List<Token> analizar(String entrada) {
        List<Token> tokens = new ArrayList<>();
        int posicion = 0;

        while (posicion < entrada.length()) {
            char caracter = entrada.charAt(posicion);

            // Los espacios, tabulaciones y saltos de linea no generan tokens.
            if (Character.isWhitespace(caracter)) {
                posicion++;
                continue;
            }

            if (Character.isLetter(caracter) || caracter == '_') {
                int inicio = posicion++;
                while (posicion < entrada.length()
                        && (Character.isLetterOrDigit(entrada.charAt(posicion))
                        || entrada.charAt(posicion) == '_')) {
                    posicion++;
                }
                tokens.add(new Token(TipoToken.IDENTIFICADOR,
                        entrada.substring(inicio, posicion)));
                continue;
            }

            if (Character.isDigit(caracter)) {
                int inicio = posicion++;
                while (posicion < entrada.length()
                        && Character.isDigit(entrada.charAt(posicion))) {
                    posicion++;
                }
                tokens.add(new Token(TipoToken.NUMERO,
                        entrada.substring(inicio, posicion)));
                continue;
            }

            if (caracter == '=') {
                tokens.add(new Token(TipoToken.ASIGNACION, String.valueOf(caracter)));
                posicion++;
                continue;
            }

            if (caracter == '+') {
                tokens.add(new Token(TipoToken.OPERADOR_ARITMETICO,
                        String.valueOf(caracter)));
                posicion++;
                continue;
            }

            if (caracter == ';') {
                tokens.add(new Token(TipoToken.DELIMITADOR, String.valueOf(caracter)));
                posicion++;
                continue;
            }

            throw new IllegalArgumentException(
                    "caracter no reconocido '" + caracter + "' en la posicion " + posicion);
        }

        return tokens;
    }

    private static void mostrarTokens(List<Token> tokens) {
        String borde = "+----------------------+----------------------+";
        System.out.println(borde);
        System.out.printf("| %-20s | %-20s |%n", "TOKEN", "LEXEMA");
        System.out.println(borde);
        for (Token token : tokens) {
            System.out.printf("| %-20s | %-20s |%n", token.tipo(), token.lexema());
        }
        System.out.println(borde);
    }
}
