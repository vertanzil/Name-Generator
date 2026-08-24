# NameGenerator
A high‑performance Java utility for generating large volumes of unique, unused full names by combining first‑name lists (male + female) with family names.  
It supports multi‑threaded generation, collision handling, progress visualization, and automatic tracking of previously used names.

---

## ✨ Features
- Multi‑threaded name generation using a dynamic thread pool
- Collision‑safe uniqueness enforcement with `ConcurrentHashMap.newKeySet()`
- Automatic feasibility checking before generation
- Terminal progress bar for large batches
- Deduplication of input name lists
- Automatic append to `used_names.txt`
- Configurable input/output files
- Efficient generation of tens of thousands of names

---

## 📁 File Structure
The program expects the following resource files:


### Input Files
- `male_first_names.txt` — male first names
- `female_first_names.txt` — female first names
- `family_names.txt` — surnames
- `used_names.txt` — previously generated names

### Output File
- `combined_names.txt` — newly generated unique names

---

## 🚀 How It Works
1. Loads all input files
2. Removes duplicate entries
3. Loads previously used names
4. Calculates total possible combinations
5. Ensures enough unused names exist
6. Spawns multiple worker threads
7. Each thread:
    - Randomly selects a first name
    - Randomly selects a surname
    - Combines them into `"First Last"`
    - Checks against `used_names.txt`
    - Adds to a thread‑safe set
8. Writes all generated names to `combined_names.txt`
9. Appends them to `used_names.txt`

---

## ⚙️ Configuration
Inside `main()`:

```java
int numberToGenerate = 50000;
final int MAX_ATTEMPTS = 10_000_000;
final int THREADS = Math.max(4, Runtime.getRuntime().availableProcessors());
