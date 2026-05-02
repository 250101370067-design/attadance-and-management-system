# Student Attendance Management System (Java Swing + Serialization)

MVP desktop application built with pure Java and Swing, persisting data via Java Serialization (.dat files). No external database required.

## Run

1. Ensure you have Java 17+ installed (`java -version`).
2. Compile:
```
javac -d out -sourcepath src src/attendance/App.java
```
3. Run:
```
java -cp out attendance.App
```

Data files are stored under `data/` in `.dat` format. On first run, you'll be prompted to create the Admin account.

## Structure

```
src/
  attendance/
    App.java
    model/
    service/
    ui/
    util/
data/
```

## Notes
- CSV import/export supported for MVP (Excel can be saved as CSV).
- All data is serialized; delete `data/` to reset.


