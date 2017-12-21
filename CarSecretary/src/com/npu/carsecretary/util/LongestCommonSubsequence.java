package com.npu.carsecretary.util;


public class LongestCommonSubsequence
{
    public static int compute(char[] str1, char[] str2)
    {
        int substringLength1 = str1.length;
        int substringLength2 = str2.length;
 
        
        int[][] opt = new int[substringLength1 + 1][substringLength2 + 1];
 
        
        for (int i = substringLength1 - 1; i >= 0; i--)
        {
            for (int j = substringLength2 - 1; j >= 0; j--)
            {
                if (str1[i] == str2[j])
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                else
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
            }
        }
 
        int i = 0, j = 0;
        while (i < substringLength1 && j < substringLength2)
        {
            if (str1[i] == str2[j])
            {
                i++;
                j++;
            }
            else if (opt[i + 1][j] >= opt[i][j + 1])
                i++;
            else
                j++;
        }
        return opt[0][0];
    }
 
    public static int compute(String str1, String str2)
    {
        return compute(str1.toCharArray(), str2.toCharArray());
    }
    
}