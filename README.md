# FitControl 🏋️‍♂️

**Equipo de Desarrollo:** Print("dev")

> 📲 **Descarga Directa:** Puedes instalar la última versión de la aplicación desde nuestro sitio oficial:
>
> 👉 **[https://ramp-chile.web.app](https://ramp-chile.web.app)**

---

## 👥 Integrantes
* Eric Saavedra Maldonado
* Diego Carrillo

## 📝 Descripción del Proyecto
**FitControl** es una aplicación móvil diseñada para solucionar la falta de herramientas de gestión para Personal Trainers (PF) y profesores de acondicionamiento físico. La plataforma permite a los profesionales organizar a sus alumnos, planificar rutinas personalizadas y realizar un seguimiento efectivo, sustituyendo métodos manuales o desorganizados.

### Roles de Usuario
La aplicación cuenta con 2 roles principales con flujos diferenciados:
1.  **PF (Profesor):** Gestión de alumnos, creación de rutinas, configuración de temporizadores y administración de perfil profesional.
2.  **Alumno:** Visualización de rutinas diarias/semanales, cálculo calórico, historial de ejercicios y contacto con el PF.

### Funcionalidades Principales
* **Autenticación:** Inicio de sesión seguro con redirección inteligente según el rol (PF o Alumno).
* **Gestión de Rutinas:** Creación y asignación de rutinas semanales con ejercicios detallados (series, peso, repeticiones).
* **Base de Datos de Ejercicios:** Selección de ejercicios pre-cargados (API WGER) o creación de ejercicios personalizados con soporte multimedia (imágenes/links).
* **Herramientas de Entrenamiento:** Temporizadores integrados para medir tiempos de ejecución en sesiones presenciales.
* **Seguimiento y Progreso:** Historial de ejercicios realizados para evaluar la evolución de carga y repeticiones.
* **Perfil Profesional:** Configuración de datos de contacto (WhatsApp, teléfono) para facilitar la comunicación alumno-profesor.
* **Cálculo Calórico:** Estimación de quema de calorías por rutina basado en el peso, repeticiones y series.

---

## 🛠️ Aspectos Técnicos

### 🔗 Arquitectura y APIs

**1. API Externa (Consumo Público):**
* **Nombre:** **WGER API**
* **Función:** Se utiliza para poblar la base de datos con imágenes, descripciones y catálogos de ejercicios estandarizados.

**2. Microservicios Propios (Backend):**
La lógica de negocio se maneja a través de los siguientes endpoints principales:

* `POST /api/auth/login`: Autenticación y determinación de rol.
* `GET /api/alumnos/list`: Obtención de lista de alumnos asignados al PF.
* `POST /api/rutinas/create`: Generación de nueva rutina semanal.
* `GET /api/ejercicios/history`: Consulta del historial de un ejercicio específico (Progreso).
* `POST /api/ejercicios/custom`: Creación de un ejercicio personalizado por el PF.
* *(Nota: Ver documentación técnica adjunta en mensaje privado que enviaremos como equipo para colección completa de Postman).*

### 📱 Recursos Nativos Integrados
Para mejorar la experiencia de usuario se implementaron:
1.  **Calendario:** Para la organización visual de las rutinas y planificación semanal.
2.  **Audio (Parlantes):** Utilizado en los temporizadores durante el entrenamiento.

---

## 🚀 Instrucciones de Ejecución

### Requisitos Previos
* Android Studio Koala o superior.
* JDK 17.
* Dispositivo Android o Emulador con API 26+.

### Pasos para levantar el proyecto (Modo Desarrollador)
1.  Clonar el repositorio.
2.  Abrir la carpeta raíz en Android Studio y esperar la sincronización de `Gradle`.
3.  **Configuración de Backend:** Asegurarse de que el servicio de Spring Boot esté ejecutándose y que la IP en el archivo de configuración apunte a la IP local de su máquina.
4.  Compilar y ejecutar en el emulador seleccionando el módulo `app`.

**Credenciales de Prueba:**
* **PF:** `diego@gmail.com` / `diego1902`
* **Alumno:** `marcelo123@gmail.com` / `marcelo123`

---

## 📦 Entregables (Firma Digital y APK)

El proyecto cumple con la normativa de entrega firmada digitalmente:

* **Descarga Web:** [https://ramp-chile.web.app](https://ramp-chile.web.app)
* **APK Firmado Local:** Archivo `app-release.apk` ubicado en la carpeta `/release` de este repositorio.
* **Keystore (.jks):** Archivo de llaves `fitcontrol-key.jks` ubicado en la raíz del proyecto.

---
*Evaluación Final Transversal - DSY1105 - 2025*
