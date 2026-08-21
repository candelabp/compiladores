package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AnalizadorErroresLexicos {
    private enum TipoToken {
        IDENTIFICADOR,
        ASIGNACION,
        NUMERO,
        DELIMITADOR
    }

    private record Token(TipoToken tipo, String lexema) {
    }

    private record ErrorLexico(char caracter, int linea, int columna) {
    }

    private record Resultado(List<Token> tokens, List<ErrorLexico> errores) {
    }

    public static void main(String[] args) {
        System.out.println("Ingrese el texto y finalice la entrada con EOF:");

        try {
            String entrada = new String(System.in.readAllBytes(), StandardCharsets.UTF_8);
            mostrarResultado(analizar(entrada));
        } catch (IOException error) {
            System.err.println("No se pudo leer la entrada: " + error.getMessage());
        }
    }

    private static Resultado analizar(String entrada) {
        List<Token> tokens = new ArrayList<>();
        List<ErrorLexico> errores = new ArrayList<>();
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

            if (Character.isLetter(caracter) || caracter == '_') {
                int inicio = posicion;
                while (posicion < entrada.length()
                        && (Character.isLetterOrDigit(entrada.charAt(posicion))
                        || entrada.charAt(posicion) == '_')) {
                    posicion++;
                    columna++;
                }
                tokens.add(new Token(TipoToken.IDENTIFICADOR,
                        entrada.substring(inicio, posicion)));
                continue;
            }

            if (Character.isDigit(caracter)) {
                int inicio = posicion;
                while (posicion < entrada.length()
                        && Character.isDigit(entrada.charAt(posicion))) {
                    posicion++;
                    columna++;
                }
                tokens.add(new Token(TipoToken.NUMERO,
                        entrada.substring(inicio, posicion)));
                continue;
            }

            if (caracter == '=') {
                tokens.add(new Token(TipoToken.ASIGNACION, String.valueOf(caracter)));
                posicion++;
                columna++;
                continue;
            }

            if (caracter == ';') {
                tokens.add(new Token(TipoToken.DELIMITADOR, String.valueOf(caracter)));
                posicion++;
                columna++;
                continue;
            }

            // Recuperacion: se registra el error, se omite el caracter y se continua.
            errores.add(new ErrorLexico(caracter, linea, columna));
            posicion++;
            columna++;
        }

        return new Resultado(tokens, errores);
    }

    private static void mostrarResultado(Resultado resultado) {
        String bordeTokens = "+----------------------+----------------------+";
        System.out.println("TOKENS");
        System.out.println(bordeTokens);
        System.out.printf("| %-20s | %-20s |%n", "TOKEN", "LEXEMA");
        System.out.println(bordeTokens);
        for (Token token : resultado.tokens()) {
            System.out.printf("| %-20s | %-20s |%n", token.tipo(), token.lexema());
        }
        System.out.println(bordeTokens);

        if (resultado.errores().isEmpty()) {
            System.out.println("No se encontraron errores lexicos.");
            return;
        }

        String bordeErrores = "+----------------------+----------+----------+";
        System.out.println("ERRORES LEXICOS");
        System.out.println(bordeErrores);
        System.out.printf("| %-20s | %-8s | %-8s |%n", "CARACTER", "LINEA", "COLUMNA");
        System.out.println(bordeErrores);
        for (ErrorLexico error : resultado.errores()) {
            System.out.printf("| %-20s | %-8d | %-8d |%n",
                    error.caracter(), error.linea(), error.columna());
        }
        System.out.println(bordeErrores);
    }
}
