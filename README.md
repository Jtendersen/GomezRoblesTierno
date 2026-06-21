# HogarYA

Sistema web para la gestión de alquileres de la inmobiliaria HogarYA.
TP Integrador — DESI 2026 — UTN FRSF.

---

## Lo que necesitás tener instalado

- Java 17
- Maven
- MySQL (con el servidor corriendo en localhost:3306)

---

## Cómo levantar el proyecto

### 1. Clonar el repo

```bash
git clone https://github.com/Jtendersen/GomezMendozaRoblesTierno.git
cd GomezMendozaRoblesTierno
```

### 2. Configurar la base de datos

Abrí el archivo `src/main/resources/application.properties` y fijate que el usuario y contraseña de MySQL sean los tuyos:

```properties
spring.datasource.username=root
spring.datasource.password=root
```

Si tu MySQL tiene otra contraseña, cambiála ahí. La base de datos `GomezMendozaRoblesTierno` se crea sola la primera vez que levantás el proyecto.

### 3. Correr la aplicación

```bash
./mvnw spring-boot:run
```

En Windows:

```bash
mvnw.cmd spring-boot:run
```

O desde el IDE: correr la clase `HogaryaApplication.java` como Spring Boot App.

### 4. Abrir en el navegador

```
http://localhost:8080
```

---

## Flujo de trabajo en Git

**Nunca trabajar directo en `main`.** Está protegido y va a rechazar cualquier push directo.

### Pasos para subir cambios

```bash
# 1. Antes de arrancar a trabajar, siempre actualizar main
git checkout main
git pull origin main

# 2. Crear una rama para lo que vas a hacer
git checkout -b nombre-de-tu-rama

# 3. Hacer los cambios, commitear
git add archivo1.java archivo2.html
git commit -m "descripcion breve de lo que hiciste"

# 4. Subir la rama a GitHub
git push origin nombre-de-tu-rama

# 5. Abrir un Pull Request desde GitHub y pedirle review a alguien
```

### Nombrar las ramas

Algo descriptivo y sin espacios, por ejemplo:
- `feature/abmc-publicaciones`
- `fix/validacion-contrato`
- `feature/listado-facturas`

### Antes de mergear

- Tiene que tener al menos un review aprobado
- Resolver los conflictos si los hay antes de pedir el merge

### Lo que NO hay que hacer

- No descargar el zip del repo desde GitHub y subir los archivos desde ahí
- No commitear la carpeta `target/`
- No pushear directo a `main`
