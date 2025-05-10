package base64;

import java.io.*;
import java.util.ArrayList;

public class Base64Tester3 {

    public static void main(String[] args) {
        try {
            // (1) Crear una lista de objetos Person
            ArrayList<Person> personList = new ArrayList<>();
            personList.add(new Person("Andres", 29, 1.78));
            personList.add(new Person("Isabella", 25, 1.65));
            personList.add(new Person("Carlos", 33, 1.82));

            // (2) Serializar la lista a byte[]
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(personList);
            oos.close();
            byte[] serializedBytes = baos.toByteArray();

            // (3) Codificar el byte[] en Base64 usando la clase personalizada
            String base64Encoded = Base64.encode(serializedBytes);

            // (4) Imprimir cadena Base64 codificada
            System.out.println("Base64 encoded list: \n" + base64Encoded);

            // (5) Decodificar la cadena en byte[] usando la clase personalizada
            byte[] decodedBytes = Base64.decode(base64Encoded);

            // (6) Deserializar el contenido a una lista
            ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            ArrayList<Person> deserializedList = (ArrayList<Person>) ois.readObject();
            ois.close();

            // (7) Imprimir la lista deserializada
            System.out.println("\nDeserialized list:");
            for (Person p : deserializedList) {
                System.out.println(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
