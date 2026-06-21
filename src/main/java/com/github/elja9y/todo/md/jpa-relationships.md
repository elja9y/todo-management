# JPA Relationships — Real Life Guide

---

## 1. One-to-One — `User` ↔ `UserProfile`

**Scenario:** Every user has exactly one profile. A profile belongs to exactly one user. Deleting the user should delete the profile too.

**Who holds the FK?** `User` — `profile_id` column lives in the `users` table.

```java
// User.java — owning side (has the FK)
@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JoinColumn(name = "profile_id", referencedColumnName = "id")
private UserProfile profile;

// UserProfile.java — inverse side (optional, only if you need to navigate back)
@OneToOne(mappedBy = "profile")
private User user;
```

**DB result:**
```
users:        id | name | profile_id
              1  | Ahmed | 5

user_profiles: id | bio       | avatar_url
               5  | "Dev..." | "img.png"
```

**Cascade effect:**
```java
// Saving user also saves profile automatically
userRepository.save(user); // profile gets inserted too

// Deleting user also deletes profile
userRepository.delete(user); // profile row gone too
```

**Fetch LAZY effect:**
```java
User user = userRepository.findById(1L);
// SQL: SELECT * FROM users WHERE id = 1
// profile NOT loaded yet

user.getProfile(); // triggers second SQL only here
// SQL: SELECT * FROM user_profiles WHERE id = 5
```

**When to use:** passport ↔ person, order ↔ invoice, employee ↔ contract.

---

## 2. One-to-Many / Many-to-One — `Customer` ↔ `Order`

**Scenario:** One customer can place many orders. Each order belongs to exactly one customer.

**Who holds the FK?** `Order` — `customer_id` lives in the `orders` table.

```java
// Customer.java — one side
@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL,
           fetch = FetchType.LAZY, orphanRemoval = true)
private List<Order> orders = new ArrayList<>();

// Order.java — many side (owning, has the FK)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "customer_id", nullable = false)
private Customer customer;
```

**DB result:**
```
customers: id | name
           1  | Ahmed

orders: id | total  | customer_id
        10 | 250.00 | 1
        11 | 80.00  | 1
        12 | 430.00 | 1
```

**orphanRemoval effect:**
```java
customer.getOrders().remove(order); // order row deleted from DB on save
customerRepository.save(customer);
```
Without `orphanRemoval`, removing from the list just breaks the link in memory — the row stays in the DB.

**CascadeType.ALL effect:**
```java
Order o1 = new Order(250.00);
Order o2 = new Order(80.00);
customer.setOrders(List.of(o1, o2));
customerRepository.save(customer); // saves customer + both orders in one call
```

**When to use:** author ↔ books, department ↔ employees, blog ↔ posts.

---

## 3. Many-to-One only (no back-reference) — `Comment` → `Post`

**Scenario:** Comments belong to a post. You never need `post.getComments()` — you always query comments by post ID directly.

**Who holds the FK?** `Comment` — `post_id` lives in the `comments` table.

```java
// Comment.java — owning side only, no @OneToMany on Post
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "post_id", nullable = false)
private Post post;

// Post.java — nothing, no relationship field
```

**Why not add `@OneToMany` on Post?**
- You might have thousands of comments per post
- Loading `post.getComments()` would pull all of them into memory
- Instead, query directly: `commentRepository.findByPostId(postId)`

```java
// CommentRepository.java
List<Comment> findByPostId(Long postId);
```

**When to use:** any child entity with potentially large collections where you only ever query from the child side.

---

## 4. Many-to-Many — `Student` ↔ `Course`

**Scenario:** A student enrolls in many courses. A course has many students. No extra data on the enrollment itself.

**Who holds the join table?** `Student` — convention is to put it on the side you query from most.

```java
// Student.java — owning side
@ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
@JoinTable(name = "student_courses",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id"))
private Set<Course> courses = new HashSet<>();

// Course.java — inverse side (optional back-reference)
@ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
private Set<Student> students = new HashSet<>();
```

**DB result:**
```
students: id | name
          1  | Ahmed
          2  | Sara

courses: id | title
         10 | "Spring Boot"
         11 | "Docker"

student_courses: student_id | course_id
                 1          | 10
                 1          | 11
                 2          | 10
```

**Why `Set` not `List`?**
A student shouldn't enroll in the same course twice. `Set` enforces uniqueness.

**Why NOT `CascadeType.ALL` here?**
```java
// DANGER with CascadeType.ALL:
studentRepository.delete(ahmed);
// This would delete "Spring Boot" course from DB
// Sara is still enrolled in it — now her enrollment is broken
```
Use only `PERSIST` and `MERGE` — never `REMOVE` on shared entities.

**Cascade PERSIST effect:**
```java
Course newCourse = new Course("Kubernetes");
ahmed.getCourses().add(newCourse);
studentRepository.save(ahmed); // saves ahmed + inserts new course row + join row
```

**When to use:** tags ↔ articles, doctors ↔ patients, actors ↔ movies.

---

## 5. Many-to-Many with Extra Data — `Employee` ↔ `Project` via `Assignment`

**Scenario:** An employee works on many projects, a project has many employees. But each assignment has a `role` and `startDate` — extra data on the relationship itself.

**Solution:** Promote the join table to a real entity.

```java
// Assignment.java — the join entity
@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String role;         // extra data
    private LocalDate startDate; // extra data
}

// Employee.java
@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
private List<Assignment> assignments = new ArrayList<>();

// Project.java
@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
private List<Assignment> assignments = new ArrayList<>();
```

**DB result:**
```
assignments: id | employee_id | project_id | role       | start_date
             1  | 1           | 10         | "Backend"  | 2024-01-15
             2  | 1           | 11         | "DevOps"   | 2024-03-01
             3  | 2           | 10         | "Frontend" | 2024-01-15
```

**When to use:** whenever the relationship itself has attributes — enrollment + grade, booking + seat number, order item + quantity + price.

---

## Fetch Type Summary

| FetchType | Loads when | Best for |
|-----------|-----------|---------|
| `LAZY` | Only when you call the getter | Collections, large data, performance-sensitive |
| `EAGER` | Always with the parent | Small sets needed on every load (e.g. roles for auth) |

> **Default behavior:**
> - `@OneToOne` and `@ManyToOne` → EAGER by default
> - `@OneToMany` and `@ManyToMany` → LAZY by default
>
> Always set fetch type explicitly — don't rely on defaults.

---

## Cascade Type Summary

| CascadeType | Effect |
|-------------|--------|
| `PERSIST` | Saving parent saves children |
| `MERGE` | Updating parent updates children |
| `REMOVE` | Deleting parent deletes children |
| `ALL` | All of the above |
| `DETACH` | Detaching parent detaches children |

**Safe rules:**
- `CascadeType.ALL` → safe only when child has no meaning without parent (profile, address)
- Never use `REMOVE` or `ALL` on shared entities (roles, courses, tags) — deleting one owner destroys data for others
- Default for many-to-many: `{CascadeType.PERSIST, CascadeType.MERGE}` only

---

## Which Side Owns the Relationship?

The **owning side** is the one without `mappedBy` — it controls the FK or join table.

| Relation | FK lives in | Owning side |
|----------|------------|-------------|
| `@OneToOne` | table of the side with `@JoinColumn` | side with `@JoinColumn` |
| `@OneToMany` / `@ManyToOne` | child table (`@ManyToOne` side) | `@ManyToOne` side |
| `@ManyToMany` | join table | side with `@JoinTable` |

Changes to the inverse side (`mappedBy`) are **ignored by JPA** — always update the owning side to persist relationship changes.
