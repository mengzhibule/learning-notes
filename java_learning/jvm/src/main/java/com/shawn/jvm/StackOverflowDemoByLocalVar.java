package com.shawn.jvm;

/**
 * 演示StackOverflowError错误 使用定义大量的本地变量表，增大此栈帧的本地变量表的长度
 */
public class StackOverflowDemoByLocalVar {

  private static long stackLength = 0;

  private static void test() {
    long l1, l2, l3, l4, l5, l6, l7, l8, l9, l10,
        l11, l12, l13, l14, l15, l16, l17, l18, l19, l20,
        l21, l22, l23, l24, l25, l26, l27, l28, l29, l30,
        l31, l32, l33, l34, l35, l36, l37, l38, l39, l40,
        l41, l42, l43, l44, l45, l46, l47, l48, l49, l50,
        l51, l52, l53, l54, l55, l56, l57, l58, l59, l60,
        l61, l62, l63, l64, l65, l66, l67, l68, l69, l70,
        l71, l72, l73, l74, l75, l76, l77, l78, l79, l80,
        l81, l82, l83, l84, l85, l86, l87, l88, l89, l90,
        l91, l92, l93, l94, l95, l96, l97, l98, l99, l100;
    stackLength++;
    test();

    l1 = l2 = l3 = l4 = l5 = l6 = l7 = l8 = l9 = l10 =
        l11 = l12 = l13 = l14 = l15 = l16 = l17 = l18 = l19 = l20 =
            l21 = l22 = l23 = l24 = l25 = l26 = l27 = l28 = l29 = l30 =
                l31 = l32 = l33 = l34 = l35 = l36 = l37 = l38 = l39 = l40 =
                    l41 = l42 = l43 = l44 = l45 = l46 = l47 = l48 = l49 = l50 =
                        l51 = l52 = l53 = l54 = l55 = l56 = l57 = l58 = l59 = l60 =
                            l61 = l62 = l63 = l64 = l65 = l66 = l67 = l68 = l69 = l70 =
                                l71 = l72 = l73 = l74 = l75 = l76 = l77 = l78 = l79 = l80 =
                                    l81 = l82 = l83 = l84 = l85 = l86 = l87 = l88 = l89 = l90 =
                                        l91 = l92 = l93 = l94 = l95 = l96 = l97 = l98 = l99 = l100 = 0;
  }

  public static void main(String[] args) {
    try{
      test();
    }catch (Throwable t){
      System.err.println("stack length: " + stackLength);
      throw t;
    }
  }

}
