package sc;

import java.awt.Color;
import java.lang.IllegalArgumentException;
import java.lang.IndexOutOfBoundsException;
import java.lang.Math;
import java.util.*;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private Picture pic;
    private int width;
    private int height;
    public Set<List<Integer>> protectedZone;
    public Set<List<Integer>> deletedZone;


    //Constructor
    public SeamCarver(Picture picture) {
        pic = new Picture(picture);
        width = pic.width();
        height = pic.height();
        protectedZone = new HashSet<>();
        deletedZone = new HashSet<>();
//        for(int i = 0;i<13;i++){
//            for(int j = 0; j<18;j++){
//                List<Integer> point = new ArrayList<>();
//                point.add(i+23);
//                point.add(j+160);
//                deletedZone.add(point);
//            }
//        }
    }


    //The picture contained in the sc data structure.
    public Picture picture() {
        return pic;
    }

    //Width of the picture
    public int width() {
        return width;
    }

    //Height of the picture
    public int height() {
        return height;
    }

    //Returns the energy of a pixel, given its coordinates.
    //Energy is a calculation of the contrast between a pixel's RGB values
    // and its neighboring pixels' RGB values.
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height())
            throw new IndexOutOfBoundsException();

        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
            return Math.pow(255.0, 2) * 3;
        }

        double deltaX = 0.0, deltaY = 0.0;
        Color x1, x2, y1, y2;
        x1 = pic.get(x - 1, y);
        x2 = pic.get(x + 1, y);
        y1 = pic.get(x, y - 1);
        y2 = pic.get(x, y + 1);
        deltaX = Math.pow((x1.getRed() - x2.getRed()), 2) + Math.pow((x1.getGreen() - x2.getGreen()), 2) + Math.pow((x1.getBlue() - x2.getBlue()), 2);
        deltaY = Math.pow((y1.getRed() - y2.getRed()), 2) + Math.pow((y1.getGreen() - y2.getGreen()), 2) + Math.pow((y1.getBlue() - y2.getBlue()), 2);
        if (!protectedZone.isEmpty() || !deletedZone.isEmpty()) {
            List<Integer> point = new ArrayList<>();
            point.add(x);
            point.add(y);
            if (protectedZone.contains(point))
                return 100000;
            else if (deletedZone.contains(point))
                return -100000;
            else
                return deltaX + deltaY;
        }
        return deltaX + deltaY;
    }

    private int[] getSeam(String mode, HashMap edgeTo, String end) {
        int size;
        if (mode.equals("h"))
            size = width();
        else if (mode.equals("v"))
            size = height();
        else
            throw new IllegalArgumentException();

        int[] path = new int[size];
        String cur = end;

        while (size > 0) {
            path[--size] = str2id(mode, cur);
            cur = (String) edgeTo.get(cur);
        }
        // path represents the seam as a 1D array of the coordinates in the seam.
        //y-coordinates are stored if the seam traverses horizontally.
        //x-coordinates are stored if the seam traverses vertically.
        return path;
    }

    private String id2str(int col, int row) {
        return col + " " + row;
    }

    private int str2id(String mode, String str) {
        if (mode.equals("v"))
            return Integer.parseInt(str.split(" ")[0]);
        else if (mode.equals("h"))
            return Integer.parseInt(str.split(" ")[1]);
        else
            throw new IllegalArgumentException();
    }

    // Loops through indices to find horizontal seam.
    public int[] findHorizontalSeam() {
        String mode = "h";
        HashMap<String, String> edgeTo = new HashMap<String, String>();
        HashMap<String, Double> energyTo = new HashMap<String, Double>();
        double cost = Double.MAX_VALUE;
        //cur represents the current pixel.
        //next represents a potential pixel to connect cur to.
        String cur, next, end = null;

        for (int col = 0; col < width() - 1; col++)
            for (int row = 0; row < height(); row++) {

                cur = id2str(col, row);
                if (col == 0) {
                    edgeTo.put(cur, null);
                    energyTo.put(cur, energy(col, row));
                }
                for (int i = row - 1; i <= row + 1; i++)
                    if (i >= 0 && i < height()) {
                        next = id2str(col + 1, i);
                        double newEng = energy(col + 1, i) + energyTo.get(cur);
                        //If we don't have a next edge yet, add one. Or, if this edge
                        // is better than the one we have, use it.
                        if (energyTo.get(next) == null || newEng < energyTo.get(next)) {

                            edgeTo.put(next, cur);
                            energyTo.put(next, newEng);

                            //End at the second to last column, because 'next' inolves
                            // the next column.
                            if (col + 1 == width() - 1 && newEng < cost) {
                                cost = newEng;
                                end = next;
                            }
                        }
                    }
            }
        return getSeam(mode, edgeTo, end);
    }

    // Loops through indices to find vertical seam.
    public int[] findVerticalSeam() {
        //See comments in findHorizontalSeam() for equivalent explanations.
        String mode = "v";
        HashMap<String, String> edgeTo = new HashMap<String, String>();
        HashMap<String, Double> energyTo = new HashMap<String, Double>();
        double cost = Double.MAX_VALUE;
        String cur, next, end = null;

        for (int row = 0; row < height() - 1; row++)
            for (int col = 0; col < width(); col++) {

                cur = id2str(col, row);
                if (row == 0) {
                    edgeTo.put(cur, null);
                    energyTo.put(cur, energy(col, row));
                }
                for (int k = col - 1; k <= col + 1; k++)
                    if (k >= 0 && k < width()) {
                        next = id2str(k, row + 1);
                        double newEng = energy(k, row + 1) + energyTo.get(cur);
                        if (energyTo.get(next) == null || newEng < energyTo.get(next)) {

                            edgeTo.put(next, cur);
                            energyTo.put(next, newEng);
                            if (row + 1 == height() - 1 && newEng < cost) {
                                cost = newEng;
                                end = next;
                            }
                        }
                    }
            }
        return getSeam(mode, edgeTo, end);
    }

    private boolean isValidSeam(int[] seam) {
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                return false;
            }
        }
        return true;
    }

    // Removes horizontal seam from picture.
    public void removeHorizontalSeam(int[] seam) {
        if (width() <= 1 || height() <= 1 || seam.length < 0 || seam.length > width() || !isValidSeam(seam))
            throw new IllegalArgumentException();

        Picture newPic = new Picture(width(), height() - 1);

        for (int col = 0; col < width(); col++)
            for (int row = 0; row < height() - 1; row++) {

                if (row < seam[col])
                    newPic.set(col, row, pic.get(col, row));
                else
                    newPic.set(col, row, pic.get(col, row + 1));

            }
        height--;
        pic = new Picture(newPic);
        updateProtectedZoneAfterHorizontalRemoval(seam);
    }

    private void updateProtectedZoneAfterHorizontalRemoval(int[] seam) {
        Set<List<Integer>> updatedProtectedZone = new HashSet<>();
        for (List<Integer> point : protectedZone) {
            int x = point.get(0), y = point.get(1);
            for (int i = 0; i < seam.length; i++) {
                if (x == i) {
                    if (y < seam[i]) {
                        List<Integer> updatedPoint = new ArrayList<>(Arrays.asList(x, y));
                        updatedProtectedZone.add(updatedPoint);
                    } else if (y > seam[i]) {
                        y -= 1;
                        List<Integer> updatedPoint = new ArrayList<>(Arrays.asList(x, y));
                        updatedProtectedZone.add(updatedPoint);
                    }
                }
            }
        }
        protectedZone = updatedProtectedZone;
    }

    public void addHorizontalSeam(int[] seam) {
        if (width() <= 1 || height() <= 1 || seam.length < 0 || seam.length > width() || !isValidSeam(seam))
            throw new IllegalArgumentException();

        Picture newPic = new Picture(width(), height() + 1);

        for (int col = 0; col < width(); col++)
            for (int row = 0; row < height(); row++) {

                if (row < seam[col])
                    newPic.set(col, row, pic.get(col, row));
                else if (row == seam[col]) {
                    int red1 = pic.get(col, row - 1).getRed();
                    int red2 = pic.get(col, row).getRed();
                    int red3 = pic.get(col, row + 1).getRed();
                    int green1 = pic.get(col, row - 1).getGreen();
                    int green2 = pic.get(col, row).getGreen();
                    int green3 = pic.get(col, row + 1).getGreen();
                    int blue1 = pic.get(col, row - 1).getBlue();
                    int blue2 = pic.get(col, row).getBlue();
                    int blue3 = pic.get(col, row + 1).getBlue();
                    if (row == 0) {
                        Color color = new Color((red2 + red3) / 2, (green2 + green3) / 2, (blue2 + blue3) / 2);
                        newPic.set(col, row, color);
                        newPic.set(col, row + 1, color);
                    } else if (row == height - 1) {
                        Color color = new Color((red2 + red1) / 2, (green2 + green1) / 2, (blue2 + blue1) / 2);
                        newPic.set(col, row, color);
                        newPic.set(col, row + 1, color);
                    } else {
                        Color color = new Color((red2 + red1 + red3) / 3, (green2 + green1 + green3) / 3, (blue2 + blue1 + blue3) / 3);
                        newPic.set(col, row, color);
                        newPic.set(col, row + 1, color);
                    }
                } else newPic.set(col, row + 1, pic.get(col, row));
            }

        height++;
        pic = new Picture(newPic);
    }

    // Removes vertical seam from picture.
    public void removeVerticalSeam(int[] seam) {
        if (width() <= 1 || height() <= 1 || seam.length < 0 || seam.length > height() || !isValidSeam(seam))
            throw new IllegalArgumentException();

        Picture newPic = new Picture(width() - 1, height());

        for (int row = 0; row < height(); row++)
            for (int col = 0; col < width() - 1; col++) {
                if (col < seam[row])
                    newPic.set(col, row, pic.get(col, row));
                else
                    newPic.set(col, row, pic.get(col + 1, row));

            }

        width--;
        pic = new Picture(newPic);
        updateProtectedZoneAfterVerticalRemoval(seam);
    }

    private void updateProtectedZoneAfterVerticalRemoval(int[] seam) {
        Set<List<Integer>> updatedProtectedZone = new HashSet<>();
        for (List<Integer> point : protectedZone) {
            int x = point.get(0), y = point.get(1);
            for (int i = 0; i < seam.length; i++) {
                if (y == i) {
                    if (x < seam[i]) {
                        List<Integer> updatedPoint = new ArrayList<>(Arrays.asList(x, y));
                        updatedProtectedZone.add(updatedPoint);
                    } else if (x > seam[i]) {
                        x -= 1;
                        List<Integer> updatedPoint = new ArrayList<>(Arrays.asList(x, y));
                        updatedProtectedZone.add(updatedPoint);
                    }
                }
            }
        }
        protectedZone = new HashSet<>(updatedProtectedZone);
    }

    public void addVerticalSeam(int[] seam) {
        if (width() <= 1 || height() <= 1 || seam.length < 0 || seam.length > height() || !isValidSeam(seam))
            throw new IllegalArgumentException();

        Picture newPic = new Picture(width() + 1, height());

        for (int row = 0; row < height(); row++)
            for (int col = 0; col < width(); col++) {

                if (col < seam[row])
                    newPic.set(col, row, pic.get(col, row));
                else if (col == seam[row]) {
                    int red1 = pic.get(col - 1, row).getRed();
                    int red2 = pic.get(col, row).getRed();
                    int red3 = pic.get(col + 1, row).getRed();
                    int green1 = pic.get(col - 1, row).getGreen();
                    int green2 = pic.get(col, row).getGreen();
                    int green3 = pic.get(col + 1, row).getGreen();
                    int blue1 = pic.get(col - 1, row).getBlue();
                    int blue2 = pic.get(col, row).getBlue();
                    int blue3 = pic.get(col + 1, row).getBlue();
                    if (col == 0) {
                        Color color = new Color((red2 + red3) / 2, (green2 + green3) / 2, (blue2 + blue3) / 2);
                        newPic.set(col, row, color);
                        newPic.set(col + 1, row, color);
                    } else if (col == width - 1) {
                        Color color = new Color((red2 + red1) / 2, (green2 + green1) / 2, (blue2 + blue1) / 2);
                        newPic.set(col, row, color);
                        newPic.set(col + 1, row, color);
                    } else {
                        Color color = new Color((red2 + red1 + red3) / 3, (green2 + green1 + green3) / 3, (blue2 + blue1 + blue3) / 3);
                        newPic.set(col, row, color);
                        newPic.set(col + 1, row, color);
                    }
                } else newPic.set(col + 1, row, pic.get(col, row));
            }
        width++;
        pic = new Picture(newPic);
    }

    public void insertVerticalSeams(double multiple) {
        int k = (int) ((multiple - 1) * this.width());
        int[][] seams = new int[k][this.height];  //用二维数组，储存k条seams，每条seam都是一个一维数组
        for (int i = 0; i < k; i++) {
            seams[i] = findVerticalSeam().clone();
            for (int j = 0; j < this.height; j++) {
                List<Integer> p = new ArrayList<>();      //p代表seams[i]中各个点的坐标
                p.add(seams[i][j]);
                p.add(j);
                protectedZone.add(p);
            }
        }
        for (int i = 0; i < k; i++) {
            this.addVerticalSeam(seams[i]);
            System.out.println(this.width);
        }
    }

    public void insertHorizontalSeams(double multiple) {
        int k = (int) ((multiple - 1) * this.height());
        int[][] seams = new int[k][this.width];  //用二维数组，储存k条seams，每条seam都是一个一维数组
        for (int i = 0; i < k; i++) {
            seams[i] = findHorizontalSeam().clone();
            for (int j = 0; j < this.width; j++) {
                List<Integer> p = new ArrayList<>();      //p代表seams[i]中各个点的坐标
                p.add(seams[i][j]);
                p.add(j);
                protectedZone.add(p);
            }
        }
        for (int i = 0; i < k; i++) {
            this.addHorizontalSeam(seams[i]);
            System.out.println(this.height);
        }
    }

    // Resizes the picture to a specified width or height.
    public Picture resizeTo(Boolean isHorizontal, double multiple) {

        // Resize the width; remove vertical seams.
        if (isHorizontal) {
            double dimension = this.width * multiple;
            if (this.width() > dimension) {
                while (this.width() > dimension) {
                    System.out.println("resizing... Currently at width " +
                            this.width());
                    int[] seam = this.findVerticalSeam();
                    this.removeVerticalSeam(seam);
                }
            } else {
                System.out.println("Current width = " + this.width);
                this.insertVerticalSeams(multiple);
            }
        }
        // Resize the height; remove horizontal seams.
        else {
            double dimension = this.height * multiple;
            if (this.height() > dimension) {
                while (this.height() > dimension) {
                    System.out.println("resizing... Currently at height " +
                            this.height());
                    int[] seam = this.findHorizontalSeam();
                    this.removeHorizontalSeam(seam);
                }
            } else {
                System.out.println("Current height = " + this.height);
                this.insertHorizontalSeams(multiple);
            }
        }
        // Return the resized image.
        return this.picture();

    }

}
