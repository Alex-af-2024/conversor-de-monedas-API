package com.afranco.conv.main;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MainConversor {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        // Solicitar moneda de entrada al usuario
        System.out.print("Ingrese la moneda de entrada (por ejemplo, USD): ");
        String monedaEntrada = scanner.nextLine().toUpperCase();

        // Solicitar moneda de salida al usuario
        System.out.print("Ingrese la moneda de salida (por ejemplo, CLP): ");
        String monedaSalida = scanner.nextLine().toUpperCase();

        double cantidad = 0;
        try {
            // Solicitar la cantidad a convertir
            System.out.print("Ingrese la cantidad a convertir: ");
            cantidad = scanner.nextDouble();
        } catch (InputMismatchException ex) {
            System.out.println("Error: Debe ingresar un número válido.");
            return;
        }

        //1. Crea a cliente y solicitud. URI con política de API.
        HttpClient client = HttpClient.newHttpClient();
        String url = "https://v6.exchangerate-api.com/v6/2ead11f8e788cd68c02d3281/latest/"+monedaEntrada;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            //2. Envio de solicitud y Recepción de respuesta.
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body(); //Se obtiene contenido de respuesta que es un json en formato String
            //System.out.println("\nRespuesta JSON completa: ");
            //System.out.println(jsonResponse); //Para probar respuesta

            //3. Se crea objeto Gson para transformar cadena Json en objeto manipulable en Java.
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class); //gson es convertido a JsonObject para Java


            //4. Extraemos valores de clave conversion_rates
            JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");
            System.out.println("\nTasas de conversión disponibles:");
            System.out.println(conversionRates); // Para ver los datos de las tasas de cambio

            // 5. Buscar y obtener la tasa de la moneda de salida
            if (conversionRates.has(monedaSalida)) {
                double tasaConversion = conversionRates.get(monedaSalida).getAsDouble();
                double resultadoConversion = convertirMoneda(cantidad, tasaConversion);
                System.out.println("\n" + cantidad + " " + monedaEntrada + " equivalen a " +
                        String.format("%.2f", resultadoConversion) + " " + monedaSalida);
            }else {
                throw new  Exception ("\nLa divisa " + monedaSalida + " no se encuentra en la lista de tasas de conversión.");
                }
            } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            }
    }
    //Convertir moneda
    public static double convertirMoneda(double cantidad, double tasa) {
        return cantidad * tasa;
    }
}
