import java.io.*;
import java.util.*;

// Main 
public class StudentGradeSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static List<Student> students = new ArrayList<>();
    private static final String FILE_NAME = "students.dat";

    public static void  main(String [] args ){
        loadFromFile();

        while (true) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1 ->  addStudent();
                case 2 ->  viewAllStudents();
                case 3 ->  searchStudent();
                case 4 ->  updateStudent();
                case 5 ->  deleteStudent();
                case 6 ->  generateReport();
                case 7 ->  {
                    saveToFile();
                    System.out.println("Thank you for using Student Grade System. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    } 
    
       private static void displayMenu(){
        System.out.println("\n--- Student Grade System Menu ---");
        System.out.println("1. Add Student");
        System.out.println("2. View Students");
        System.out.println("3. Search Student");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Generate Report");
        System.out.println("7. Exit");
        System.out.println("----------------------------------");
       }

       // student class
    static class Student implements Serializable {
        private String id;
        private String name;
        private Map<String, Double> subjects;
        private double gpa;
        private String grade; 
        public Student(String name, String id) {
            this.name = name;
            this.id = id;
            this.subjects = new HashMap<>();
            initializeSubjects();
        }

        private void initializeSubjects() {
            subjects.put("Mathematics", 0.0);
            subjects.put("Physics", 0.0);
            subjects.put("Chemistry", 0.0);
            subjects.put("Programming", 0.0); 
            subjects.put("English", 0.0); 
        }
    //GPA calculation
    public void calculateGPA() {
        double total = 0.0;
        for (double marks : subjects.values()) {
            total += marks;
        }
        this.gpa = total / subjects.size();
        calculateGrade();
    }

    // Grade calculation
    private void calculateGrade() {
        if (gpa >= 90) {
            grade = "A+";
        } else if (gpa >= 80) {
            grade = "A";
        } else if (gpa >= 70) {
            grade = "B";
        } else if (gpa >= 60) {
            grade = "C";
        } else if (gpa >= 50) {
            grade = "D";
        } else {
            grade = "F";
        }
    }
   
    public void display(){
        System.out.println("\nStudent ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Subjects and Marks: ");
        subjects.forEach((subject, marks) -> 
            System.out.printf(" %-15s: %2f\n " ,subject , marks));
        System.out.printf("GPA: %.2f\n", gpa);
        System.out.println("Grade: " + grade);      
    }

   //Getter and setter methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getGpa() {
        return gpa;
    }

    public String getGrade() {
        return grade;
    }

    public void setMarks(String subject, double marks) {
        subjects.put(subject, Math.max(0, Math.min(100,marks)));
        calculateGPA();
    } 
}


// features Implementation
private static void addStudent() {
    System.out.println("\n--- Add New Student ---");
    String id = getStringInput("Enter Student ID: ");
    if (findStudentById(id) != null) {
        System.out.println("Student with this ID already exists!");
        return;
    }
    String name = getStringInput("Enter Student Name: ");
    Student student = new Student(name, id); // <-- Correct order
    for (String subject : student.subjects.keySet()) {
        double marks = getDoubleInput("Enter marks for " + subject + ": ");
        student.setMarks(subject, marks);
    }
    students.add(student);
    System.out.println("Student added successfully.");

}

private static void viewAllStudents() {
    if (students.isEmpty()) {
        System.out.println("No students found.");
        return;
    }
    System.out.println("\n---  All Students ---");
    System.out.printf("%-10s %-20s %-10s %-10s\n", "ID", "Name", "GPA", "Grade");
    System.out.println("-".repeat(45));
    for (Student student : students) {
        System.out.printf("%-10s %-20s %-10.2f %-10s\n", 
            student.getId(), student.getName(), student.getGpa(), student.getGrade());
    }

}

private static void searchStudent() {
    System.out.println("\n--- Search Student ---");
    System.out.println("1. Search by ID");
    System.out.println("2. Search by Name");
    int choice = getIntInput("Enter your choice: ");

    switch(choice){
        case 1 -> {
            String id = getStringInput("Enter Student ID: ");
            Student student = findStudentById(id);
            if (student != null) {
                student.display();
            } else {
                System.out.println("Student not found.");
            }
        }
        case 2 -> {
            String name = getStringInput("Enter Student Name: ");
            List<Student> results = students.stream().filter(s -> s.getName().toLowerCase().contains(name.toLowerCase())).toList();
            if (results.isEmpty()) {
               System.out.println("No students found with that name.");
                }
             else {
                results.forEach(Student::display);
            }
        }
        default -> System.out.println("Invalid choice. Please try again.");
        }
    }


 private static void updateStudent() {
        String id = getStringInput("Enter Student ID to update: ");
        Student student = findStudentById(id);
        
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }
        
        System.out.println("\nCurrent marks:");
        student.display();
        
        System.out.println("\nEnter new marks (press Enter to skip):");
        for (String subject : student.subjects.keySet()) {
            System.out.print("Enter marks for " + subject + " [" + 
                student.subjects.get(subject) + "]: ");
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
                try {
                    double marks = Double.parseDouble(input);
                    student.setMarks(subject, marks);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Keeping old value.");
                }
            }
        }
        
        System.out.println("Student record updated successfully!");
    }

    private static void deleteStudent() {
        String id = getStringInput("Enter Student ID to delete: ");
        Student student = findStudentById(id);
        
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }
        
        System.out.print("Are you sure you want to delete " + student.getName() + "? (yes/no): ");
        String confirmation = scanner.nextLine();
        
        if (confirmation.equalsIgnoreCase("yes")) {
            students.remove(student);
            System.out.println("Student deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void generateReport() {
        if (students.isEmpty()) {
            System.out.println("No students to generate report!");
            return;
        }
        
        System.out.println("\n--- CLASS REPORT ---");
        System.out.println("Total Students: " + students.size());
        
        // Calculate statistics
        double totalGPA = 0;
        Map<String, Integer> gradeDistribution = new HashMap<>();
        String[] grades = {"A+", "A", "B", "C", "D", "F"};
        
        for (String grade : grades) {
            gradeDistribution.put(grade, 0);
        }
        
        for (Student student : students) {
            totalGPA += student.getGpa();
            gradeDistribution.put(student.getGrade(), 
                gradeDistribution.get(student.getGrade()) + 1);
        }
        
        System.out.printf("Average GPA: %.2f\n", totalGPA / students.size());
        
        // Find top student
        Student topStudent = students.stream()
            .max(Comparator.comparingDouble(Student::getGpa))
            .orElse(null);
            
        if (topStudent != null) {
            System.out.println("\nTop Student:");
            System.out.println("  Name: " + topStudent.getName());
            System.out.printf("  GPA: %.2f\n", topStudent.getGpa());
        }
        
        // Grade distribution
        System.out.println("\nGrade Distribution:");
        for (String grade : grades) {
            int count = gradeDistribution.get(grade);
            if (count > 0) {
                System.out.printf("  %s: %d students (%.1f%%)\n", 
                    grade, count, (count * 100.0 / students.size()));
            }
        }
    }

    // Helper methods
    private static Student findStudentById(String id) {
        return students.stream()
            .filter(s -> s.getId().equalsIgnoreCase(id))
            .findFirst()
            .orElse(null);
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    // File handling methods
    private static void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FILE_NAME))) {
            oos.writeObject(students);
            System.out.println("Data saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_NAME))) {
            students = (List<Student>) ois.readObject();
            System.out.println("Data loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
            students = new ArrayList<>();
        }
    }
}
