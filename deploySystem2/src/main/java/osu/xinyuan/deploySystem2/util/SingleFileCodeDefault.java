package osu.xinyuan.deploySystem2.util;

public class SingleFileCodeDefault {
    public static String defaultJavaCode() {
        return "// \"static void main\" must be defined in a public class.\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello World!\");\n" +
                "    }\n" +
                "}";
    }
}
