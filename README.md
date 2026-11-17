# CampusFest_2026-2_isi

## Integrantes :
- Alan Barón
- Dylan Pérez
- Sofía Ordoñez
- Santiago Delgado

## Requisitos Previos :
- Java Development Kit (JDK) 21
- Apache Maven 3.8+

## Ejecución :
1. **Ejecutar las pruebas unitarias**
   ```bash
   mvn clean test
2. **Ejecutar la interfaz gráfica JavaFx**
   ```bash
   mvn javafx:run

## Casos de Uso Implementados :
- **CU01 - Consultar Cupos:** Calcula los cupos libres de un evento restando al aforo los cupos bloqueados y las reservas activas (confirmadas o pendientes dentro de los 10 minutos).
- **CU02 - Reservar y Confirmar Entrada:** Crea una reserva en estado PENDIENTE con vencimiento a los 10 minutos exactos y permite confirmarla antes de que expire el plazo.
- **CU03 - Editar Evento:** Permite consultar y modificar nombre, fecha, lugar y aforo del evento, impidiendo que el nuevo aforo quede por debajo de los cupos ya ocupados.
- **CU04 - Registrar Ingreso con QR:** Valida que el código QR corresponda al evento, que la reserva esté confirmada y que no haya registrado un ingreso previo.

## Declaración de Uso de Inteligencia Artificial :
- **Herramienta de IA utilizadas:** Gemini.
- **Para qué y en qué clases se usó:** Se utilizó como apoyo para diseñar la hoja de estilos de la interfaz gráfica (estilos.css), revisar la estructura de paquetes de Arquitectura Limpia y consultar ejemplos de consultas SQL con MERGE para el adaptador de persistencia (RepositorioEventosH2.java).
- **Propuesta de la IA que violaba el diseño y cómo se corrigió:** Al pedir ayuda para conectar la pantalla de edición de eventos (PantallaEventoController.java), la IA sugirió validar directamente en el controlador si el nuevo aforo era menor que los cupos ocupados antes de llamar al caso de uso. Esto rompía el patrón GRASP de Experto en Información y la regla de Arquitectura Limpia de mantener los controladores sin lógica de negocio. Para corregirlo, quitamos esa validación del controlador y la dejamos únicamente en el método cambiarAforo de la entidad Evento.java, dejando que el controlador solo capture la excepción y muestre el mensaje en etiquetaMensaje.
