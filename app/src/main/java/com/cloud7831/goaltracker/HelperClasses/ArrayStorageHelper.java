package com.cloud7831.goaltracker.HelperClasses;

import androidx.annotation.NonNull;

public final class ArrayStorageHelper {

    /**
     * Takes in a specially formatted String and converts it to an int[]. Used to store int[] as
     * a string in Room Database.
     * @param list formatted as "size-#-#...#-#"
     * @return int[] of size "size" with the values specified in the string.
     */
    public static int[] convertStrArrToInt(String list) {
        // Lists should have the format #-#-... #-#
        // where the first number is the length of the array and a '-' separates numbers.

        String[] args = list.split("-"); // split by the '-' character
        int[] arr = new int[Integer.parseInt(args[0])];
        if(arr.length > 0){
            for(int i = 1; i < args.length; i++){
                arr[i-1] = Integer.parseInt(args[i]);
            }
        }
        return arr;
    }

    /**
     * Takes in an int[] and returns a string in the format: size-#-#...#-#
     * @param arr int[] >= 0
     * @return "0" for empty arr or "size-#-#...#-#"
     */
    public static String convertIntArrToStr(@NonNull int[] arr){
        StringBuilder list = new StringBuilder();

        // Store the length of the arr as the first value.
        list.append(Integer.toString(arr.length));

        if(arr.length > 0){
            // append the elements to the string in the format:
            // size-#-#-#...#-#
            for (int value : arr) {
                list.append("-"); // Use the '-' as a separator.
                list.append(Integer.toString(value));
            }
        }

        return list.toString();
    }

}
