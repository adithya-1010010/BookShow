# Inheritance

## Person (abstract) -> Customer

```java
public abstract class Person {
    protected String name;
    protected String phone;
    public abstract String getRole();
}

public class Customer extends Person {
    private String email; // optional, if collected
    @Override
    public String getRole() { return "CUSTOMER"; }
}
```

**Why this is genuine, not contrived:** A Customer IS-A Person — it shares identity fields (name, phone) with any future person-type the system might need (e.g., a `Staff` class if admin features are added later per `future-improvements.md`). This is a natural single-level hierarchy, not an artificial one built solely to satisfy the OOP requirement.

No deeper inheritance chain is introduced, because the domain does not call for one — forcing additional levels would violate the "do not force OOP concepts into meaningless places" rule.
