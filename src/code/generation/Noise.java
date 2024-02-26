package code.generation;

import java.util.Random;

public abstract class Noise {

  /**
   * Generates a {@code float} array with values pseudorandomly chosen between {@code -1f} and {@code 1f}.
   * 
   * @param size the length of the array to generate
   * 
   * @return an array of size {@code size} containing random values between {@code -1f} and {@code 1f}.
   */
  public static float[] randomNoise(int size) {
    return seededNoise(new Random(), size);
  }

  /**
   * Generates a {@code float} array with values pseudorandomly chosen between {@code -1f} and {@code 1f}. 
   * Uses a given random number generator.
   * 
   * @param random the random number generator to use to generate the array.
   * @param size the length of the array to generate
   * 
   * @return an array of size {@code size} containing random values between {@code -1f} and {@code 1f}.
   */
  public static float[] seededNoise(Random random, int size) {
    float[] res = new float[size];
    for (int i = 0; i < res.length; i++) {
      res[i] = random.nextFloat(2)-1;
    }

    return res;
  }

  /**
   * Takes a random sample of noise from a given set of values between {@code -1f} and {@code 1f}.
   * 
   * @param random the random number generator to use in picking the sample.
   * @param original a {@code float} array to sample from.
   * @param size the size of the sample. Must be no larger than the length of the original array.
   * 
   * @return a sample of noise taken from a larger set.
   */
  public static float[] sampleNoise(Random random, float[] original, int size) {
    if (size > original.length) return original;

    float[] res = new float[size];
    int off = random.nextInt(original.length-res.length);

    for (int i = 0; i < res.length; i++) {
      res[i] = original[i + off];
    }

    return res;
  }
}
