package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class AnalizadorLexicoFinal {
    private static final String SEPARADOR = "========================================";

    public static void main(String[] args) {
        LexerCompleto analizador = new LexerCompleto();
        LexerCompleto.Resultado ultimoResultado = null;

        try (Scanner teclado = new Scanner(System.in)) {
            boolean continuar = true;
            while (continuar) {
                mostrarMenu();
                String opcion = teclado.nextLine().trim();

                switch (opcion) {
                    case "1" -> {
                        System.out.print("Ingrese una cadena: ");
                        ultimoResultado = analizador.analizar(teclado.nextLine());
                        mostrarResumen(ultimoResultado);
                    }
                    case "2" -> {
                        System.out.print("Ruta del archivo: ");
                        Path ruta = Path.of(teclado.nextLine().trim());
                        try {
                            ultimoResultado = analizador.analizar(Files.readString(ruta));
                            mostrarResumen(ultimoResultado);
                        } catch (IOException error) {
                            System.err.println("No se pudo leer el archivo: " + error.getMessage());
                        }
                    }
                    case "3" -> {
                        if (ultimoResultado == null) {
                            System.out.println("Primero debe analizar una entrada.");
                        } else {
                            LexerCompleto.mostrarTokens(ultimoResultado);
                        }
                    }
                    case "4" -> {
                        if (ultimoResultado == null) {
                            System.out.println("Primero debe analizar una entrada.");
                        } else {
                            LexerCompleto.mostrarErrores(ultimoResultado);
                        }
                    }
                    case "5" -> {
                        if (ultimoResultado == null) {
                            System.out.println("Primero debe analizar una entrada.");
                        } else {
                            LexerCompleto.mostrarEstadisticas(ultimoResultado);
                        }
                    }
                    case "6" -> continuar = false;
                    default -> System.out.println("Opcion invalida.");
                }
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println(SEPARADOR);
        System.out.println("ANALIZADOR LEXICO");
        System.out.println(SEPARADOR);
        System.out.println("1. Ingresar cadena");
        System.out.println("2. Analizar archivo");
        System.out.println("3. Mostrar tokens");
        System.out.println("4. Mostrar errores");
        System.out.println("5. Mostrar estadisticas");
        System.out.println("6. Salir");
        System.out.print("Seleccione una opcion: ");
    }

    private static void mostrarResumen(LexerCompleto.Resultado resultado) {
        System.out.println(SEPARADOR);
        System.out.println("RESULTADO DEL ANALISIS");
        System.out.println(SEPARADOR);
        LexerCompleto.mostrarEstadisticas(resultado);
        System.out.println(SEPARADOR);
    }
}
