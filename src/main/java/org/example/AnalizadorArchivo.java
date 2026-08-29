package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AnalizadorArchivo {
    private static final Set<String> PALABRAS_RESERVADAS = Set.of("int", "float");

    private enum TipoToken {
        PALABRA_RESERVADA,
        IDENTIFICADOR,
        ASIGNACION,
        NUMERO,
        OPERADOR_ARITMETICO,
        DELIMITADOR
    }

    private record Token(TipoToken tipo, String lexema, int linea, int columna) {
    }

    private record ErrorLexico(char caracter, int linea, int columna) {
    }

    private record Resultado(List<Token> tokens, List<ErrorLexico> errores) {
    }

    public static void main(String[] args) {
        Path ruta = Path.of(args.length > 0 ? args[0] : "programa.txt");

        try {
            String contenido = Files.readString(ruta);
            System.out.println("Archivo analizado: " + ruta.toAbsolutePath());
            mostrarResultado(analizar(contenido));
        } catch (IOException error) {
            System.err.println("No se pudo abrir o leer el archivo '" + ruta + "': "
                    + error.getMessage());
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

            TipoToken tipoSimple = switch (caracter) {
                case '=' -> TipoToken.ASIGNACION;
                case '+' -> TipoToken.OPERADOR_ARITMETICO;
                case ';' -> TipoToken.DELIMITADOR;
                default -> null;
            };

            if (tipoSimple != null) {
                tokens.add(new Token(tipoSimple, String.valueOf(caracter),
                        lineaInicial, columnaInicial));
                posicion++;
                columna++;
                continue;
            }

            // Se registra el error y se avanza para continuar con el resto del archivo.
            errores.add(new ErrorLexico(caracter, linea, columna));
            posicion++;
            columna++;
        }

        return new Resultado(tokens, errores);
    }

    private static void mostrarResultado(Resultado resultado) {
        String bordeTokens = "+--------+----------+----------------------+----------------------+";
        System.out.println("TOKENS");
        System.out.println(bordeTokens);
        System.out.printf("| %-6s | %-8s | %-20s | %-20s |%n",
                "LINEA", "COLUMNA", "TOKEN", "LEXEMA");
        System.out.println(bordeTokens);
        for (Token token : resultado.tokens()) {
            System.out.printf("| %-6d | %-8d | %-20s | %-20s |%n",
                    token.linea(), token.columna(), token.tipo(), token.lexema());
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
