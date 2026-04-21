package util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Image {
    public static int RED = 0;
    public static int GREEN = 1;
    public static int BLUE = 2;

    /**
     * Static method for openning an image from file and return it as matrix of pixels.
     * @param path Path to the image file.
     */
    public static int[][][] loadImage(String path) throws IOException {
        int pixels[][][] = null;

        BufferedImage image = ImageIO.read(new File(path));
        pixels = new int[image.getHeight()][image.getWidth()][3];
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                Color color = new Color(image.getRGB(j, i));
                pixels[i][j][RED] = color.getRed();
                pixels[i][j][GREEN] = color.getGreen();
                pixels[i][j][BLUE] = color.getBlue();
            }
        }

        return pixels;
    }

    public static int[][] toGray(int[][][] color_pixels) throws IOException {
        int pixels[][] = null;

        pixels = new int[color_pixels.length][color_pixels[0].length];
        for (int i = 0; i < color_pixels.length; i++) {
            for (int j = 0; j < color_pixels[0].length; j++) {
                pixels[i][j] = (int) ((color_pixels[i][j][RED] + color_pixels[i][j][GREEN] + color_pixels[i][j][BLUE]) / 3);
            }
        }

        return pixels;
    }

    public static int[][][] fromGray(int[][] gray_pixels){
        int pixels[][][] = null;

        pixels = new int[gray_pixels.length][gray_pixels[0].length][3];
        for (int i = 0; i < gray_pixels.length; i++) {
            for (int j = 0; j < gray_pixels[0].length; j++) {
                pixels[i][j][RED] = gray_pixels[i][j];
                pixels[i][j][GREEN] = gray_pixels[i][j];
                pixels[i][j][BLUE] = gray_pixels[i][j];
            }
        }

        return pixels;
    }

    public static int[][] getChannel(int[][][] color_pixels, int channel) {
        int pixels[][] = null;

        pixels = new int[color_pixels.length][color_pixels[0].length];
        for (int i = 0; i < color_pixels.length; i++) {
            for (int j = 0; j < color_pixels[0].length; j++) {
                pixels[i][j] = color_pixels[i][j][channel];
            }
        }

        return pixels;
    }

    public static int[][][] setChannel(int[][][] color_pixels, int[][] channel, int channel_index) {
        for (int i = 0; i < color_pixels.length; i++) {
            for (int j = 0; j < color_pixels[0].length; j++) {
                color_pixels[i][j][channel_index] = channel[i][j];
            }
        }

        return color_pixels;
    }

    public static void saveImage(String path, int[][][] pixels) throws IOException {
        BufferedImage image = new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                int red = (int) pixels[i][j][RED];
                int green = (int) pixels[i][j][GREEN];
                int blue = (int) pixels[i][j][BLUE];
                Color color = new Color(red, green, blue);
                image.setRGB(j, i, color.getRGB());
            }
        }
        String format = path.substring(path.lastIndexOf('.') + 1);
        ImageIO.write(image, format, new File(path));
    }

    public static int[][][] copy(int[][][] pixels) {
        int[][][] copy = new int[pixels.length][pixels[0].length][3];
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                copy[i][j][RED] = pixels[i][j][RED];
                copy[i][j][GREEN] = pixels[i][j][GREEN];
                copy[i][j][BLUE] = pixels[i][j][BLUE];
            }
        }
        return copy;
    }

    public static int[][][] solidColor(int width, int height, int red, int green, int blue) {
        int[][][] pixels = new int[height][width][3];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixels[i][j][RED] = red;
                pixels[i][j][GREEN] = green;
                pixels[i][j][BLUE] = blue;
            }
        }
        return pixels;
    }

    public static int[][][] invert(int[][][] pixels) {
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                pixels[i][j][RED] = 255 - pixels[i][j][RED];
                pixels[i][j][GREEN] = 255 - pixels[i][j][GREEN];
                pixels[i][j][BLUE] = 255 - pixels[i][j][BLUE];
            }
        }
        return pixels;
    }

    public static void main(String[] args) throws IOException {
        String filename = "resources/etsii.png";
        System.out.println("Loading " + filename);
        int[][][] pixels = loadImage(filename);
        System.out.println(pixels[0].length + "x" + pixels.length + " (width x height)");

        int[][] gray = toGray(pixels);
        System.out.println("Converted to grayscale");
        pixels = fromGray(gray);
        saveImage("resources/etsii-gray.png", pixels);
        System.out.println("Saved to resources/etsii-gray.png");

        int[][] blue = getChannel(pixels, BLUE);
        int[][][] blank = solidColor(pixels[0].length, pixels.length, 0, 0, 0);
        blank = setChannel(blank, blue, BLUE);
        saveImage("resources/etsii-blue.png", invert(blank));
        System.out.println("Saved blue channel to resources/etsii-blue.png");
    }

}