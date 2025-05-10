package base64;

import java.io.*;

public class Base64Tester2 {

    public static void main(String[] args) {
        try {
            // (1) Crear objeto
            Person person = new Person("Francisco", 29, 1.78);

            // (2) Imprimir objeto original
            System.out.println("Original object: " + person);

            // (3) Serializar objeto a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(person);
            oos.close();
            byte[] serializedBytes = baos.toByteArray();

            // (4) Codificar en Base64 usando la clase personalizada
            String base64Encoded = Base64.encode(serializedBytes);

            // (5) Imprimir cadena Base64
            System.out.println("Base64 encoded: " + base64Encoded);

            // (6) Decodificar Base64 a byte[] usando la clase personalizada
            byte[] decodedBytes = Base64.decode(base64Encoded);

            // (7) Deserializar a objeto Person
            ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Person deserializedPerson = (Person) ois.readObject();
            ois.close();

            // (8) Imprimir objeto deserializado
            System.out.println("Deserialized object: " + deserializedPerson);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
