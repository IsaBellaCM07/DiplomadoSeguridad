package base64;

import java.io.Serializable;

public class Person implements Serializable {
    private String name;
    private int age;
    private double height;

    public Person() {}

    public Person(String name, int age, double height) {
        this.name = name;
        this.age = age;
        this.height = height;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getHeight() {
        return height;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    // toString
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + ", height=" + height + "}";
    }
}
