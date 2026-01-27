# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

LifeHubServer is a reactive Spring Boot application implementing a comprehensive RBAC (Role-Based Access Control) system with user management, permission control, and menu-based navigation.

**Tech Stack**: Spring Boot 3.5.9, Java 17, Spring WebFlux (reactive), Spring Security, MyBatis-Plus, PostgreSQL, Redis, JWT authentication

## Development Commands

```bash
# Build and run (default profile: dev)
mvn spring-boot:run

# Build with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Clean build
mvn clean compile

# Run tests
mvn test

# Package application
mvn package

# Skip tests during build
mvn clean package -DskipTests
```

## Architecture Overview

### Reactive Programming Model
This application uses **Spring WebFlux** (reactive) throughout. All controllers return reactive types (`Mono<T>` or `Flux<T>`), not blocking types. When adding new endpoints, follow the reactive pattern:
- Controllers return `Mono<ResponseResult<T>>` for single results
- Use `SecurityUtils.getCurrentUserId()` which returns `Mono<Long>`
- Service methods should be non-blocking and return reactive types

### Layered Architecture
```
Controller Layer (REST endpoints)
    ↓
Service Layer (Business logic - interface + impl pattern)
    ↓
Mapper Layer (MyBatis-Plus for database operations)
    ↓
Database (PostgreSQL)
```

### Security Architecture

**JWT-based Authentication Flow:**
1. User logs in via `/auth/login` → returns JWT token
2. JwtAuthenticationFilter validates token on each request
3. User ID stored in SecurityContext as Principal (Long type)
4. Use `SecurityUtils.getCurrentUserId()` to get current user

**Authorization Model:**
- **Super Admin**: Role code `SUPER_ADMIN` gets all permissions automatically
- **Role-Based**: Normal users get permissions through role assignments
- **Permission Key Pattern**: `{app}:{module}:{action}:{resource}` (e.g., `SYS:user:query`)
- **Authority Prefix**: All roles prefixed with `ROLE_` in Spring Security

**White-listed Endpoints** (no authentication required):
- `/auth/**` - Login endpoints
- `/public/**` - Public resources
- `/ws/**` - WebSocket endpoints

### Database Schema Patterns

**Common Conventions:**
- **Soft deletion**: `is_deleted` (0=active, 1=deleted) or `deleted_at` timestamp
- **Audit fields**: `created_at`, `updated_at`, `created_by`, `updated_by`
- **Hierarchical data**: `parent_id` for tree structures (menus, permissions)
- **Multi-tenancy**: `app_code` field to scope data by application

**Key Entity Relationships:**
```
User ←→ UserRole ←→ Role ←→ RolePermission ←→ Permission
                              ↓
                         PermissionMenu ←→ Menu
```

### MyBatis-Plus Configuration
- Mapper XML locations: `classpath*:mapper/**/*.xml`
- SQL logging enabled in dev profile (see `application-dev.properties`)
- Pagination support via MyBatis-Plus `Page<T>`

### Redis Usage
- Session storage and caching
- User role information cached after login for performance
- Configuration: `application-dev.properties`

## Adding New Features

### 1. Adding a New System Module
Follow the existing structure in `src/main/java/com/xhn/sys/`:
```
sys/
├── {module}/
│   ├── model/           # Entity classes (extending Base{Module})
│   ├── mapper/          # MyBatis-Plus mapper interface
│   ├── service/         # Service interface
│   ├── service/impl/    # Service implementation
│   └── controller/      # REST controller
```

### 2. Creating CRUD Endpoints
Reference existing controllers (e.g., `SysRoleController`, `SysPermissionController`):
- Use standard HTTP methods: `@PostMapping`, `@DeleteMapping`, `@PutMapping`, `@GetMapping`
- Return `ResponseResult<T>` wrapper for consistent API responses
- Add validation using `@Valid` annotation on request DTOs

### 3. Implementing User-Specific Data
When querying data for the current user:
```java
@GetMapping("/user/current")
public Mono<ResponseResult<YourDTO>> getCurrentUserData() {
    return SecurityUtils.getCurrentUserId()
        .map(userId -> yourService.getDataByUserId(userId))
        .map(ResponseResult::success);
}
```

### 4. Handling Super Admin Logic
For features where super admins should see all data:
```java
boolean isSuperAdmin = roles.stream()
    .anyMatch(role -> SecurityConstants.SUPER_ADMIN_ROLE_CODE.equals(role.getRoleCode()));

if (isSuperAdmin) {
    // Return all data
    data = baseMapper.selectList(null);
} else {
    // Return user-specific data
    data = baseMapper.selectDataByUserId(userId);
}
```

### 5. Custom Mapper Queries
For complex queries, add methods to mapper interface and implement in XML:
```java
// Mapper interface
List<YourEntity> selectCustomDataByUserId(Long userId);
```
```xml
<!-- Mapper XML at src/main/resources/mapper/sys/{Module}Mapper.xml -->
<select id="selectCustomDataByUserId" resultType="com.xhn.sys.{module}.model.YourEntity">
    SELECT * FROM your_table WHERE user_id = #{userId}
</select>
```

## Important Constants

**Security Constants** (`com.xhn.base.constants.SecurityConstants`):
- `SUPER_ADMIN_ROLE_CODE` = "SUPER_ADMIN"
- `ADMIN_ROLE_CODE` = "ADMIN"
- `ROLE_PREFIX` = "ROLE_"
- `ALL_PERMISSIONS_AUTHORITY` = "*:*:*"

**Utility Classes**:
- `SecurityUtils` - Get current user info in reactive context
- `JwtUtil` - JWT token operations
- `ResponseResult` - Standard API response wrapper

## Configuration Files

- **`application.properties`** - Main configuration (active profile, port, logging)
- **`application-dev.properties`** - Dev profile (database, Redis, JWT secret)
- **`logback-spring.xml`** - Logging configuration (SQL separated to dedicated file)
- **`pom.xml`** - Maven dependencies and build configuration

## Database Connection

**Development Database**: PostgreSQL at `120.78.0.54:6252/LifeHub`

## Common Patterns

### Service Implementation Pattern
```java
@Slf4j
@Service
@RequiredArgsConstructor  // Lombok for constructor injection
public class YourServiceImpl extends ServiceImpl<YourMapper, YourEntity> implements YourService {
    private final DependencyService dependencyService;

    @Override
    public YourResult yourMethod(YourParam param) {
        // Business logic
    }
}
```

### Controller Endpoint Pattern
```java
@RestController
@RequestMapping("/sys/your-module")
@RequiredArgsConstructor
public class YourController {
    private final YourService yourService;

    @GetMapping
    public ResponseResult<List<YourEntity>> list() {
        return ResponseResult.success(yourService.list());
    }
}
```

### Reactive Security Pattern
When you need the current user ID in a service or controller:
```java
SecurityUtils.getCurrentUserId()
    .flatMap(userId -> {
        // Do something with userId
        return yourService.doSomething(userId);
    })
```
