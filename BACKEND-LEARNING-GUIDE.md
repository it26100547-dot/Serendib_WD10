# Backend Learning Guide

## Start with this one sentence

**The frontend sends an HTTP request, the Controller receives it, the Service decides what to do, the Repository reads/writes the data file, and the result returns to the frontend as JSON.**

## Tour Package CREATE example

Admin clicks Save:

```text
1. JavaScript sends POST /api/packages
2. Spring Boot finds TourPackageController.create()
3. Controller receives JSON as a TourPackage object
4. Controller calls TourPackageService.create()
5. Service validates name and price
6. Service calls TourPackageRepository.save()
7. Repository gives the package a new ID
8. Repository writes packages.txt
9. Saved TourPackage returns to Service
10. Service returns it to Controller
11. Controller sends JSON to the browser
```

## Tour Package READ example

```text
GET /api/packages
        ↓
TourPackageController.getAll()
        ↓
TourPackageService.filter()
        ↓
TourPackageRepository.findAll()
        ↓
packages.txt
```

## Tour Package UPDATE example

```text
PUT /api/packages/3
        ↓
Controller gets id = 3 and the changed TourPackage
        ↓
Service checks package 3 exists
        ↓
Repository replaces package 3 in packages.txt
```

## Tour Package DELETE example

```text
DELETE /api/packages/3
        ↓
Controller
        ↓
Service.delete(3)
        ↓
Repository.deleteById(3)
        ↓
packages.txt
```

## Spring annotations you should know

- `@SpringBootApplication` — starts the Spring Boot application.
- `@RestController` — class that receives API requests.
- `@RequestMapping` — base URL for a controller.
- `@GetMapping` — handles GET/read.
- `@PostMapping` — handles POST/create.
- `@PutMapping` — handles PUT/update.
- `@DeleteMapping` — handles DELETE.
- `@RequestBody` — converts incoming JSON into a Java object.
- `@PathVariable("id")` — reads an ID from a URL such as `/api/packages/5`.
- `@RequestParam` — reads a filter from a URL such as `/api/packages?category=Adventure`.
- `@Service` — business-logic class.
- `@Repository` — data-access class.

## Files you should understand before your viva

For your Tour Package section, understand these deeply:

```text
TourPackage.java
TourPackageController.java
TourPackageService.java
TourPackageRepository.java
packages.txt
```

Then understand these common files at a basic level:

```text
SerendibApplication.java
SessionService.java
ApiResponse.java
application.properties
pom.xml
js/api.js
```

You do not need to memorize every line. You should be able to explain what each file is responsible for and trace one CRUD request from beginning to end.
