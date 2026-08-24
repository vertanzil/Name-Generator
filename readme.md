# NameCombiner
A high‑performance Java utility for generating large volumes of unique full names by combining male/female first‑name lists with family names.  
Built for speed, safety, and scalability — ideal for simulations, testing datasets, game development, anonymization, and synthetic identity generation.

---

## 📛 Overview
NameCombiner generates unique, unused, and collision‑safe full names using multi‑threaded processing.  
It reads from text files, validates feasibility, generates names concurrently, writes results to an output file, and appends them to a persistent used_names.txt to prevent future reuse.

---

## 🏷️ Badges
![Java](https://img.shields.io/badge/Java-17+-blue)
![Build](https://img.shields.io/badge/Build-Maven-orange)
![License](https://img.shields.io/badge/License-MIT-green)
![Status](https://img.shields.io/badge/Status-Active-success)

---

## ✨ Features
- Multi‑threaded name generation using all CPU cores
- Guaranteed uniqueness via ConcurrentHashMap.newKeySet()
- Live progress bar for large batches
- Automatic deduplication of input lists
- Persistent tracking of previously used names
- Feasibility validation before generation
- Configurable input/output paths
- Efficient enough to generate tens of thousands of names quickly

---

## 📁 File Structure

src/main/resources/  
├── male_first_names.txt  
├── female_first_names.txt  
├── family_names.txt  
├── used_names.txt  
└── combined_names.txt

---

## 🚀 How It Works
1. Loads all input files
2. Removes duplicate entries
3. Loads previously used names
4. Calculates total possible combinations
5. Ensures enough unused names exist
6. Spawns multiple worker threads
7. Each thread:
    - Selects a random first name
    - Selects a random surname
    - Combines them into "First Last"
    - Checks against used_names.txt
    - Adds to a thread‑safe set
8. Writes all generated names to combined_names.txt
9. Appends them to used_names.txt

---

## ⚙️ Configuration

Inside main():

int numberToGenerate = 50000;  
final int MAX_ATTEMPTS = 10_000_000;  
final int THREADS = Math.max(4, Runtime.getRuntime().availableProcessors());

---

## 🧵 Multithreading
Uses ExecutorService, ConcurrentHashMap.newKeySet(), and AtomicInteger for safe concurrent generation.

---

## 📊 Progress Bar

[██████████████████████████          ] 25000/50000  
Completed!

---

## 📝 Example Output

James Carter  
Sophia Nguyen  
Liam Patel  
Emma Johnson  
...

---

## 📦 Running the Program

### Maven
mvn compile exec:java -Dexec.mainClass="io.github.vertanzil.NameCombiner"

### Direct Java Execution
javac NameCombiner.java  
java io.github.vertanzil.NameCombiner

---

## 📄 License
MIT License

---

## 🤝 Contributing
Contributions are welcome.  
Open issues or submit pull requests for improvements or new features.
