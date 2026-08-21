package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AnalizadorComentarios {
    private enum TipoToken {
        IDENTIFICADOR,
        ASIGNACION,
        NUMERO,
        DELIMITADOR,
        COMENTARIO
    }

    private record Token(TipoToken tipo, String lexema) {
    }

    public static void main(String[] args) {
        boolean descartarComentarios = args.length > 0
                && args[0].equalsIgnoreCase("--descartar-comentarios");

        try (Scanner teclado = new Scanner(System.in)) {
            System.out.print("Ingrese una linea: ");
            String entrada = teclado.nextLine();

            try {
                mostrarTokens(analizar(entrada, descartarComentarios));
            } catch (IllegalArgumentException error) {
                System.err.println("Error lexico: " + error.getMessage());
            }
        }
    }

    private static List<Token> analizar(String entrada, boolean descartarComentarios) {
        List<Token> tokens = new ArrayList<>();
        int posicion = 0;

        while (posicion < entrada.length()) {
            char caracter = entrada.charAt(posicion);

            if (Character.isWhitespace(caracter)) {
                posicion++;
                continue;
            }

            if (caracter == '/' && posicion + 1 < entrada.length()
                    && entrada.charAt(posicion + 1) == '/') {
                if (!descartarComentarios) {
                    tokens.add(new Token(TipoToken.COMENTARIO, entrada.substring(posicion)));
                }
                break;
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
        String borde = "+----------------------+------------------------------+";
        System.out.println(borde);
        System.out.printf("| %-20s | %-28s |%n", "TOKEN", "LEXEMA");
        System.out.println(borde);
        for (Token token : tokens) {
            System.out.printf("| %-20s | %-28s |%n", token.tipo(), token.lexema());
        }
        System.out.println(borde);
    }
}
