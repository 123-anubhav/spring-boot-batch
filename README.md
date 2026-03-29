# spring-boot-batch
boot 2.x with java 1.8v batch processing


# how to wirte our custom logic with data and use it process steps

**where to apply logic**:

---

## 🔁 Spring Batch Flow (Important)

For each row in your CSV:

1. **Reader (`FlatFileItemReader`)**
   → Reads one line from file

2. **LineMapper**
   → Converts that line into a `Customer` object

3. **Processor (`ItemProcessor`) ✅ BEST PLACE FOR LOGIC**
   → Apply business logic / transformations

4. **Writer**
   → Saves processed object to DB

---

## ❗ Where should you process each row?

| Component           | Responsibility       | Should you add logic here? |
| ------------------- | -------------------- | -------------------------- |
| `lineMapper`        | Convert CSV → Object | ❌ NO (only mapping)        |
| `customerProcessor` | Business logic       | ✅ YES (correct place)      |

👉 **Answer:**
You should apply your row-level logic inside **`CustomerProcessor`**, NOT inside `lineMapper`.

---

## ✅ Why NOT `lineMapper`?

Because:

* It should only map fields (CSV → Java object)
* Keeps separation of concerns clean
* Easier testing & maintenance

---

## ✅ Correct Approach: Use `CustomerProcessor`

### 🔹 Step 1: Create Processor

```java
@Component
public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {

        // ✅ Example 1: Modify data
        customer.setFirstName(customer.getFirstName().toUpperCase());

        // ✅ Example 2: Filter rows (skip invalid data)
        if (customer.getEmail() == null || !customer.getEmail().contains("@")) {
            return null; // Spring Batch will skip this row
        }

        // ✅ Example 3: Custom logic
        if ("India".equalsIgnoreCase(customer.getCountry())) {
            customer.setCountry("IN");
        }

        return customer;
    }
}
```

---

## 🔹 Step 2: Your Step Already Uses Processor ✅

```java
@Bean
public Step step1() {
    return stepBuilderFactory.get("step-1")
            .<Customer, Customer>chunk(10)
            .reader(customerReader())
            .processor(customerProcessor()) // 👈 THIS IS WHERE LOGIC RUNS
            .writer(customerWriter())
            .build();
}
```

---

## 🔹 Step 3: Flow Example

CSV Row:

```
1,John,Doe,johnemail.com,Male,9999999999,India,1995-01-01
```

### Execution:

1. **Reader**
   → reads line

2. **LineMapper**
   → converts to:

```java
Customer{id=1, firstName="John", email="johnemail.com", country="India"}
```

3. **Processor**
   → modifies:

```java
Customer{id=1, firstName="JOHN", email="johnemail.com", country="IN"}
```

→ OR skips if invalid

4. **Writer**
   → saves to DB

---

## 🔥 BONUS: Skip Invalid Records

If you want to skip bad rows:

```java
@Bean
public Step step1() {
    return stepBuilderFactory.get("step-1")
            .<Customer, Customer>chunk(10)
            .reader(customerReader())
            .processor(customerProcessor())
            .writer(customerWriter())
            .faultTolerant()
            .skip(Exception.class)
            .skipLimit(100)
            .build();
}
```

---

## 🔥 BONUS: If You Really Want Custom Mapping Logic

Only do this if needed:

```java
fieldSetMapper.setFieldSetMapper(fieldSet -> {
    Customer customer = new Customer();
    customer.setFirstName(fieldSet.readString("firstName").trim());
    // ❌ avoid business logic here
    return customer;
});
```

---

## 🧠 Key Takeaways

✔ Each CSV row → 1 `Customer` object
✔ `lineMapper` → only mapping
✔ `CustomerProcessor` → **ALL business logic here**
✔ Return `null` → skip row
✔ Writer → saves processed data

---

## 🚀 Real-world Use Cases for Processor

* Data validation
* Transform fields
* Enrich data (API call)
* Filtering rows
* Formatting dates
* Calculations

---
