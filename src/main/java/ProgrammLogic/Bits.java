package ProgrammLogic;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

public class Bits {
    public static boolean[] getBits(long n,int size){
        boolean[] res=new boolean[size];
        for (int i=0;i<size;i++){
            if((n & 1) == 1){
                res[size-i-1] = true;
            }else{
                res[size-i-1] = false;
            }
            n = n >> 1;
        }
        return res;
    }
}
