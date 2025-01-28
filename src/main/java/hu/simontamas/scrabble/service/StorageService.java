package hu.simontamas.scrabble.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@RequiredArgsConstructor
public class StorageService {

    public void serializeDataOut(String path, Object obj) throws IOException {
        FileOutputStream foS = new FileOutputStream(path);
        ObjectOutputStream ooS = new ObjectOutputStream(foS);
        ooS.writeObject(obj);
        ooS.close();
    }

    public Object serializeDataIn(String path) throws IOException, ClassNotFoundException {
        FileInputStream fiS = new FileInputStream(path);
        ObjectInputStream oiS = new ObjectInputStream(fiS);
        Object obj = oiS.readObject();
        oiS.close();
        return obj;
    }
}
