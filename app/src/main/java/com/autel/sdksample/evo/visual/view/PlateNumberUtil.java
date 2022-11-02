package com.autel.sdksample.evo.visual.view;


/**
 * @author R18380 2019-5-30
 * 车牌号转译工具类
 * 车牌识别结果用9个索引号表示，前8位为车牌字符信息，最后一位为车牌颜色
 *
 *   ———————————————————————————————————————————————
 *  |  索引位置	   |      对应字典
 *  |    1	       |     provinces
 *  |    2	       |     alphabets
 *  |   3-7	       |        ads
 *  |    8	       |     ads_8th(*表示该位置无字符)
 *  |    9	       |       color
 *   ———————————————————————————————————————————————
 *
 * 例1:
 * [0,0,25,25,25,25,25,25,1] 对应  皖A111111  green
 *
 * 例2：
 * [0,0,25,25,25,25,25,35,0] 对应  皖A11111  blue
 *
 */

public class PlateNumberUtil {

    private static final String [] provinces = {"皖", "沪", "津", "渝", "冀", "晋", "蒙", "辽", "吉", "黑", "苏", "浙", "京", "闽",
            "赣", "鲁", "豫", "鄂", "湘", "粤", "桂","琼", "川", "贵", "云", "藏", "陕", "甘", "青", "宁", "新", "警", "学", "O"};

    private static final String[] alphabets = {"A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W","X", "Y", "Z", "O"};

    private static final String[] ads = {"A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X","Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "O", "学", "挂"};

    private static final String[] ads_8th = {"A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X","Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "O", "*"};

    private static final String[] color = {"blue", "green", "yellow", "black"};

    public static String getPlateNumber(int[] arr){
        if (null == arr || arr.length != 16) {
            return "";
        }
        int length = arr.length;
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i<length; i++){
            if(i == 0){
                sb.append(provinces[arr[i]]);
            }else if(i == 1){
                sb.append(alphabets[arr[i]]);
            }else if(i == 7){
                if(!ads_8th[arr[i]].equals("*")){
                    sb.append(ads_8th[arr[i]]);
                }
            }else if( i == 8){
                sb.append("  ");
                sb.append(color[arr[i]]);
            }else{
                if (i >= 9) break;
                sb.append(ads[arr[i]]);
            }

        }

        return sb.toString();
    }
}
