package utils;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomUtils {

    private static Random random = new Random();

    public static void shuffle(List<?> collection) {
        Collections.shuffle(collection, random);
    }

}